#!/usr/bin/env python3
"""
LinkedIn Company Jobs Scraper

Fetches job listings from LinkedIn company pages.
Handles two approaches:
1. Direct API calls (if accessible)
2. Playwright browser automation (for authenticated scraping)

Usage:
    python linkedin_job_scraper.py --company intuit
    python linkedin_job_scraper.py --url "https://www.linkedin.com/company/intuit/jobs/"
"""

import argparse
import json
import sys
import re
from typing import List, Dict, Optional
from urllib.parse import urlparse, parse_qs

try:
    import requests
except ImportError:
    print("Error: requests library not found. Install: pip install requests")
    sys.exit(1)


class LinkedInJobScraper:
    """Scraper for LinkedIn company job listings"""

    def __init__(self, session_cookie: Optional[str] = None):
        """
        Initialize scraper

        Args:
            session_cookie: LinkedIn li_at cookie for authenticated requests (optional)
        """
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',
            'Accept': 'application/json, text/plain, */*',
            'Accept-Language': 'en-US,en;q=0.9',
            'Referer': 'https://www.linkedin.com/',
        })

        if session_cookie:
            self.session.cookies.set('li_at', session_cookie, domain='.linkedin.com')

    def extract_company_id(self, company_url: str) -> Optional[str]:
        """
        Extract company ID from LinkedIn URL

        Args:
            company_url: LinkedIn company URL (e.g., https://www.linkedin.com/company/intuit/)

        Returns:
            Company slug (e.g., 'intuit')
        """
        # Pattern: /company/{company_slug}/
        match = re.search(r'/company/([^/]+)', company_url)
        if match:
            return match.group(1)
        return None

    def fetch_jobs_via_api(self, company_slug: str, start: int = 0, count: int = 25) -> Dict:
        """
        Attempt to fetch jobs via LinkedIn's internal API

        Args:
            company_slug: Company identifier (e.g., 'intuit')
            start: Starting position for pagination
            count: Number of jobs to fetch

        Returns:
            API response data
        """
        # LinkedIn's jobs API endpoint (may require authentication)
        api_url = f"https://www.linkedin.com/jobs-guest/jobs/api/seeMoreJobPostings/search"

        params = {
            'f_C': company_slug,  # Company filter
            'start': start,
            'count': count,
            'sortBy': 'DD',  # Sort by date descending
        }

        try:
            response = self.session.get(api_url, params=params, timeout=10)
            response.raise_for_status()

            # Check if we got HTML (authwall) or JSON
            content_type = response.headers.get('Content-Type', '')
            if 'application/json' in content_type:
                return response.json()
            elif 'text/html' in content_type:
                # Got HTML instead of JSON - likely authwall
                if 'authwall' in response.text.lower():
                    return {
                        'error': 'authentication_required',
                        'message': 'LinkedIn requires authentication to access jobs API'
                    }
                # Try to parse HTML for job listings
                return self._parse_html_jobs(response.text)
            else:
                return {
                    'error': 'unexpected_content_type',
                    'content_type': content_type
                }
        except requests.RequestException as e:
            return {
                'error': 'request_failed',
                'message': str(e)
            }

    def _parse_html_jobs(self, html: str) -> Dict:
        """
        Parse job listings from HTML response (guest/public view)

        Args:
            html: HTML content from LinkedIn jobs page

        Returns:
            Parsed job data
        """
        # LinkedIn embeds job data in HTML as JSON-LD or in script tags
        jobs = []

        # Try to find JSON-LD structured data
        json_ld_pattern = r'<script type="application/ld\+json">(.*?)</script>'
        matches = re.findall(json_ld_pattern, html, re.DOTALL)

        for match in matches:
            try:
                data = json.loads(match)
                if isinstance(data, dict) and data.get('@type') == 'JobPosting':
                    jobs.append({
                        'id': data.get('identifier', {}).get('value'),
                        'title': data.get('title'),
                        'company': data.get('hiringOrganization', {}).get('name'),
                        'location': data.get('jobLocation', {}).get('address', {}).get('addressLocality'),
                        'datePosted': data.get('datePosted'),
                        'description': data.get('description'),
                        'employmentType': data.get('employmentType'),
                        'url': data.get('url'),
                    })
                elif isinstance(data, list):
                    for item in data:
                        if isinstance(item, dict) and item.get('@type') == 'JobPosting':
                            jobs.append({
                                'id': item.get('identifier', {}).get('value'),
                                'title': item.get('title'),
                                'company': item.get('hiringOrganization', {}).get('name'),
                                'location': item.get('jobLocation', {}).get('address', {}).get('addressLocality'),
                                'datePosted': item.get('datePosted'),
                                'description': item.get('description'),
                                'employmentType': item.get('employmentType'),
                                'url': item.get('url'),
                            })
            except json.JSONDecodeError:
                continue

        # Also try to find job list containers in HTML
        # LinkedIn uses class names like "base-card" for job cards
        job_card_pattern = r'data-entity-urn="urn:li:jobPosting:(\d+)"'
        job_ids = re.findall(job_card_pattern, html)

        if not jobs and job_ids:
            # Found job IDs but no structured data
            return {
                'partial': True,
                'job_ids': job_ids,
                'message': 'Found job IDs but need authenticated access for full details'
            }

        return {
            'jobs': jobs,
            'count': len(jobs),
            'source': 'html_parsing'
        }

    def fetch_all_jobs(self, company_slug: str, max_jobs: int = 100) -> List[Dict]:
        """
        Fetch all available jobs for a company with pagination

        Args:
            company_slug: Company identifier
            max_jobs: Maximum number of jobs to fetch

        Returns:
            List of job dictionaries
        """
        all_jobs = []
        start = 0
        page_size = 25

        while start < max_jobs:
            result = self.fetch_jobs_via_api(company_slug, start=start, count=page_size)

            if 'error' in result:
                print(f"Error fetching jobs: {result.get('message', result['error'])}", file=sys.stderr)
                break

            jobs = result.get('jobs', [])
            if not jobs:
                # Check if we got partial data
                if result.get('partial') and result.get('job_ids'):
                    print(f"Found {len(result['job_ids'])} job IDs but authentication required for details",
                          file=sys.stderr)
                break

            all_jobs.extend(jobs)

            # Check if we got fewer jobs than requested (last page)
            if len(jobs) < page_size:
                break

            start += page_size

        return all_jobs


