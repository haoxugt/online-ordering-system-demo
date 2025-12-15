# 🍎 Complete macOS Setup Guide - Online Ordering System

## **Table of Contents**

1. [Prerequisites Installation](#prerequisites-installation)
2. [Project Setup](#project-setup)
3. [Database Configuration](#database-configuration)
4. [Starting the Services](#starting-the-services)
5. [Testing the Application](#testing-the-application)
6. [Troubleshooting](#troubleshooting)

---

# 📦 Prerequisites Installation

## **1. Install Homebrew (Package Manager)**

Homebrew is the package manager for macOS. Install it first:

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

**Verify installation:**
```bash
brew --version
```

**Expected output:** `Homebrew 4.x.x`

---

## **2. Install Java JDK 17**

Spring Boot 3.x requires Java 17 or higher.

### **Installation:**
```bash
brew install openjdk@17
```

### **Add to PATH:**
```bash
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

**For Intel Macs (x86_64):**
```bash
echo 'export PATH="/usr/local/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### **Verify:**
```bash
java -version
```

**Expected output:**
```
openjdk version "17.0.x"
```

---

## **3. Install Maven**

Maven is the build tool for Java projects.

### **Installation:**
```bash
brew install maven
```

### **Verify:**
```bash
mvn -version
```

**Expected output:**
```
Apache Maven 3.9.x
Java version: 17.0.x
```

---

## **4. Install Docker Desktop**

Docker runs the infrastructure (MySQL, Redis, RabbitMQ).

### **Installation:**

**Option A: Download from website (Recommended)**
1. Go to https://www.docker.com/products/docker-desktop
2. Download Docker Desktop for Mac (choose Apple Silicon or Intel)
3. Install the .dmg file
4. Launch Docker Desktop from Applications

**Option B: Using Homebrew**
```bash
brew install --cask docker
```

### **Start Docker:**
- Open Docker Desktop from Applications
- Wait for Docker to start (icon in menu bar)

### **Verify:**
```bash
docker --version
docker-compose --version
```

**Expected output:**
```
Docker version 24.x.x
Docker Compose version v2.x.x
```

---

## **5. Install MySQL Client (Optional)**

For direct database access:

```bash
brew install mysql-client
```

**Add to PATH:**
```bash
echo 'export PATH="/opt/homebrew/opt/mysql-client/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

---

## **6. Install MongoDB (Optional - for MongoDB option)**

If you want to use MongoDB locally instead of Docker:

### **Installation:**
```bash
brew tap mongodb/brew
brew install mongodb-community@7.0
```

### **Start MongoDB:**
```bash
brew services start mongodb-community@7.0
```

### **Verify:**
```bash
mongosh --version
```

**Expected output:** `2.x.x`

---

## **7. Install Node.js and npm**

For the React frontend.

### **Installation:**
```bash
brew install node@20
```

### **Verify:**
```bash
node --version
npm --version
```

**Expected output:**
```
v20.x.x
10.x.x
```

---

## **8. Install Git (Usually pre-installed)**

```bash
brew install git
```

### **Verify:**
```bash
git --version
```

---

# 🚀 Project Setup

## **1. Download and Extract Project**

```bash
# Navigate to your projects directory
cd ~/Documents

# Unzip the project
unzip online-ordering-system.zip

# Navigate into project
cd online-ordering-system
```

---

## **2. Verify Project Structure**

```bash
ls -la
```

**You should see:**
```
backend/                    # Spring Boot microservices
frontend/                   # React application
infrastructure/            # Docker configuration
docs/                      # Documentation
README.md
```

---

## **3. Make Scripts Executable**

```bash
chmod +x start-services.sh
chmod +x build.sh
```

---

# 🗄️ Database Configuration

## **1. Start Docker Infrastructure**

### **Navigate to Docker directory:**
```bash
cd infrastructure/docker
```

### **Start Docker services:**
```bash
docker-compose up -d
```

**This starts:**
- MySQL 8.0 (port 3306)
- Redis 7.0 (port 6379)
- RabbitMQ 3.12 (ports 5672, 15672)

### **Verify containers are running:**
```bash
docker ps
```

**Expected output:**
```
CONTAINER ID   IMAGE              STATUS         PORTS                    NAMES
abc123         mysql:8.0          Up 30 seconds  3306->3306/tcp          mysql
def456         redis:7-alpine     Up 30 seconds  6379->6379/tcp          redis
ghi789         rabbitmq:3.12      Up 30 seconds  5672->5672, 15672/tcp   rabbitmq
```

---

## **2. Setup MySQL Databases**

```bash
cd ~/Documents/online-ordering-system
./setup-databases-macos.sh
```


### **Verify databases:**

```bash
docker exec -i docker-mysql-1 mysql -u root -ppassword -e "SHOW DATABASES;"
```

**Expected output:**
```
+--------------------+
| Database           |
+--------------------+
| information_schema |
| menu_db            |
| mysql              |
| order_db_0         |
| order_db_1         |
| order_db_2         |
| order_db_3         |
| user_db            |
+--------------------+
```

---

## **3. Setup MongoDB (Optional)**

If you want to use MongoDB for orders:

### **Using Docker MongoDB:**

The docker-compose already includes MongoDB. Just verify:

```bash
docker exec -it mongodb mongosh
```

### **Create database and user:**

```javascript
use orders_db

db.createUser({
  user: "orderservice",
  pwd: "orderpass",
  roles: [{ role: "readWrite", db: "orders_db" }]
})

// Create indexes
db.orders.createIndex({ "user_id": 1 })
db.orders.createIndex({ "status": 1 })
db.orders.createIndex({ "created_at": -1 })

exit
```

---

## **4. Verify Redis**

```bash
docker exec -it redis redis-cli ping
```

**Expected output:** `PONG`

---

## **5. Verify RabbitMQ**

**Access RabbitMQ Management UI:**
- Open browser: http://localhost:15672
- Username: `guest`
- Password: `guest`

**✅ You should see the RabbitMQ dashboard**

---

# 🏗️ Build the Backend

## **1. Navigate to Backend Directory**

```bash
cd ~/Documents/online-ordering-system/backend
```

---

## **2. Build All Microservices**

```bash
mvn clean install
```

**This will:**
1. Download all dependencies
2. Compile all services
3. Run tests
4. Package JAR files

**Expected output:**
```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] ------------------------------------------------------------------------
[INFO] parent ............................................. SUCCESS
[INFO] common ............................................. SUCCESS
[INFO] config-server ...................................... SUCCESS
[INFO] eureka-server ...................................... SUCCESS
[INFO] api-gateway ........................................ SUCCESS
[INFO] user-service ....................................... SUCCESS
[INFO] menu-service ....................................... SUCCESS
[INFO] cart-service ....................................... SUCCESS
[INFO] order-service ...................................... SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**Time:** First build takes 5-10 minutes (downloading dependencies)

---

## **3. Troubleshooting Build Issues**

### **If you get "JAVA_HOME not set":**

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
```

### **If you get Maven download errors:**

```bash
# Clear Maven cache
rm -rf ~/.m2/repository
mvn clean install
```

---

# 🎯 Starting the Services

## **Method 1: Using Start Script (Recommended)**

### **1. Make script executable:**

```bash
cd ~/Documents/online-ordering-system
chmod +x start-services.sh
```

### **2. Start all services:**

```bash
./start-services.sh
```

**This opens multiple terminal tabs** for each service.

---

## **Method 2: Manual Start (For Debugging)**

Open **8 separate terminal windows/tabs** and run:

### **Terminal 1: Config Server**
```bash
cd ~/Documents/online-ordering-system/backend/config-server
mvn spring-boot:run
```
**Wait for:** `Started ConfigServerApplication`

---

### **Terminal 2: Eureka Server**
```bash
cd ~/Documents/online-ordering-system/backend/eureka-server
mvn spring-boot:run
```
**Wait for:** `Started EurekaServerApplication`

---

### **Terminal 3: API Gateway**
```bash
cd ~/Documents/online-ordering-system/backend/api-gateway
mvn spring-boot:run
```
**Wait for:** `Started ApiGatewayApplication`

---

### **Terminal 4: User Service**
```bash
cd ~/Documents/online-ordering-system/backend/user-service
mvn spring-boot:run
```
**Wait for:** `Started UserServiceApplication`

---

### **Terminal 5: Menu Service**
```bash
cd ~/Documents/online-ordering-system/backend/menu-service
mvn spring-boot:run
```
**Wait for:** `Started MenuServiceApplication`

---

### **Terminal 6: Cart Service**
```bash
cd ~/Documents/online-ordering-system/backend/cart-service
mvn spring-boot:run
```
**Wait for:** `Started CartServiceApplication`

---

### **Terminal 7: Order Service**
```bash
cd ~/Documents/online-ordering-system/backend/order-service
mvn spring-boot:run
```
**Wait for:** `Started OrderServiceApplication`

---

### **Terminal 8: (Optional) MongoDB Order Service**
```bash
cd ~/Documents/online-ordering-system/backend/order-service-mongodb
mvn spring-boot:run
```
**Wait for:** `Started OrderServiceMongoApplication`

---

## **Service Startup Order**

**IMPORTANT:** Start in this order:

1. **Config Server** (8888) - Must start first
2. **Eureka Server** (8761) - Wait 30 seconds
3. **API Gateway** (8080) - Wait for Eureka
4. **Business Services** (8081-8084) - Can start simultaneously
   - User Service (8081)
   - Menu Service (8082)
   - Cart Service (8083)
   - Order Service (8084)

**Total startup time:** ~3-5 minutes

---

## **Verify All Services Started**

### **Check Eureka Dashboard:**

Open browser: http://localhost:8761

**You should see all services registered:**
- CONFIG-SERVER
- API-GATEWAY
- USER-SERVICE
- MENU-SERVICE
- CART-SERVICE
- ORDER-SERVICE

---

### **Check Service Health:**

```bash
# API Gateway
curl http://localhost:8080/actuator/health

# User Service
curl http://localhost:8081/actuator/health

# Menu Service
curl http://localhost:8082/actuator/health

# Cart Service
curl http://localhost:8083/actuator/health

# Order Service
curl http://localhost:8084/actuator/health
```

**All should return:** `{"status":"UP"}`

---

# 🎨 Starting the Frontend

## **1. Install Dependencies**

```bash
cd ~/Documents/online-ordering-system/frontend
npm install
```

**Time:** 1-2 minutes

---

## **2. Start React Development Server**

```bash
npm start
```

**Expected output:**
```
Compiled successfully!

You can now view frontend in the browser.

  Local:            http://localhost:3000
  On Your Network:  http://192.168.x.x:3000
```

**Browser should automatically open to** http://localhost:3000

---

# 🧪 Testing the Application

## **1. Test User Registration**

### **Via Frontend:**
1. Go to http://localhost:3000
2. Click "Register"
3. Fill form:
   - Username: `testuser`
   - Email: `test@example.com`
   - Password: `password123`
   - Phone: `555-1234`
4. Click "Register"

### **Via API:**
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "phone": "555-1234"
  }'
```

**Expected response:**
```json
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "role": "USER"
}
```

---

## **2. Test User Login**

### **Via Frontend:**
1. Go to http://localhost:3000/login
2. Enter:
   - Username: `testuser`
   - Password: `password123`
3. Click "Login"

### **Via API:**
```bash
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Expected response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "username": "testuser",
  "role": "USER"
}
```

**Save the token for authenticated requests!**

---

## **3. Test Menu Service**

### **Get all menu items:**
```bash
curl http://localhost:8082/api/menu/items
```

**Or via frontend:** http://localhost:3000/menu

**Expected:** List of pizzas, burgers, salads, beverages

---

## **4. Test Cart Service**

### **Add item to cart:**
```bash
curl -X POST http://localhost:8083/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{
    "menuItemId": 1,
    "name": "Margherita Pizza",
    "price": 12.99,
    "quantity": 2
  }'
```

### **Get cart:**
```bash
curl http://localhost:8083/api/cart/1
```

**Expected:**
```json
{
  "userId": 1,
  "items": [
    {
      "menuItemId": 1,
      "name": "Margherita Pizza",
      "price": 12.99,
      "quantity": 2,
      "subtotal": 25.98
    }
  ],
  "totalAmount": 25.98
}
```

---

## **5. Test Order Service**

### **Create order:**
```bash
curl -X POST http://localhost:8084/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "userId": 1,
    "items": "[{\"menuItemId\":1,\"name\":\"Pizza\",\"price\":12.99,\"quantity\":2}]",
    "totalAmount": 25.98,
    "paymentMethod": "CREDIT_CARD",
    "deliveryAddress": "123 Main St, San Francisco, CA"
  }'
```

---

## **6. Test Complete User Flow**

1. ✅ Register account
2. ✅ Login (get JWT token)
3. ✅ Browse menu
4. ✅ Add items to cart
5. ✅ Place order
6. ✅ View order history

---

# 🔧 Troubleshooting

## **Docker Issues**

### **Containers not starting:**
```bash
# Check Docker is running
docker ps

# Restart Docker Desktop
# Applications → Docker → Restart

# View logs
docker-compose logs mysql
docker-compose logs redis
```

---

### **Port already in use:**
```bash
# Find process using port 3306
lsof -i :3306

# Kill process
kill -9 <PID>
```

---

## **Maven Build Failures**

### **Dependencies not downloading:**
```bash
# Use different Maven repository
mvn clean install -U
```

### **Out of memory:**
```bash
export MAVEN_OPTS="-Xmx2048m"
mvn clean install
```

---

## **Service Startup Issues**

### **Config Server not starting:**
```bash
# Check Java version
java -version  # Must be 17+

# Check port 8888
lsof -i :8888
```

### **Eureka Server not registering services:**
```bash
# Wait longer (up to 2 minutes)
# Check Eureka at http://localhost:8761
```

---

## **Database Connection Issues**

### **MySQL connection refused:**
```bash
# Check MySQL container
docker ps | grep mysql

# Restart MySQL
docker-compose restart mysql

# Wait 30 seconds
sleep 30
```

### **Redis connection refused:**
```bash
# Check Redis
docker exec -it redis redis-cli ping

# Restart if needed
docker-compose restart redis
```

---

## **Frontend Issues**

### **npm install fails:**
```bash
# Clear cache
npm cache clean --force

# Remove node_modules
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

### **Port 3000 already in use:**
```bash
# Find process
lsof -i :3000

# Kill it
kill -9 <PID>

# Or use different port
PORT=3001 npm start
```

---

# 📊 Service Ports Reference

| Service | Port | URL |
|---------|------|-----|
| **Config Server** | 8888 | http://localhost:8888 |
| **Eureka Server** | 8761 | http://localhost:8761 |
| **API Gateway** | 8080 | http://localhost:8080 |
| **User Service** | 8081 | http://localhost:8081 |
| **Menu Service** | 8082 | http://localhost:8082 |
| **Cart Service** | 8083 | http://localhost:8083 |
| **Order Service** | 8084 | http://localhost:8084 |
| **MongoDB Order Service** | 8085 | http://localhost:8085 |
| **Frontend** | 3000 | http://localhost:3000 |
| **MySQL** | 3306 | localhost:3306 |
| **Redis** | 6379 | localhost:6379 |
| **RabbitMQ** | 5672 | localhost:5672 |
| **RabbitMQ Management** | 15672 | http://localhost:15672 |
| **MongoDB** | 27017 | localhost:27017 |

---

# 🎯 Quick Start Checklist

- [ ] Install Homebrew
- [ ] Install Java 17
- [ ] Install Maven
- [ ] Install Docker Desktop
- [ ] Install Node.js
- [ ] Extract project
- [ ] Start Docker containers
- [ ] Setup MySQL databases
- [ ] Setup MongoDB (optional)
- [ ] Build backend (`mvn clean install`)
- [ ] Start all 8 microservices
- [ ] Install frontend deps (`npm install`)
- [ ] Start frontend (`npm start`)
- [ ] Test application

---

# 💡 Tips for macOS

## **Use iTerm2 for Better Terminal**

```bash
brew install --cask iterm2
```

---

## **Create Aliases for Quick Commands**

Add to `~/.zshrc`:

```bash
alias start-docker="cd ~/Documents/online-ordering-system/infrastructure/docker && docker-compose up -d"
alias stop-docker="cd ~/Documents/online-ordering-system/infrastructure/docker && docker-compose down"
alias build-backend="cd ~/Documents/online-ordering-system/backend && mvn clean install"
alias start-frontend="cd ~/Documents/online-ordering-system/frontend && npm start"
```

Reload:
```bash
source ~/.zshrc
```

---

## **Monitor Resource Usage**

**Docker:**
```bash
docker stats
```

**System:**
- Open Activity Monitor
- Check CPU, Memory, Network

---

# 📚 Additional Resources

## **Architecture Documentation:**
- `docs/api/API_DOCUMENTATION.md` - API endpoints
- `docs/database/SHARDING_PARTITIONING_GUIDE.md` - Database design
- `AUTHENTICATION_AUTHORIZATION_GUIDE.md` - Security

## **Online Resources:**
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Cloud: https://spring.io/projects/spring-cloud
- React: https://react.dev
- Docker: https://docs.docker.com

---

# ✅ Success Indicators

**When everything is working:**

1. ✅ Docker shows 3 containers running
2. ✅ Eureka shows 7 registered services
3. ✅ All health endpoints return UP
4. ✅ Frontend loads at http://localhost:3000
5. ✅ Can register/login users
6. ✅ Can view menu items
7. ✅ Can add to cart
8. ✅ Can place orders

---

**Congratulations! Your microservices system is running on macOS!** 🎉

---

# 🆘 Getting Help

If you encounter issues:

1. Check the [Troubleshooting](#troubleshooting) section
2. Review logs: `docker-compose logs`
3. Check service logs in terminal windows
4. Verify all ports are available
5. Ensure Java 17 is being used
6. Try restarting Docker Desktop

**Common fixes:**
- Restart Docker Desktop
- Clear Maven cache: `rm -rf ~/.m2/repository`
- Rebuild: `mvn clean install`
- Restart services in correct order

---

**macOS-specific differences from Windows:**
- Use `/` instead of `\` in paths
- Use `~/.zshrc` instead of environment variables GUI
- Use `lsof` instead of `netstat` to check ports
- Use `open` instead of `start` to open files/URLs
- Docker Desktop runs as GUI app, not service

**Everything else is the same!** 🚀
