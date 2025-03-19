-- Function to Assign Discount to Products (Ensures No Duplicate Assignments)
WITH available_products AS (
    SELECT id FROM generate_series(1, 50) id 
    WHERE id NOT IN (
        SELECT DISTINCT product_id FROM discount_product dp
        JOIN discount d ON dp.discount_id = d.id
        WHERE d.exp-date > NOW()
    )
)
INSERT INTO discount_product (discount_id, product_id)
SELECT $1, id FROM (
    SELECT id FROM available_products ORDER BY random() LIMIT $2
) sub;

-- Insert Discount Records
INSERT INTO discount (id, title, type, sales-percent-amount, exp-date)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440001', 'Flash Sale Discount', 'FLASH_SALES', 15.0, NOW() + INTERVAL '7 days'),
    ('550e8400-e29b-41d4-a716-446655440002', 'Daily Sale Discount', 'DAILY_SALES', 10.0, NOW() + INTERVAL '30 days'),
    ('550e8400-e29b-41d4-a716-446655440003', 'Seasonal Sale Discount', 'SEASONAL_SALES', 20.0, NOW() + INTERVAL '90 days');

-- Assign Discounts to Products Using the Optimized Query
DO $$ 
DECLARE 
    discount_assignments TABLE (discount_id UUID, product_count INT) VALUES 
        ('550e8400-e29b-41d4-a716-446655440001', 40),
        ('550e8400-e29b-41d4-a716-446655440002', 35),
        ('550e8400-e29b-41d4-a716-446655440003', 30);
    rec RECORD;
BEGIN
    FOR rec IN SELECT * FROM discount_assignments LOOP
        EXECUTE format($$
            WITH available_products AS (
                SELECT id FROM generate_series(1, 50) id 
                WHERE id NOT IN (
                    SELECT DISTINCT product_id FROM discount_product dp
                    JOIN discount d ON dp.discount_id = d.id
                    WHERE d.exp-date > NOW()
                )
            )
            INSERT INTO discount_product (discount_id, product_id)
            SELECT '%s', id FROM (
                SELECT id FROM available_products ORDER BY random() LIMIT %s
            ) sub;
        $$, rec.discount_id, rec.product_count);
    END LOOP;
END $$;
