#!/bin/bash

# Online Ordering System - macOS Startup Script
# This script starts all microservices in separate terminal tabs

echo "================================================"
echo "Starting Online Ordering System"
echo "================================================"
echo ""
echo "This will open 8 terminal tabs for each service"
echo "Press Ctrl+C to cancel, or wait 3 seconds to continue..."
sleep 3

# Get the project directory
PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BACKEND_DIR="$PROJECT_DIR/backend"

echo ""
echo "Starting services from: $BACKEND_DIR"
echo ""

# Function to open new terminal tab and run command
run_in_new_tab() {
    local title=$1
    local command=$2
    
    osascript -e "tell application \"Terminal\"
        do script \"echo '=== $title ==='; cd '$BACKEND_DIR/$command' && mvn spring-boot:run\"
    end tell"
}

echo "1/8 Starting Config Server..."
run_in_new_tab "Config Server (8888)" "config-server"
sleep 5

echo "2/8 Starting Eureka Server..."
run_in_new_tab "Eureka Server (8761)" "eureka-server"
sleep 10

echo "3/8 Starting API Gateway..."
run_in_new_tab "API Gateway (8080)" "api-gateway"
sleep 5

echo "4/8 Starting User Service..."
run_in_new_tab "User Service (8081)" "user-service"
sleep 2

echo "5/8 Starting Menu Service..."
run_in_new_tab "Menu Service (8082)" "menu-service"
sleep 2

echo "6/8 Starting Cart Service..."
run_in_new_tab "Cart Service (8083)" "cart-service"
sleep 2

echo "7/8 Starting Order Service..."
run_in_new_tab "Order Service (8084)" "order-service"
sleep 2

echo "8/8 All services starting..."
echo ""
echo "================================================"
echo "Services are starting in separate terminal tabs"
echo "================================================"
echo ""
echo "Wait 2-3 minutes for all services to fully start"
echo ""
echo "Then check Eureka: http://localhost:8761"
echo "And start frontend: cd frontend && npm start"
echo ""
echo "To stop services: Close each terminal tab or press Ctrl+C in each"
echo ""
