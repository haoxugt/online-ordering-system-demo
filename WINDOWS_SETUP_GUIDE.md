# 🪟 Windows Setup Guide - Complete Step-by-Step Tutorial

## Complete Guide to Build and Run This Project on Windows

This guide will take you from zero to running the complete online ordering system on Windows.

---

## 📋 Prerequisites Installation

### 1. Install Java 17 (JDK)

**Download:**
1. Go to: https://adoptium.net/
2. Download **Temurin 17 (LTS)** for Windows
3. Choose **x64** installer (.msi)

**Install:**
1. Run the downloaded `.msi` file
2. ✅ Check "Set JAVA_HOME variable"
3. ✅ Check "Add to PATH"
4. Click "Next" → "Install"

**Verify:**
```cmd
# Open Command Prompt (Win+R, type 'cmd', Enter)
java -version

# Should see:
# openjdk version "17.0.x"
```

**If not found, set manually:**
```cmd
# Open System Environment Variables
Win+R → type: sysdm.cpl → Enter
→ "Advanced" tab → "Environment Variables"

# Add JAVA_HOME:
Variable name: JAVA_HOME
Variable value: C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot

# Edit Path:
Add: %JAVA_HOME%\bin
```

---

### 2. Install Maven

**Download:**
1. Go to: https://maven.apache.org/download.cgi
2. Download **Binary zip archive** (apache-maven-3.9.x-bin.zip)

**Install:**
1. Extract to `C:\Program Files\Apache\Maven`
2. Open System Environment Variables (Win+R → `sysdm.cpl`)
3. Environment Variables → System variables

**Add MAVEN_HOME:**
```
Variable name: MAVEN_HOME
Variable value: C:\Program Files\Apache\Maven\apache-maven-3.9.x
```

**Edit Path:**
```
Add: %MAVEN_HOME%\bin
```

**Verify:**
```cmd
mvn -version

# Should see:
# Apache Maven 3.9.x
# Java version: 17.0.x
```

---

### 3. Install Node.js and npm

**Download:**
1. Go to: https://nodejs.org/
2. Download **LTS version** (20.x or higher)
3. Run the installer

**Install:**
1. Run the downloaded `.msi` file
2. ✅ Accept all defaults
3. ✅ Check "Automatically install necessary tools"

**Verify:**
```cmd
node -v
# Should see: v20.x.x

npm -v
# Should see: 10.x.x
```

---

### 4. Install Docker Desktop

**Download:**
1. Go to: https://www.docker.com/products/docker-desktop/
2. Download **Docker Desktop for Windows**

**Install:**
1. Run the installer
2. ✅ Use WSL 2 instead of Hyper-V (recommended)
3. Restart computer when prompted

**Enable WSL 2 (if not enabled):**
```powershell
# Open PowerShell as Administrator (Right-click → "Run as administrator")

# Enable WSL
dism.exe /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart

# Enable Virtual Machine Platform
dism.exe /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart

# Restart computer

# After restart, set WSL 2 as default
wsl --set-default-version 2
```

**Start Docker Desktop:**
1. Open Docker Desktop from Start Menu
2. Wait for "Docker Desktop is running" (green icon in system tray)

**Verify:**
```cmd
docker --version
# Should see: Docker version 24.x.x

docker-compose --version
# Should see: Docker Compose version v2.x.x
```

---

### 5. Install MySQL Client (Optional but Recommended)

**Download:**
1. Go to: https://dev.mysql.com/downloads/mysql/
2. Download **MySQL Installer for Windows**

**Install:**
1. Run MySQL Installer
2. Choose **"Custom"** installation
3. Select only: **MySQL Shell** (for command line)
4. Or use **MySQL Workbench** for GUI

**Alternative (Lightweight):**
- Just use Docker MySQL (covered later)
- No separate installation needed!

---

### 6. Install Git (Optional but Recommended)

**Download:**
1. Go to: https://git-scm.com/download/win
2. Download Git for Windows

**Install:**
1. Run installer
2. Accept all defaults

