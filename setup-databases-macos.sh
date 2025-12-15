#!/bin/bash

# Online Ordering System - Database Setup Script for macOS

echo "================================================"
echo "Database Setup for Online Ordering System"
echo "================================================"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running!"
    echo "Please start Docker Desktop and try again"
    exit 1
fi

echo "✅ Docker is running"
echo ""

# Check if MySQL container is running
if ! docker ps | grep -q mysql; then
    echo "❌ MySQL container is not running!"
    echo "Please start Docker containers first:"
    echo "cd infrastructure/docker && docker-compose up -d"
    exit 1
fi

echo "✅ MySQL container is running"
echo ""
echo "Setting up databases..."
echo ""

# Create databases
echo "1/5 Creating databases..."
docker exec -i docker-mysql-1 mysql -u root -ppassword << 'EOF'
CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS menu_db;
CREATE DATABASE IF NOT EXISTS order_db_0;
CREATE DATABASE IF NOT EXISTS order_db_1;
CREATE DATABASE IF NOT EXISTS order_db_2;
CREATE DATABASE IF NOT EXISTS order_db_3;
EOF

if [ $? -eq 0 ]; then
    echo "✅ Databases created"
else
    echo "❌ Failed to create databases"
    exit 1
fi

# Setup user_db
echo ""
echo "2/5 Setting up user_db..."
docker exec -i docker-mysql-1 mysql -u root -ppassword << 'EOF'
USE user_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
EOF

echo "✅ user_db setup complete"

# Setup menu_db
echo ""
echo "3/5 Setting up menu_db with sample data..."
docker exec -i docker-mysql-1 mysql -u root -ppassword << 'EOF'
USE menu_db;

CREATE TABLE IF NOT EXISTS menu_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50),
    image_url VARCHAR(255),
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_available (available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Clear existing data
TRUNCATE TABLE menu_items;

-- Insert sample data
INSERT INTO menu_items (name, description, price, category, available) VALUES
('Margherita Pizza', 'Classic pizza with tomato sauce and mozzarella', 12.99, 'Pizza', TRUE),
('Pepperoni Pizza', 'Pizza with pepperoni and cheese', 14.99, 'Pizza', TRUE),
('Hawaiian Pizza', 'Pizza with ham and pineapple', 13.99, 'Pizza', TRUE),
('Veggie Supreme Pizza', 'Pizza loaded with vegetables', 13.99, 'Pizza', TRUE),
('Cheeseburger', 'Beef burger with cheese, lettuce, and tomato', 9.99, 'Burger', TRUE),
('Chicken Burger', 'Grilled chicken burger', 10.99, 'Burger', TRUE),
('Veggie Burger', 'Plant-based burger', 11.99, 'Burger', TRUE),
('Caesar Salad', 'Fresh romaine lettuce with caesar dressing', 7.99, 'Salad', TRUE),
('Greek Salad', 'Tomatoes, cucumbers, olives, and feta cheese', 8.99, 'Salad', TRUE),
('Garden Salad', 'Mixed greens with vegetables', 6.99, 'Salad', TRUE),
('Coke', 'Coca-Cola 330ml', 2.99, 'Beverage', TRUE),
('Sprite', 'Sprite 330ml', 2.99, 'Beverage', TRUE),
('Fanta', 'Fanta Orange 330ml', 2.99, 'Beverage', TRUE),
('Water', 'Bottled water 500ml', 1.99, 'Beverage', TRUE),
('Orange Juice', 'Fresh orange juice', 3.99, 'Beverage', TRUE),
('Apple Juice', 'Fresh apple juice', 3.99, 'Beverage', TRUE);
EOF

echo "✅ menu_db setup complete (16 items added)"

# Setup order databases
echo ""
echo "4/5 Setting up order databases (4 shards)..."

for i in 0 1 2 3; do
    echo "   Setting up order_db_$i..."
    docker exec -i docker-mysql-1 mysql -u root -ppassword << EOF
USE order_db_$i;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    items TEXT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    delivery_address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
EOF
done

echo "✅ All order databases setup complete"

# Verify
echo ""
echo "5/5 Verifying setup..."
echo ""
echo "Databases:"
docker exec -i docker-mysql-1 mysql -u root -ppassword -e "SHOW DATABASES;" | grep -E "user_db|menu_db|order_db"

echo ""
echo "Menu items count:"
docker exec -i docker-mysql-1 mysql -u root -ppassword -e "USE menu_db; SELECT COUNT(*) as total_items FROM menu_items;" | tail -n 1

echo ""
echo "================================================"
echo "✅ Database setup complete!"
echo "================================================"
echo ""
echo "Summary:"
echo "  - user_db: Ready for user registration"
echo "  - menu_db: 16 menu items loaded"
echo "  - order_db_0 to order_db_3: Ready for orders (4-way sharding)"
echo ""
echo "Next steps:"
echo "  1. Build backend: cd backend && mvn clean install"
echo "  2. Start services: ./start-services-macos.sh"
echo "  3. Start frontend: cd frontend && npm start"
echo ""
