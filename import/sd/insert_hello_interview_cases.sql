-- ============================================================
-- HelloInterview 系统设计典型案例导入
-- ============================================================
-- 来源: https://www.hellointerview.com/learn/system-design/practice
-- 共27个案例: Easy(4), Medium(12), Hard(11)
--
-- 插入规则:
-- 1. system_design_cases表: 如果案例已存在(按title匹配)，跳过插入
-- 2. case_resources表: 插入对应的学习资源URL
-- 3. created_by_user_id = 3 (管理员用户)
-- 4. skill_id = 2 (系统设计)
-- ============================================================

-- ============================================================
-- Easy 难度案例 (4个)
-- ============================================================

-- Case 1: Design Bit.ly
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Bit.ly', 'EASY', 3, 1, 1
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Bit.ly'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Bit.ly' LIMIT 1),
    'ARTICLE',
    'Design Bit.ly',
    'https://www.hellointerview.com/learn/system-design/answer-keys/bit.ly',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Bit.ly')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Bit.ly' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/bit.ly'
);

-- Case 2: Design Dropbox
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Dropbox', 'EASY', 3, 1, 2
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Dropbox'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Dropbox' LIMIT 1),
    'ARTICLE',
    'Design Dropbox',
    'https://www.hellointerview.com/learn/system-design/answer-keys/dropbox',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Dropbox')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Dropbox' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/dropbox'
);

-- Case 3: Design a Local Delivery Service
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design a Local Delivery Service', 'EASY', 3, 1, 3
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design a Local Delivery Service'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design a Local Delivery Service' LIMIT 1),
    'ARTICLE',
    'Design a Local Delivery Service',
    'https://www.hellointerview.com/learn/system-design/answer-keys/a-local-delivery-service',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design a Local Delivery Service')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design a Local Delivery Service' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/a-local-delivery-service'
);

-- Case 4: Design a News Aggregator
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design a News Aggregator', 'EASY', 3, 1, 4
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design a News Aggregator'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design a News Aggregator' LIMIT 1),
    'ARTICLE',
    'Design a News Aggregator',
    'https://www.hellointerview.com/learn/system-design/answer-keys/a-news-aggregator',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design a News Aggregator')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design a News Aggregator' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/a-news-aggregator'
);

-- ============================================================
-- Medium 难度案例 (12个)
-- ============================================================

-- Case 5: Design Ticketmaster
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Ticketmaster', 'MEDIUM', 3, 1, 5
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Ticketmaster'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Ticketmaster' LIMIT 1),
    'ARTICLE',
    'Design Ticketmaster',
    'https://www.hellointerview.com/learn/system-design/answer-keys/ticketmaster',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Ticketmaster')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Ticketmaster' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/ticketmaster'
);

-- Case 6: Design FB News Feed
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design FB News Feed', 'MEDIUM', 3, 1, 6
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design FB News Feed'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design FB News Feed' LIMIT 1),
    'ARTICLE',
    'Design FB News Feed',
    'https://www.hellointerview.com/learn/system-design/answer-keys/fb-news-feed',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design FB News Feed')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design FB News Feed' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/fb-news-feed'
);

-- Case 7: Design Tinder
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Tinder', 'MEDIUM', 3, 1, 7
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Tinder'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Tinder' LIMIT 1),
    'ARTICLE',
    'Design Tinder',
    'https://www.hellointerview.com/learn/system-design/answer-keys/tinder',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Tinder')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Tinder' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/tinder'
);

-- Case 8: Design LeetCode
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design LeetCode', 'MEDIUM', 3, 1, 8
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design LeetCode'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design LeetCode' LIMIT 1),
    'ARTICLE',
    'Design LeetCode',
    'https://www.hellointerview.com/learn/system-design/answer-keys/leetcode',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design LeetCode')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design LeetCode' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/leetcode'
);

-- Case 9: Design WhatsApp
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design WhatsApp', 'MEDIUM', 3, 1, 9
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design WhatsApp'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design WhatsApp' LIMIT 1),
    'ARTICLE',
    'Design WhatsApp',
    'https://www.hellointerview.com/learn/system-design/answer-keys/whatsapp',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design WhatsApp')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design WhatsApp' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/whatsapp'
);