**Verify:**
```cmd
git --version
# Should see: git version 2.x.x
```

---

### 7. Install a Good Text Editor/IDE

**Choose One:**

**Option A: IntelliJ IDEA (Recommended for Java)**
1. Go to: https://www.jetbrains.com/idea/download/
2. Download **Community Edition** (Free)
3. Install with defaults

**Option B: VS Code (Lightweight)**
1. Go to: https://code.visualstudio.com/
2. Download and install
3. Install extensions:
   - Java Extension Pack
   - Spring Boot Extension Pack
   - ES7+ React/Redux/React-Native snippets

---

## 📦 Project Setup

### Step 1: Extract the Project

```cmd
# Create a projects folder
cd C:\
mkdir Projects
cd Projects

# Extract the downloaded tar.gz file here
# Right-click → "Extract All" (Windows 11)
# Or use 7-Zip if you have it installed

# You should now have:
C:\Projects\online-ordering-system\
```

**If you can't extract .tar.gz:**

**Install 7-Zip:**
1. Download: https://www.7-zip.org/
2. Install
3. Right-click the .tar.gz file → 7-Zip → "Extract Here"

---

### Step 2: Verify Project Structure

```cmd
cd C:\Projects\online-ordering-system
dir

# You should see:
# backend\
# frontend\
# infrastructure\
# docs\
# README.md
# etc.
```

---

## 🚀 Building the Project

### Step 1: Build Backend (All Microservices)

```cmd
# Navigate to backend
cd C:\Projects\online-ordering-system\backend

# Clean and build all services
mvn clean install

# This will:
# - Download all dependencies (first time takes 5-10 minutes)
# - Compile all Java code
# - Run tests
# - Create JAR files

# Wait for: "BUILD SUCCESS"
```

**If build fails with "mvn not found":**
```cmd
# Close and reopen Command Prompt
# Maven needs PATH to be reloaded
```

**If build fails with Java errors:**
```cmd
# Check Java version
java -version

# Must be Java 17 or higher
# If wrong version, reinstall Java 17
```

---

### Step 2: Build Frontend

```cmd
# Navigate to frontend
cd C:\Projects\online-ordering-system\frontend

# Install dependencies
npm install

# This downloads all React dependencies (takes 2-3 minutes)

# Wait for completion (no errors)
```

**If npm install fails:**
```cmd
# Clear npm cache
npm cache clean --force

# Try again
npm install
```

---

## 🐳 Starting Infrastructure (Docker)

### Step 1: Start Docker Desktop

1. Open **Docker Desktop** from Start Menu
2. Wait until you see **"Docker Desktop is running"**
3. Green icon in system tray

---

### Step 2: Choose Your Setup

You have 3 options:

#### **OPTION A: Basic Setup (MySQL + RabbitMQ + Redis)**

```cmd
cd C:\Projects\online-ordering-system\infrastructure\docker

# Start all infrastructure
docker-compose up -d

# This starts:
# - MySQL (port 3306)
# - Redis (port 6379)
# - RabbitMQ (ports 5672, 15672)

# Wait 30 seconds for services to start
```

#### **OPTION B: With MongoDB (Single Instance)**

```cmd
cd C:\Projects\online-ordering-system\infrastructure\docker

# Start with MongoDB
docker-compose -f docker-compose-with-mongodb.yml up -d

# This starts:
# - MySQL (port 3306)
# - Redis (port 6379)
# - RabbitMQ (ports 5672, 15672)
# - MongoDB (port 27017)

# Wait 30 seconds
```

#### **OPTION C: Full Setup (MongoDB Sharded Cluster)**

```cmd
cd C:\Projects\online-ordering-system\infrastructure\docker

# Start sharded MongoDB cluster
docker-compose -f docker-compose-mongodb-sharded.yml up -d

# This starts:
# - MySQL (port 3306)
# - Redis (port 6379)
# - RabbitMQ (ports 5672, 15672)
# - MongoDB Sharded Cluster (3 shards + config servers)

# Wait 60 seconds for cluster initialization
```

