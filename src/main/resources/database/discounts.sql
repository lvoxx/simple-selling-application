-- 1. Enable the UUID extension for generating UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Insert Discount Records
WITH new_discounts AS (
    INSERT INTO discount (id, title, type, sales_percent_amount, exp_date)
    VALUES 
        (uuid_generate_v4(), 'Flash Sale Discount', 'FLASH_SALES', 15.0, NOW() + INTERVAL '7 days'),
        (uuid_generate_v4(), 'Daily Sale Discount', 'DAILY_SALES', 10.0, NOW() + INTERVAL '30 days'),
        (uuid_generate_v4(), 'Seasonal Sale Discount', 'SEASONAL_SALES', 20.0, NOW() + INTERVAL '90 days')
    RETURNING id, type
)
SELECT * FROM new_discounts;

-- 3. Update products for FLASH_SALES discount
UPDATE products
SET discount_id = (
    SELECT id FROM discount WHERE type = 'FLASH_SALES' LIMIT 1
)
WHERE id IN (
    SELECT id FROM products
    WHERE discount_id IS NULL
    ORDER BY random()
    LIMIT 40
);

-- 4. Update products for DAILY_SALES discount
UPDATE products
SET discount_id = (
    SELECT id FROM discount WHERE type = 'DAILY_SALES' LIMIT 1
)
WHERE id IN (
    SELECT id FROM products
    WHERE discount_id IS NULL
    ORDER BY random()
    LIMIT 35
);

-- 5. Update products for SEASONAL_SALES discount
UPDATE products
SET discount_id = (
    SELECT id FROM discount WHERE type = 'SEASONAL_SALES' LIMIT 1
)
WHERE id IN (
    SELECT id FROM products
    WHERE discount_id IS NULL
    ORDER BY random()
    LIMIT 30
);