-- Case 10: Design Yelp
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Yelp', 'MEDIUM', 3, 1, 10
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Yelp'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Yelp' LIMIT 1),
    'ARTICLE',
    'Design Yelp',
    'https://www.hellointerview.com/learn/system-design/answer-keys/yelp',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Yelp')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Yelp' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/yelp'
);

-- Case 11: Design Strava
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Strava', 'MEDIUM', 3, 1, 11
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Strava'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Strava' LIMIT 1),
    'ARTICLE',
    'Design Strava',
    'https://www.hellointerview.com/learn/system-design/answer-keys/strava',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Strava')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Strava' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/strava'
);

-- Case 12: Design a Rate Limiter
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design a Rate Limiter', 'MEDIUM', 3, 1, 12
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design a Rate Limiter'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design a Rate Limiter' LIMIT 1),
    'ARTICLE',
    'Design a Rate Limiter',
    'https://www.hellointerview.com/learn/system-design/answer-keys/a-rate-limiter',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design a Rate Limiter')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design a Rate Limiter' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/a-rate-limiter'
);

-- Case 13: Design Online Auction
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Online Auction', 'MEDIUM', 3, 1, 13
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Online Auction'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Online Auction' LIMIT 1),
    'ARTICLE',
    'Design Online Auction',
    'https://www.hellointerview.com/learn/system-design/answer-keys/online-auction',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Online Auction')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Online Auction' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/online-auction'
);

-- Case 14: Design FB Live Comments
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design FB Live Comments', 'MEDIUM', 3, 1, 14
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design FB Live Comments'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design FB Live Comments' LIMIT 1),
    'ARTICLE',
    'Design FB Live Comments',
    'https://www.hellointerview.com/learn/system-design/answer-keys/fb-live-comments',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design FB Live Comments')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design FB Live Comments' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/fb-live-comments'
);

-- Case 15: Design FB Post Search
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design FB Post Search', 'MEDIUM', 3, 1, 15
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design FB Post Search'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design FB Post Search' LIMIT 1),
    'ARTICLE',
    'Design FB Post Search',
    'https://www.hellointerview.com/learn/system-design/answer-keys/fb-post-search',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design FB Post Search')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design FB Post Search' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/fb-post-search'
);

-- Case 16: Design a Price Tracking Service
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design a Price Tracking Service', 'MEDIUM', 3, 1, 16
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design a Price Tracking Service'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design a Price Tracking Service' LIMIT 1),
    'ARTICLE',
    'Design a Price Tracking Service',
    'https://www.hellointerview.com/learn/system-design/answer-keys/a-price-tracking-service',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design a Price Tracking Service')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design a Price Tracking Service' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/a-price-tracking-service'
);

-- ============================================================
-- Hard 难度案例 (11个)
-- ============================================================

-- Case 17: Design Instagram
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Instagram', 'HARD', 3, 1, 17
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Instagram'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Instagram' LIMIT 1),
    'ARTICLE',
    'Design Instagram',
    'https://www.hellointerview.com/learn/system-design/answer-keys/instagram',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Instagram')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Instagram' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/instagram'
);

-- Case 18: Design YouTube Top K
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design YouTube Top K', 'HARD', 3, 1, 18
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design YouTube Top K'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design YouTube Top K' LIMIT 1),
    'ARTICLE',
    'Design YouTube Top K',
    'https://www.hellointerview.com/learn/system-design/answer-keys/youtube-top-k',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design YouTube Top K')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design YouTube Top K' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/youtube-top-k'
);

-- Case 19: Design Uber
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Uber', 'HARD', 3, 1, 19
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Uber'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Uber' LIMIT 1),
    'ARTICLE',
    'Design Uber',
    'https://www.hellointerview.com/learn/system-design/answer-keys/uber',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Uber')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Uber' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/uber'
);