**Recommended for learning: Option A first, then try Option C**

---

### Step 3: Verify Docker Containers

```cmd
# Check running containers
docker ps

# You should see containers running:
# - mysql
# - redis
# - rabbitmq
# - (mongodb if you chose option B or C)

# Check logs if any issues
docker logs mysql
docker logs rabbitmq
```

**Common Docker Issues:**

**"Cannot connect to Docker daemon":**
```cmd
# Make sure Docker Desktop is running
# Check system tray for Docker icon (must be green)
```

**"Port already in use":**
```cmd
# Check what's using the port
netstat -ano | findstr :3306
netstat -ano | findstr :5672

# Kill the process or change port in docker-compose.yml
```

---

## 🗄️ Database Setup

### Option 1: MySQL Sharding Setup (Recommended)

```cmd
# Connect to MySQL container
docker exec -it mysql mysql -u root -p

# Password: password

# Run sharding script
mysql> source /C:/Projects/online-ordering-system/docs/database/sharding-schema.sql

# OR copy-paste from file
```

**Alternative (if source command doesn't work):**

```cmd
# Copy file into container
docker cp C:\Projects\online-ordering-system\docs\database\sharding-schema.sql mysql:/tmp/

# Execute
docker exec -it mysql mysql -u root -ppassword < /tmp/sharding-schema.sql
```

**Or use MySQL Workbench:**
1. Open MySQL Workbench
2. Connect to: `localhost:3306`
3. Username: `root`, Password: `password`
4. File → Open SQL Script → `sharding-schema.sql`
5. Execute (lightning bolt icon)

---

### Option 2: MongoDB Setup (If using MongoDB)

```cmd
# Connect to MongoDB container
docker exec -it mongos mongosh

# Initialize databases
use orders_db

db.createUser({
  user: 'orderservice',
  pwd: 'orderpass',
  roles: [{ role: 'readWrite', db: 'orders_db' }]
})

# Create indexes
db.orders.createIndex({ "user_id": 1 })
db.orders.createIndex({ "status": 1 })
db.orders.createIndex({ "timestamps.created": -1 })

# Exit
exit
```

---

## 🎯 Starting the Microservices

Open **7 separate Command Prompt windows** (or use Terminal tabs):

### Window 1: Config Server

```cmd
cd C:\Projects\online-ordering-system\backend\config-server
mvn spring-boot:run

# Wait for: "Started ConfigServerApplication"
# Port: 8888
```

---

### Window 2: Eureka Server (Service Discovery)

```cmd
cd C:\Projects\online-ordering-system\backend\eureka-server
mvn spring-boot:run

# Wait for: "Started EurekaServerApplication"
# Port: 8761
# Open: http://localhost:8761 (Eureka Dashboard)
```

---

### Window 3: API Gateway

```cmd
cd C:\Projects\online-ordering-system\backend\api-gateway
mvn spring-boot:run

# Wait for: "Started ApiGatewayApplication"
# Port: 8080
```

---

### Window 4: User Service

```cmd
cd C:\Projects\online-ordering-system\backend\user-service
mvn spring-boot:run

# Wait for: "Started UserServiceApplication"
# Port: 8081
```

---

### Window 5: Menu Service

```cmd
cd C:\Projects\online-ordering-system\backend\menu-service
mvn spring-boot:run

# Wait for: "Started MenuServiceApplication"
# Port: 8082
```

---

### Window 6: Cart Service

```cmd
cd C:\Projects\online-ordering-system\backend\cart-service
mvn spring-boot:run

# Wait for: "Started CartServiceApplication"
# Port: 8083
```

---

### Window 7: Order Service (Choose MySQL or MongoDB)

**For MySQL with Sharding:**
```cmd
cd C:\Projects\online-ordering-system\backend\order-service
mvn spring-boot:run

# Port: 8084
```

**For MongoDB:**
```cmd
cd C:\Projects\online-ordering-system\backend\order-service-mongodb
mvn spring-boot:run

# Port: 8085
```

**You can run BOTH if you want!**

---

## 🎨 Starting the Frontend

### Window 8: React Frontend

```cmd
cd C:\Projects\online-ordering-system\frontend
npm start

# Opens browser automatically at: http://localhost:3000
# Wait for: "Compiled successfully!"
```

---

## ✅ Verify Everything is Running

### Check Services

Open your browser and check:

| Service | URL | What to See |
|---------|-----|-------------|
| **Frontend** | http://localhost:3000 | Homepage |
| **Eureka** | http://localhost:8761 | All services registered |
| **RabbitMQ** | http://localhost:15672 | Management UI (guest/guest) |
| **API Gateway** | http://localhost:8080/actuator/health | {"status":"UP"} |

---

### Test the System

#### 1. Register a User

```cmd
# Open Command Prompt or use Postman
curl -X POST http://localhost:8080/api/users/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"password\":\"password123\",\"email\":\"test@example.com\",\"phone\":\"1234567890\"}"

# You should get a success response
```

#### 2. Login

```cmd
curl -X POST http://localhost:8080/api/users/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"password\":\"password123\"}"

# Copy the JWT token from response
```

#### 3. Browse Menu

```cmd
curl http://localhost:8080/api/menu/items

# You should see menu items (if sample data loaded)
```

---

## 🛠️ Using the Application

### Through Browser (Frontend)

1. Open: http://localhost:3000
2. Click **"Register"**
3. Fill in details and register
4. Login with your credentials
5. Browse menu items
6. Add items to cart
7. View cart
8. Place order
9. View your orders

---

### Through Postman (API Testing)

**Download Postman:**
1. https://www.postman.com/downloads/
2. Install and open

**Import Collection:**
1. Open Postman
2. File → Import
3. Paste the API endpoints from `docs/api/API_DOCUMENTATION.md`

---

## 📊 Monitoring and Management

### Eureka Dashboard
```
http://localhost:8761
```
- See all registered services
- Check service health
- View instance details

### RabbitMQ Management
```
http://localhost:15672
Username: guest
Password: guest
```
- View queues
- See message rates
- Monitor consumers

### Kafka UI (if using Kafka)
```
http://localhost:8090
```
- View topics
- Check partitions
- Monitor consumers

### MongoDB (if using)
```cmd
# Connect via command line
docker exec -it mongos mongosh

# Or use MongoDB Compass (GUI)
# Download: https://www.mongodb.com/products/compass
# Connect to: mongodb://localhost:27017
```

### MySQL Workbench
```
Host: localhost
Port: 3306
Username: root
Password: password
```

---

## 🐛 Troubleshooting

### "Port already in use"

```cmd
# Find what's using the port
netstat -ano | findstr :8080

# Kill the process
taskkill /PID <PID_NUMBER> /F

# Or change port in application.yml
```

---

### "Cannot connect to Docker"

```cmd
# Check Docker Desktop status
# System tray → Docker icon should be green

# Restart Docker Desktop
# Right-click Docker icon → Restart

# Or restart Docker service
net stop com.docker.service
net start com.docker.service
```

---

### "Service not registering with Eureka"

```cmd
# Check Eureka is running
curl http://localhost:8761

# Check service application.yml:
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

# Restart the service
```

---

### "Maven build fails"

```cmd
# Clean Maven cache
mvn clean

# Delete .m2 repository (nuclear option)
rmdir /s /q %USERPROFILE%\.m2\repository

# Rebuild
mvn clean install
```

---

### "npm install fails"

```cmd
# Delete node_modules and package-lock.json
rmdir /s /q node_modules
del package-lock.json

# Clear cache
npm cache clean --force

# Reinstall
npm install
```

---

### "Database connection error"

```cmd
# Check MySQL is running
docker ps | findstr mysql

# Check MySQL logs
docker logs mysql

# Test connection
docker exec -it mysql mysql -u root -ppassword -e "SHOW DATABASES;"

# Verify application.yml has correct config:
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/user_db
    username: root
    password: password
```

---

## 📝 Quick Command Reference

### Docker Commands

```cmd
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View running containers
docker ps

# View logs
docker logs <container_name>

# Restart a container
docker restart <container_name>

# Remove all containers and start fresh
docker-compose down -v
docker-compose up -d
```

### Maven Commands

```cmd
# Build all services
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests

# Run a specific service
mvn spring-boot:run

# Clean build artifacts
mvn clean
```

### npm Commands

```cmd
# Install dependencies
npm install

# Start development server
npm start

# Build for production
npm run build

# Clear cache
npm cache clean --force
```

---

## 🎓 Learning Path

### Day 1: Basic Setup
1. ✅ Install all prerequisites
2. ✅ Extract project
3. ✅ Start Docker (Option A)
4. ✅ Build backend and frontend
5. ✅ Start Config + Eureka + Gateway
6. ✅ Test with browser

### Day 2: Core Services
1. ✅ Start User, Menu, Cart services
2. ✅ Test registration and login
3. ✅ Test menu browsing
4. ✅ Test shopping cart

### Day 3: Orders & Messaging
1. ✅ Setup MySQL sharding
2. ✅ Start Order Service
3. ✅ Test order creation
4. ✅ Check RabbitMQ messages

### Day 4: MongoDB
1. ✅ Switch to MongoDB setup
2. ✅ Start MongoDB Order Service
3. ✅ Test with MongoDB
4. ✅ Compare with MySQL

### Day 5: Advanced
1. ✅ Try Kafka instead of RabbitMQ
2. ✅ Setup MongoDB sharded cluster
3. ✅ Test monitoring tools
4. ✅ Load testing

---

## 🎯 Final Checklist

Before submitting/presenting, make sure:

- [ ] All 7-8 microservices running
- [ ] Eureka dashboard shows all services
- [ ] Frontend accessible at localhost:3000
- [ ] Can register and login
- [ ] Can browse menu and add to cart
- [ ] Can place orders
- [ ] RabbitMQ receiving messages
- [ ] MySQL sharding working (check stats endpoint)
- [ ] MongoDB option tested (if using)
- [ ] All documentation read and understood

---

## 📚 Useful Resources

**Spring Boot Documentation:**
https://spring.io/guides

**Docker Documentation:**
https://docs.docker.com/

**React Documentation:**
https://react.dev/

**MySQL Documentation:**
https://dev.mysql.com/doc/

**MongoDB Documentation:**
https://www.mongodb.com/docs/

**RabbitMQ Documentation:**
https://www.rabbitmq.com/documentation.html

---

## 🆘 Getting Help

If you encounter issues:

1. **Check logs:** Always check service logs first
2. **Read error messages:** They usually tell you what's wrong
3. **Google the error:** Likely someone had the same issue
4. **Check Docker:** Make sure all containers are running
5. **Restart services:** Sometimes a restart fixes it

---

## 🎉 Congratulations!

If you've made it this far, you now have:
- ✅ A complete microservices system running locally
- ✅ Understanding of Spring Cloud architecture
- ✅ Experience with Docker and containers
- ✅ Knowledge of both SQL and NoSQL databases
- ✅ Experience with message queues
- ✅ A full-stack application with React frontend

**You're ready to demo and present your project!** 🚀

---

## 💡 Pro Tips for Windows Users

1. **Use Windows Terminal** (better than cmd.exe)
   - Download from Microsoft Store
   - Supports tabs, better colors

2. **Use PowerShell** instead of Command Prompt
   - More powerful
   - Better command support

3. **Add to PATH permanently** for tools you use often
   - Avoids typing full paths

4. **Use WSL 2** with Docker for better performance
   - Linux containers run faster

5. **Pin Command Prompt to taskbar**
   - You'll be using it a lot!

6. **Create batch files** for common tasks:

```batch
@echo off
REM start-all.bat
cd C:\Projects\online-ordering-system\infrastructure\docker
docker-compose up -d
echo Docker containers started!
echo Now start your microservices manually...
pause
```

Save as `start-all.bat` and double-click to run!

---

**This guide should get you from zero to running in about 2-3 hours on Windows!** ⏱️
