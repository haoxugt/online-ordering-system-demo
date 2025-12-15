# 🚀 Quick Reference - macOS

## **One-Time Setup**

```bash
# 1. Install prerequisites
brew install openjdk@17 maven node@20
brew install --cask docker

# 2. Setup paths
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 3. Extract project
cd ~/Documents
unzip online-ordering-system.zip
cd online-ordering-system

# 4. Make scripts executable
chmod +x *.sh
```

---

## **Daily Startup**

```bash
# 1. Start Docker Desktop
open -a Docker

# Wait 30 seconds for Docker to start

# 2. Start infrastructure
cd ~/Documents/online-ordering-system/infrastructure/docker
docker-compose up -d

# Wait 30 seconds for MySQL to initialize

# 3. Setup databases (first time only)
cd ~/Documents/online-ordering-system
./setup-databases-macos.sh

# 4. Build backend (first time only)
cd backend
mvn clean install

# 5. Start all services
cd ~/Documents/online-ordering-system
./start-services-macos.sh

# 6. Start frontend
cd frontend
npm install  # First time only
npm start
```

---

## **Quick Commands**

### **Check Docker**
```bash
# Is Docker running?
docker ps

# View logs
docker-compose logs mysql
docker-compose logs redis
```

### **Check Services**
```bash
# Eureka Dashboard
open http://localhost:8761

# API Gateway health
curl http://localhost:8080/actuator/health

# All services health
for port in 8080 8081 8082 8083 8084; do
  echo "Port $port:"
  curl -s http://localhost:$port/actuator/health | grep status
done
```

### **Database Access**
```bash
# MySQL
docker exec -it mysql mysql -u root -ppassword

# Redis
docker exec -it redis redis-cli

# MongoDB (if using)
docker exec -it mongodb mongosh
```

### **View Logs**
```bash
# Service logs are in the terminal tabs
# Or use:
tail -f backend/*/target/*.log
```

---

## **Common Tasks**

### **Restart Everything**
```bash
# Stop all
docker-compose down
# Close all service terminal tabs

# Start infrastructure
docker-compose up -d

# Restart services
./start-services-macos.sh
```

### **Clean Build**
```bash
cd backend
mvn clean install -DskipTests
```

### **Clear Cache**
```bash
# Maven cache
rm -rf ~/.m2/repository

# npm cache
cd frontend
rm -rf node_modules package-lock.json
npm install

# Redis cache
docker exec -it redis redis-cli FLUSHALL
```

---

## **Troubleshooting**

### **Port Already in Use**
```bash
# Find process
lsof -i :8080

# Kill it
kill -9 <PID>
```

### **Docker Issues**
```bash
# Restart Docker Desktop
killall Docker && open -a Docker

# Reset containers
docker-compose down
docker-compose up -d
```

### **Build Failures**
```bash
# Check Java version
java -version  # Must be 17+

# Set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Clean and rebuild
cd backend
mvn clean install -U
```

---

## **Useful URLs**

| Service | URL |
|---------|-----|
| **Frontend** | http://localhost:3000 |
| **Eureka Dashboard** | http://localhost:8761 |
| **API Gateway** | http://localhost:8080 |
| **RabbitMQ Management** | http://localhost:15672 (guest/guest) |
| **Config Server** | http://localhost:8888 |

---

## **Test Endpoints**

### **Register User**
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"pass123","email":"test@test.com","phone":"555-1234"}'
```

### **Login**
```bash
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"pass123"}'
```

### **Get Menu**
```bash
curl http://localhost:8082/api/menu/items
```

### **Add to Cart**
```bash
curl -X POST http://localhost:8083/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"menuItemId":1,"name":"Pizza","price":12.99,"quantity":2}'
```

---

## **Stopping Everything**

```bash
# Stop services (close terminal tabs)
# Or press Ctrl+C in each tab

# Stop Docker
cd infrastructure/docker
docker-compose down

# Stop frontend
# Press Ctrl+C in frontend terminal
```

---

## **Aliases (Add to ~/.zshrc)**

```bash
# Online Ordering System Aliases
alias oos-start="cd ~/Documents/online-ordering-system && ./start-services-macos.sh"
alias oos-docker="cd ~/Documents/online-ordering-system/infrastructure/docker && docker-compose up -d"
alias oos-stop="cd ~/Documents/online-ordering-system/infrastructure/docker && docker-compose down"
alias oos-build="cd ~/Documents/online-ordering-system/backend && mvn clean install"
alias oos-frontend="cd ~/Documents/online-ordering-system/frontend && npm start"
alias oos-logs="cd ~/Documents/online-ordering-system/infrastructure/docker && docker-compose logs -f"
alias oos-health="curl http://localhost:8080/actuator/health"
```

After adding, reload:
```bash
source ~/.zshrc
```

Then use:
```bash
oos-start      # Start all services
oos-frontend   # Start frontend
oos-health     # Check health
```

---

## **Service Status Checklist**

- [ ] Docker Desktop running
- [ ] MySQL container running (docker ps)
- [ ] Redis container running  
- [ ] RabbitMQ container running
- [ ] Config Server started (8888)
- [ ] Eureka Server started (8761)
- [ ] API Gateway started (8080)
- [ ] User Service started (8081)
- [ ] Menu Service started (8082)
- [ ] Cart Service started (8083)
- [ ] Order Service started (8084)
- [ ] Frontend running (3000)
- [ ] All services registered in Eureka
- [ ] All health endpoints return UP

---

## **macOS-Specific Tips**

### **Terminal Management**
```bash
# Use iTerm2 for better terminal experience
brew install --cask iterm2

# Or use tmux for multiple panes
brew install tmux
```

### **Monitor Resources**
```bash
# Docker stats
docker stats

# System monitor
open -a "Activity Monitor"
```

### **Command Line Tools**
```bash
# Watch command (like Linux watch)
brew install watch

# Watch service health
watch -n 5 'curl -s http://localhost:8080/actuator/health'
```

---

**Happy Coding! 🎉**
