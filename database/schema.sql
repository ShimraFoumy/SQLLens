-- =============================================================================
-- SQLLens Database Schema
-- Defines tables for query performance visualization and optimizer demonstration
-- =============================================================================

DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS customers CASCADE;

-- 1. Departments
CREATE TABLE departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    building VARCHAR(50) NOT NULL,
    budget NUMERIC(14, 2) NOT NULL
);

-- 2. Employees
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    department_id INT REFERENCES departments(id) ON DELETE SET NULL,
    salary NUMERIC(10, 2) NOT NULL,
    hire_date DATE NOT NULL
);

-- 3. Customers
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Products
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0
);

-- 5. Orders
CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    customer_id INT NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    order_date DATE NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(30) NOT NULL
);

-- 6. Order Items
CREATE TABLE order_items (
    id SERIAL PRIMARY KEY,
    order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    quantity INT NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL
);