def main():
    parser = argparse.ArgumentParser(description='Scrape LinkedIn company job listings')
    parser.add_argument('--company', help='Company slug (e.g., intuit)')
    parser.add_argument('--url', help='Full LinkedIn company jobs URL')
    parser.add_argument('--cookie', help='LinkedIn li_at session cookie for authenticated access')
    parser.add_argument('--max-jobs', type=int, default=100, help='Maximum number of jobs to fetch')
    parser.add_argument('--output', help='Output JSON file path')

    args = parser.parse_args()

    # Determine company slug
    company_slug = args.company
    if not company_slug and args.url:
        scraper = LinkedInJobScraper()
        company_slug = scraper.extract_company_id(args.url)
        if not company_slug:
            print(f"Error: Could not extract company ID from URL: {args.url}", file=sys.stderr)
            sys.exit(1)

    if not company_slug:
        parser.print_help()
        sys.exit(1)

    # Initialize scraper
    scraper = LinkedInJobScraper(session_cookie=args.cookie)

    # Fetch jobs
    print(f"Fetching jobs for company: {company_slug}", file=sys.stderr)
    jobs = scraper.fetch_all_jobs(company_slug, max_jobs=args.max_jobs)

    # Prepare output
    output_data = {
        'company': company_slug,
        'total_jobs': len(jobs),
        'jobs': jobs
    }

    # Output results
    if args.output:
        with open(args.output, 'w') as f:
            json.dump(output_data, f, indent=2)
        print(f"Saved {len(jobs)} jobs to {args.output}", file=sys.stderr)
    else:
        print(json.dumps(output_data, indent=2))

    # Print summary
    print(f"\nSummary: Found {len(jobs)} jobs for {company_slug}", file=sys.stderr)
    if jobs:
        print("\nSample jobs:", file=sys.stderr)
        for job in jobs[:3]:
            print(f"  - {job.get('title')} ({job.get('location')})", file=sys.stderr)


if __name__ == '__main__':
    main()
