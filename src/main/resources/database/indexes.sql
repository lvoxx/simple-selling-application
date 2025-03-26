-- Product
CREATE INDEX IF NOT EXISTS idx_products_name_tsv 
ON products USING GIN (to_tsvector('english', name));

-- Discount
CREATE INDEX IF NOT EXISTS idx_discount_title_btree
ON discount(title);

CREATE INDEX IF NOT EXISTS idx_discount_exp_date_brin
ON discount(exp_date) USING brin(timestamp);

-- Product Interaction
CREATE INDEX IF NOT EXISTS idx_product_interaction_on_time_brin 
ON product_interaction(on_time) USING brin(timestamp);

CREATE INDEX IF NOT EXISTS idx_product_interaction_locate_at_btree 
ON product_interaction(locate_at);

-- Spring User
CREATE INDEX IF NOT EXISTS idx_spring_user_email_hash 
ON spring_user USING hash(email);

CREATE INDEX IF NOT EXISTS idx_spring_user_phone_number_hash 
ON spring_user USING hash(phone_number);

-- Registration Token
CREATE INDEX IF NOT EXISTS idx_registration_token_token_hash 
ON registration_token USING hash(token);