-- Case 20: Design Robinhood
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Robinhood', 'HARD', 3, 1, 20
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Robinhood'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Robinhood' LIMIT 1),
    'ARTICLE',
    'Design Robinhood',
    'https://www.hellointerview.com/learn/system-design/answer-keys/robinhood',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Robinhood')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Robinhood' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/robinhood'
);

-- Case 21: Design Google Docs
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Google Docs', 'HARD', 3, 1, 21
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Google Docs'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Google Docs' LIMIT 1),
    'ARTICLE',
    'Design Google Docs',
    'https://www.hellointerview.com/learn/system-design/answer-keys/google-docs',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Google Docs')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Google Docs' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/google-docs'
);

-- Case 22: Design a Distributed Cache
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design a Distributed Cache', 'HARD', 3, 1, 22
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design a Distributed Cache'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design a Distributed Cache' LIMIT 1),
    'ARTICLE',
    'Design a Distributed Cache',
    'https://www.hellointerview.com/learn/system-design/answer-keys/a-distributed-cache',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design a Distributed Cache')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design a Distributed Cache' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/a-distributed-cache'
);

-- Case 23: Design YouTube
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design YouTube', 'HARD', 3, 1, 23
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design YouTube'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design YouTube' LIMIT 1),
    'ARTICLE',
    'Design YouTube',
    'https://www.hellointerview.com/learn/system-design/answer-keys/youtube',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design YouTube')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design YouTube' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/youtube'
);

-- Case 24: Design a Job Scheduler
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design a Job Scheduler', 'HARD', 3, 1, 24
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design a Job Scheduler'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design a Job Scheduler' LIMIT 1),
    'ARTICLE',
    'Design a Job Scheduler',
    'https://www.hellointerview.com/learn/system-design/answer-keys/a-job-scheduler',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design a Job Scheduler')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design a Job Scheduler' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/a-job-scheduler'
);

-- Case 25: Design Web Crawler
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Web Crawler', 'HARD', 3, 1, 25
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Web Crawler'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Web Crawler' LIMIT 1),
    'ARTICLE',
    'Design Web Crawler',
    'https://www.hellointerview.com/learn/system-design/answer-keys/web-crawler',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Web Crawler')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Web Crawler' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/web-crawler'
);

-- Case 26: Design Ad Click Aggregator
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design Ad Click Aggregator', 'HARD', 3, 1, 26
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design Ad Click Aggregator'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design Ad Click Aggregator' LIMIT 1),
    'ARTICLE',
    'Design Ad Click Aggregator',
    'https://www.hellointerview.com/learn/system-design/answer-keys/ad-click-aggregator',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design Ad Click Aggregator')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design Ad Click Aggregator' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/ad-click-aggregator'
);

-- Case 27: Design a Payment System
INSERT INTO system_design_cases (skill_id, title, difficulty, created_by_user_id, is_official, display_order)
SELECT 2, 'Design a Payment System', 'HARD', 3, 1, 27
WHERE NOT EXISTS (
    SELECT 1 FROM system_design_cases WHERE title = 'Design a Payment System'
);

INSERT INTO case_resources (case_id, resource_type, title, url, description, display_order)
SELECT
    (SELECT id FROM system_design_cases WHERE title = 'Design a Payment System' LIMIT 1),
    'ARTICLE',
    'Design a Payment System',
    'https://www.hellointerview.com/learn/system-design/answer-keys/a-payment-system',
    'Hello Interview 学习资料',
    1
WHERE EXISTS (SELECT 1 FROM system_design_cases WHERE title = 'Design a Payment System')
  AND NOT EXISTS (
    SELECT 1 FROM case_resources
    WHERE case_id = (SELECT id FROM system_design_cases WHERE title = 'Design a Payment System' LIMIT 1)
      AND url = 'https://www.hellointerview.com/learn/system-design/answer-keys/a-payment-system'
);

-- ============================================================
-- 导入完成
-- ============================================================
-- 查看导入结果:
-- SELECT title, difficulty, display_order FROM system_design_cases ORDER BY display_order;
-- SELECT COUNT(*) as total, difficulty FROM system_design_cases GROUP BY difficulty;
-- ============================================================
