BEGIN;
INSERT INTO product_interaction (id, product_id, username, on_time, locate_at)
SELECT 
    gen_random_uuid(),  
    (random() * 199 + 1)::bigint,  
    CASE WHEN random() < 0.95 THEN md5(random()::text) ELSE 'Anonymous User' END,  
    NOW() - (interval '60 days' * random()),  
    CASE WHEN random() < 0.98 THEN md5(random()::text) ELSE 'Unknown' END  
FROM generate_series(1, 100000);
COMMIT;
-- Repeat this statement 90 times for 9 million rows
