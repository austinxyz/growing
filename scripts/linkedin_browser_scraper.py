#!/usr/bin/env python3
"""
LinkedIn Company Jobs Scraper with Browser Automation

Uses Playwright to scrape job listings from LinkedIn company pages.
Handles JavaScript rendering and can work with authenticated sessions.

Installation:
    pip install playwright
    playwright install chromium

Usage:
    # Guest access (limited)
    python linkedin_browser_scraper.py --company intuit --output jobs.json

    # Authenticated access (requires manual login first time)
    python linkedin_browser_scraper.py --company intuit --auth --output jobs.json
"""

import argparse
import json
import sys
import time
from typing import List, Dict
from pathlib import Path

try:
    from playwright.sync_api import sync_playwright, TimeoutError as PlaywrightTimeout
except ImportError:
    print("Error: Playwright not installed. Run: pip install playwright && playwright install chromium")
    sys.exit(1)


class LinkedInBrowserScraper:
    """Browser-based LinkedIn job scraper using Playwright"""

    def __init__(self, headless: bool = True, auth_storage: str = None):
        """
        Initialize browser scraper

        Args:
            headless: Run browser in headless mode
            auth_storage: Path to save/load authentication state
        """
        self.headless = headless
        self.auth_storage = auth_storage
        self.playwright = None
        self.browser = None
        self.context = None
        self.page = None

    def __enter__(self):
        self.playwright = sync_playwright().start()

        # Launch browser
        self.browser = self.playwright.chromium.launch(headless=self.headless)

        # Create context with optional auth state
        context_options = {
            'viewport': {'width': 1280, 'height': 720},
            'user_agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        }

        if self.auth_storage and Path(self.auth_storage).exists():
            context_options['storage_state'] = self.auth_storage
            print(f"Loaded authentication from {self.auth_storage}", file=sys.stderr)

        self.context = self.browser.new_context(**context_options)
        self.page = self.context.new_page()

        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        if self.page:
            self.page.close()
        if self.context:
            self.context.close()
        if self.browser:
            self.browser.close()
        if self.playwright:
            self.playwright.stop()

    def login_if_needed(self, timeout: int = 60):
        """
        Check if login is required and wait for manual login

        Args:
            timeout: Maximum time to wait for login (seconds)
        """
        self.page.goto('https://www.linkedin.com', wait_until='networkidle')

        # Check if we're on login page
        if 'authwall' in self.page.url or 'login' in self.page.url:
            print("\n" + "="*60, file=sys.stderr)
            print("LinkedIn requires authentication.", file=sys.stderr)
            print("Please log in manually in the browser window.", file=sys.stderr)
            print("Waiting for login to complete...", file=sys.stderr)
            print("="*60 + "\n", file=sys.stderr)

            # Wait for navigation away from login page
            try:
                self.page.wait_for_url(
                    lambda url: 'authwall' not in url and 'login' not in url,
                    timeout=timeout * 1000
                )
                print("Login successful!", file=sys.stderr)

                # Save authentication state
                if self.auth_storage:
                    self.context.storage_state(path=self.auth_storage)
                    print(f"Saved authentication to {self.auth_storage}", file=sys.stderr)

            except PlaywrightTimeout:
                print("Login timeout. Proceeding anyway...", file=sys.stderr)

    def scrape_company_jobs(self, company_slug: str, max_jobs: int = 100) -> List[Dict]:
        """
        Scrape job listings from company jobs page

        Args:
            company_slug: Company identifier (e.g., 'intuit')
            max_jobs: Maximum number of jobs to scrape

        Returns:
            List of job dictionaries
        """
        jobs = []
        url = f"https://www.linkedin.com/company/{company_slug}/jobs/"

        print(f"Navigating to {url}", file=sys.stderr)
        self.page.goto(url, wait_until='networkidle')

        # Check if we hit authwall
        if 'authwall' in self.page.url:
            print("Authentication required. Attempting login...", file=sys.stderr)
            self.login_if_needed()
            # Navigate to jobs page again
            self.page.goto(url, wait_until='networkidle')

        # Wait for job listings to load
        try:
            # LinkedIn uses various selectors for job cards
            self.page.wait_for_selector('.jobs-search__results-list, .scaffold-layout__list-container', timeout=10000)
        except PlaywrightTimeout:
            print("Warning: Job list container not found. Page may require login.", file=sys.stderr)
            return jobs

        # Scroll to load more jobs
        last_height = 0
        scroll_attempts = 0
        max_scrolls = max_jobs // 25 + 1  # LinkedIn typically loads 25 jobs per scroll

        while scroll_attempts < max_scrolls:
            # Scroll to bottom
            self.page.evaluate('window.scrollTo(0, document.body.scrollHeight)')
            time.sleep(2)  # Wait for new jobs to load

            # Check if new content loaded
            new_height = self.page.evaluate('document.body.scrollHeight')
            if new_height == last_height:
                break

            last_height = new_height
            scroll_attempts += 1
            print(f"Scrolled {scroll_attempts} times...", file=sys.stderr)

        # Extract job cards
        job_cards = self.page.query_selector_all('.job-search-card, .jobs-search-results__list-item')
        print(f"Found {len(job_cards)} job cards", file=sys.stderr)

        for card in job_cards[:max_jobs]:
            try:
                job = self._extract_job_from_card(card)
                if job:
                    jobs.append(job)
            except Exception as e:
                print(f"Error extracting job: {e}", file=sys.stderr)
                continue

        return jobs

    def _extract_job_from_card(self, card) -> Dict:
        """
        Extract job information from a job card element

        Args:
            card: Playwright element handle for job card

        Returns:
            Job dictionary
        """
        job = {}

        # Extract job title
        title_elem = card.query_selector('.job-search-card__title, .base-search-card__title')
        if title_elem:
            job['title'] = title_elem.inner_text().strip()

        # Extract company name
        company_elem = card.query_selector('.job-search-card__company-name, .base-search-card__subtitle')
        if company_elem:
            job['company'] = company_elem.inner_text().strip()

        # Extract location
        location_elem = card.query_selector('.job-search-card__location, .job-search-card__location-name')
        if location_elem:
            job['location'] = location_elem.inner_text().strip()

        # Extract job link
        link_elem = card.query_selector('a[href*="/jobs/view/"]')
        if link_elem:
            href = link_elem.get_attribute('href')
            if href:
                job['url'] = href if href.startswith('http') else f"https://www.linkedin.com{href}"
                # Extract job ID from URL
                import re
                match = re.search(r'/jobs/view/(\d+)', href)
                if match:
                    job['id'] = match.group(1)

        # Extract posted date
        date_elem = card.query_selector('.job-search-card__listdate, time')
        if date_elem:
            job['datePosted'] = date_elem.get_attribute('datetime') or date_elem.inner_text().strip()

        # Extract job metadata (employment type, seniority, etc.)
        metadata_elem = card.query_selector('.job-search-card__metadata')
        if metadata_elem:
            job['metadata'] = metadata_elem.inner_text().strip()

        return job if job.get('title') else None


def main():
    parser = argparse.ArgumentParser(description='Scrape LinkedIn company jobs using browser automation')
    parser.add_argument('--company', required=True, help='Company slug (e.g., intuit)')
    parser.add_argument('--max-jobs', type=int, default=100, help='Maximum number of jobs to fetch')
    parser.add_argument('--output', help='Output JSON file path')
    parser.add_argument('--auth', action='store_true', help='Enable authenticated mode (manual login)')
    parser.add_argument('--headless', action='store_true', help='Run browser in headless mode')
    parser.add_argument('--auth-storage', default='linkedin_auth.json',
                       help='Path to save/load authentication state')

    args = parser.parse_args()

    # Use headless mode by default, unless --auth is specified (needs visible browser for login)
    headless = args.headless or not args.auth
    auth_storage = args.auth_storage if args.auth else None

    # Scrape jobs
    with LinkedInBrowserScraper(headless=headless, auth_storage=auth_storage) as scraper:
        # Login if needed (only in auth mode)
        if args.auth:
            scraper.login_if_needed()

        # Scrape jobs
        print(f"\nScraping jobs for {args.company}...", file=sys.stderr)
        jobs = scraper.scrape_company_jobs(args.company, max_jobs=args.max_jobs)

    # Prepare output
    output_data = {
        'company': args.company,
        'total_jobs': len(jobs),
        'scraped_at': time.strftime('%Y-%m-%d %H:%M:%S'),
        'jobs': jobs
    }

    # Output results
    if args.output:
        with open(args.output, 'w', encoding='utf-8') as f:
            json.dump(output_data, f, indent=2, ensure_ascii=False)
        print(f"\nSaved {len(jobs)} jobs to {args.output}", file=sys.stderr)
    else:
        print(json.dumps(output_data, indent=2, ensure_ascii=False))

    # Print summary
    print(f"\n{'='*60}", file=sys.stderr)
    print(f"Summary: Found {len(jobs)} jobs for {args.company}", file=sys.stderr)
    if jobs:
        print("\nSample jobs:", file=sys.stderr)
        for job in jobs[:5]:
            print(f"  - {job.get('title')}", file=sys.stderr)
            print(f"    Location: {job.get('location')}", file=sys.stderr)
            print(f"    URL: {job.get('url', 'N/A')}", file=sys.stderr)
            print()
    print("="*60, file=sys.stderr)


if __name__ == '__main__':
    main()
