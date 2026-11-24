-- =============================================================================
-- SQLLens Indexes & Query Optimization DDL
-- These indexes demonstrate query plan transitions from Seq Scan to Index Scan
-- =============================================================================

-- Secondary index on orders(customer_id) for join & filter acceleration
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);

-- Secondary index on orders(order_date) for range scan queries
CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_date);

-- Secondary index on customers(city) for equality lookup demonstration
CREATE INDEX IF NOT EXISTS idx_customers_city ON customers(city);

-- Secondary index on products(category) for grouping and category filtering
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);

-- Composite index on employees(department_id, salary) for complex filter demo
CREATE INDEX IF NOT EXISTS idx_employees_dept_salary ON employees(department_id, salary);

-- Index on order_items foreign keys
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);

-- Refresh planner stats
ANALYZE orders;
ANALYZE customers;
ANALYZE products;
ANALYZE employees;
ANALYZE order_items;
