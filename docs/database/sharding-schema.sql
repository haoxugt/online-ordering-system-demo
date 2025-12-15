-- ==================================================================
-- MySQL PARTITIONING SCHEMA - Fixed for MySQL 8.0 Compatibility
-- ==================================================================
-- This version fixes the "timezone-dependent expressions" error
-- by using a separate integer column for partitioning

-- ============ CREATE DATABASES ============
CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS menu_db;
CREATE DATABASE IF NOT EXISTS order_db_0;
CREATE DATABASE IF NOT EXISTS order_db_1;
CREATE DATABASE IF NOT EXISTS order_db_2;
CREATE DATABASE IF NOT EXISTS order_db_3;

-- ============ USER DATABASE ============
USE user_db;

-- Note: Users table is NOT partitioned to allow UNIQUE constraints on username/email
-- Partitioning users doesn't provide much benefit since user queries are by username/email
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ MENU DATABASE ============
USE menu_db;

-- Menu items partitioned by category
-- Note: Category must be part of any unique indexes due to partitioning rules
CREATE TABLE IF NOT EXISTS menu_items (
    id BIGINT AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'Other',  -- Required for partitioning
    image_url VARCHAR(255),
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id, category),  -- Composite PK includes partition key
    INDEX idx_category (category),
    INDEX idx_available (available),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
PARTITION BY LIST COLUMNS(category) (
    PARTITION p_pizza VALUES IN ('Pizza', 'Italian'),
    PARTITION p_burger VALUES IN ('Burger', 'Sandwich'),
    PARTITION p_asian VALUES IN ('Asian', 'Chinese', 'Japanese', 'Sushi'),
    PARTITION p_beverages VALUES IN ('Beverage', 'Drink', 'Coffee', 'Tea'),
    PARTITION p_others VALUES IN ('Salad', 'Dessert', 'Other', 'Appetizer', 'Soup')
);

-- Insert sample menu items
INSERT INTO menu_items (name, description, price, category, available) VALUES
('Margherita Pizza', 'Classic pizza with tomato sauce and mozzarella', 12.99, 'Pizza', TRUE),
('Pepperoni Pizza', 'Pizza with pepperoni and cheese', 14.99, 'Pizza', TRUE),
('Cheeseburger', 'Beef burger with cheese, lettuce, and tomato', 9.99, 'Burger', TRUE),
('Chicken Sandwich', 'Grilled chicken sandwich', 8.99, 'Sandwich', TRUE),
('Caesar Salad', 'Fresh romaine lettuce with caesar dressing', 7.99, 'Salad', TRUE),
('Coke', 'Coca-Cola 330ml', 2.99, 'Beverage', TRUE),
('Water', 'Bottled water 500ml', 1.99, 'Beverage', TRUE);

-- ============ ORDER DATABASE 0 ============
USE order_db_0;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    items TEXT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    delivery_address TEXT,
    created_year INT NOT NULL,  -- Partitioning column
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_year),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
PARTITION BY RANGE (created_year) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027),
    PARTITION p2027 VALUES LESS THAN (2028),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- ============ ORDER DATABASE 1 ============
USE order_db_1;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    items TEXT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    delivery_address TEXT,
    created_year INT NOT NULL,  -- Partitioning column
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_year),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
PARTITION BY RANGE (created_year) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027),
    PARTITION p2027 VALUES LESS THAN (2028),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- ============ ORDER DATABASE 2 ============
USE order_db_2;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    items TEXT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    delivery_address TEXT,
    created_year INT NOT NULL,  -- Partitioning column
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_year),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
PARTITION BY RANGE (created_year) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027),
    PARTITION p2027 VALUES LESS THAN (2028),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- ============ ORDER DATABASE 3 ============
USE order_db_3;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    items TEXT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    delivery_address TEXT,
    created_year INT NOT NULL,  -- Partitioning column
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_year),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
PARTITION BY RANGE (created_year) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026),
    PARTITION p2026 VALUES LESS THAN (2027),
    PARTITION p2027 VALUES LESS THAN (2028),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- ============ VERIFY PARTITIONING ============
SELECT 
    TABLE_SCHEMA,
    TABLE_NAME,
    PARTITION_NAME,
    PARTITION_METHOD,
    PARTITION_EXPRESSION,
    TABLE_ROWS
FROM 
    INFORMATION_SCHEMA.PARTITIONS
WHERE 
    TABLE_SCHEMA IN ('order_db_0', 'order_db_1', 'order_db_2', 'order_db_3', 'user_db', 'menu_db')
    AND PARTITION_NAME IS NOT NULL
ORDER BY 
    TABLE_SCHEMA, TABLE_NAME, PARTITION_NAME;

-- Show databases
SHOW DATABASES;

-- Show sample data
USE menu_db;
SELECT id, name, price, category FROM menu_items;
