# MongoDB Horizontal Sharding - Better Than MySQL!

## YES! MongoDB Has SUPERIOR Horizontal Scaling!

### Key Differences from MySQL:

| Feature | MySQL Sharding | MongoDB Sharding |
|---------|---------------|------------------|
| **Setup** | Manual (you code it) | Automatic (built-in) |
| **Shard Management** | Application handles | MongoDB handles |
| **Adding Shards** | Code changes needed | No code changes |
| **Balancing** | Manual rebalancing | Auto-balancing |
| **Cross-Shard Queries** | Complex to implement | Transparent |
| **Chunk Migration** | Manual | Automatic |

## MongoDB Sharding Architecture

```
                    ┌─────────────────┐
                    │  Application    │
                    │  (Your Code)    │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │    mongos       │ ← Query Router (Load Balancer)
                    │  (Port 27017)   │    Transparent to app!
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  Config Servers │ ← Metadata storage
                    │  (Replica Set)  │    Tracks chunks & shards
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
   ┌────▼─────┐        ┌────▼─────┐        ┌────▼─────┐
   │ Shard 1  │        │ Shard 2  │        │ Shard 3  │
   │ (RS)     │        │ (RS)     │        │ (RS)     │
   │ Chunk    │        │ Chunk    │        │ Chunk    │
   │ 0-3333   │        │ 3334-6666│        │ 6667-9999│
   └──────────┘        └──────────┘        └──────────┘
```

## How MongoDB Sharding Works

### 1. Automatic Data Distribution

**MySQL (Manual):**
```java
// YOU must write this code
int shardIndex = userId % 4;
DataSource ds = getDataSource(shardIndex);
// YOU handle routing
```

**MongoDB (Automatic):**
```java
// Just use normal MongoDB operations!
orderRepository.save(order);
// MongoDB automatically:
// 1. Calculates shard key hash
// 2. Routes to correct shard
// 3. Stores data
// NO CODE NEEDED!
```

### 2. Shard Key

**Choose a field to distribute data:**

```javascript
// Enable sharding on database
sh.enableSharding("orders_db")

// Shard collection by user_id (hashed for even distribution)
sh.shardCollection(
  "orders_db.orders",
  { user_id: "hashed" }
)
```

**MongoDB automatically:**
- Calculates hash of user_id
- Determines which shard
- Routes the document
- Balances chunks across shards

### 3. Chunks

MongoDB divides data into **chunks** (default 64MB):

```
Shard 1: Chunks [A, B, C, D]
Shard 2: Chunks [E, F, G, H]
Shard 3: Chunks [I, J, K, L]
```

**Auto-balancing:**
If Shard 1 gets too many chunks, MongoDB automatically migrates chunks to other shards!

```
Before:
Shard 1: [A, B, C, D, E] ← Too many!
Shard 2: [F, G]
Shard 3: [H, I]

After (auto-balanced):
Shard 1: [A, B, C]
Shard 2: [D, F, G]
Shard 3: [E, H, I]
```

## Complete MongoDB Sharding Setup

### Production Sharded Cluster

#### Step 1: Start Config Servers (3 for HA)

```bash
# Config Server 1
mongod --configsvr --replSet configRS --port 27019 --dbpath /data/config1 --bind_ip localhost

# Config Server 2
mongod --configsvr --replSet configRS --port 27020 --dbpath /data/config2 --bind_ip localhost

# Config Server 3
mongod --configsvr --replSet configRS --port 27021 --dbpath /data/config3 --bind_ip localhost

# Initialize config replica set
mongosh --port 27019
rs.initiate({
  _id: "configRS",
  configsvr: true,
  members: [
    { _id: 0, host: "localhost:27019" },
    { _id: 1, host: "localhost:27020" },
    { _id: 2, host: "localhost:27021" }
  ]
})
```

#### Step 2: Start Shard Servers (3 shards, each with replica set)

```bash
# Shard 1 Replica Set
mongod --shardsvr --replSet shard1RS --port 27030 --dbpath /data/shard1a
mongod --shardsvr --replSet shard1RS --port 27031 --dbpath /data/shard1b
mongod --shardsvr --replSet shard1RS --port 27032 --dbpath /data/shard1c

# Initialize Shard 1
mongosh --port 27030
rs.initiate({
  _id: "shard1RS",
  members: [
    { _id: 0, host: "localhost:27030" },
    { _id: 1, host: "localhost:27031" },
    { _id: 2, host: "localhost:27032" }
  ]
})

# Shard 2 Replica Set
mongod --shardsvr --replSet shard2RS --port 27040 --dbpath /data/shard2a
mongod --shardsvr --replSet shard2RS --port 27041 --dbpath /data/shard2b
mongod --shardsvr --replSet shard2RS --port 27042 --dbpath /data/shard2c

# Initialize Shard 2
mongosh --port 27040
rs.initiate({
  _id: "shard2RS",
  members: [
    { _id: 0, host: "localhost:27040" },
    { _id: 1, host: "localhost:27041" },
    { _id: 2, host: "localhost:27042" }
  ]
})

# Shard 3 Replica Set
mongod --shardsvr --replSet shard3RS --port 27050 --dbpath /data/shard3a
mongod --shardsvr --replSet shard3RS --port 27051 --dbpath /data/shard3b
mongod --shardsvr --replSet shard3RS --port 27052 --dbpath /data/shard3c

# Initialize Shard 3
mongosh --port 27050
rs.initiate({
  _id: "shard3RS",
  members: [
    { _id: 0, host: "localhost:27050" },
    { _id: 1, host: "localhost:27051" },
    { _id: 2, host: "localhost:27052" }
  ]
})
```

#### Step 3: Start mongos (Query Router)

```bash
mongos --configdb configRS/localhost:27019,localhost:27020,localhost:27021 --port 27017
```

#### Step 4: Add Shards to Cluster

```javascript
// Connect to mongos
mongosh --port 27017

// Add all shards
sh.addShard("shard1RS/localhost:27030,localhost:27031,localhost:27032")
sh.addShard("shard2RS/localhost:27040,localhost:27041,localhost:27042")
sh.addShard("shard3RS/localhost:27050,localhost:27051,localhost:27052")

// Verify shards
sh.status()
```

#### Step 5: Enable Sharding on Database

```javascript
// Enable sharding on orders_db
sh.enableSharding("orders_db")

// Shard the orders collection by user_id (hashed)
sh.shardCollection(
  "orders_db.orders",
  { user_id: "hashed" }
)

// Check sharding status
db.orders.getShardDistribution()
```

## Shard Key Strategies

### 1. Hashed Sharding (Recommended for user_id)

```javascript
sh.shardCollection("orders_db.orders", { user_id: "hashed" })
```

**Pros:**
- ✅ Even distribution
- ✅ No hot shards
- ✅ Great for write-heavy workloads

**Cons:**
- ❌ Can't do range queries on shard key efficiently

### 2. Range-Based Sharding

```javascript
sh.shardCollection("orders_db.orders", { created_at: 1 })
```

**Pros:**
- ✅ Good for time-series data
- ✅ Range queries efficient
- ✅ Easy to archive old data

**Cons:**
- ❌ Hot shard for recent data (all writes go to one shard)

### 3. Compound Shard Key (Best of both!)

```javascript
sh.shardCollection("orders_db.orders", { user_id: 1, created_at: 1 })
```

**Pros:**
- ✅ Even distribution by user
- ✅ Time-based queries within user
- ✅ Balanced writes

### 4. Zone Sharding (Geographic)

```javascript
// Create zones for different regions
sh.addShardTag("shard1RS", "US_WEST")
sh.addShardTag("shard2RS", "US_EAST")
sh.addShardTag("shard3RS", "EUROPE")

// Route data by region
sh.addTagRange(
  "orders_db.orders",
  { region: "US_WEST" },
  { region: "US_WEST_MAX" },
  "US_WEST"
)
```

## Application Code (No Changes Needed!)

### Before Sharding

```java
@Autowired
private MongoOrderRepository orderRepository;

public Order createOrder(Order order) {
    return orderRepository.save(order);
}

public List<Order> getUserOrders(Long userId) {
    return orderRepository.findByUserId(userId);
}
```

### After Sharding

```java
@Autowired
private MongoOrderRepository orderRepository;

public Order createOrder(Order order) {
    return orderRepository.save(order);  // Same code!
}

public List<Order> getUserOrders(Long userId) {
    return orderRepository.findByUserId(userId);  // Same code!
}
```

**NO CODE CHANGES! MongoDB handles everything!**

### Connection String Changes

**Before (single server):**
```
mongodb://localhost:27017/orders_db
```

**After (sharded cluster):**
```
mongodb://mongos1:27017,mongos2:27017/orders_db
```

That's it! Just point to mongos routers.

## Monitoring Sharded Cluster

### Check Shard Status

```javascript
sh.status()
```

**Output:**
```
sharding version: {
  "_id": 1,
  "minCompatibleVersion": 5,
  "currentVersion": 6
}

shards:
  shard1RS { host: "shard1RS/localhost:27030,27031,27032", state: 1 }
  shard2RS { host: "shard2RS/localhost:27040,27041,27042", state: 1 }
  shard3RS { host: "shard3RS/localhost:27050,27051,27052", state: 1 }

databases:
  orders_db.orders
    shard key: { "user_id": "hashed" }
    chunks:
      shard1RS  8
      shard2RS  7
      shard3RS  9
```

### Check Data Distribution

```javascript
db.orders.getShardDistribution()
```

**Output:**
```
Shard shard1RS at shard1RS/localhost:27030,27031,27032
  data: 1.5GB docs: 500000 chunks: 8
  estimated data per chunk: 187.5MB
  estimated docs per chunk: 62500

Shard shard2RS at shard2RS/localhost:27040,27041,27042
  data: 1.4GB docs: 480000 chunks: 7
  estimated data per chunk: 200MB
  estimated docs per chunk: 68571

Shard shard3RS at shard3RS/localhost:27050,27051,27052
  data: 1.6GB docs: 520000 chunks: 9
  estimated data per chunk: 177.7MB
  estimated docs per chunk: 57777

Totals
  data: 4.5GB docs: 1500000 chunks: 24
  Shard shard1RS contains 33.33% data, 33.33% docs, 33.33% chunks
  Shard shard2RS contains 31.11% data, 32.00% docs, 29.17% chunks
  Shard shard3RS contains 35.56% data, 34.67% docs, 37.50% chunks
```

### Monitor Balancer

```javascript
// Check if balancer is running
sh.getBalancerState()

// Check balancer status
sh.balancerCollectionStatus("orders_db.orders")
```

## Adding More Shards (Zero Downtime!)

### Add Shard 4

```bash
# Start new shard replica set
mongod --shardsvr --replSet shard4RS --port 27060 --dbpath /data/shard4a
mongod --shardsvr --replSet shard4RS --port 27061 --dbpath /data/shard4b
mongod --shardsvr --replSet shard4RS --port 27062 --dbpath /data/shard4c

# Initialize
mongosh --port 27060
rs.initiate({
  _id: "shard4RS",
  members: [
    { _id: 0, host: "localhost:27060" },
    { _id: 1, host: "localhost:27061" },
    { _id: 2, host: "localhost:27062" }
  ]
})

# Add to cluster (application stays running!)
mongosh --port 27017
sh.addShard("shard4RS/localhost:27060,27061,27062")
```

**MongoDB automatically:**
1. Adds shard to cluster
2. Starts migrating chunks
3. Balances data across all 4 shards
4. **Application continues running!**

## Performance Benefits

### Write Performance

**Single Server:**
- Max: ~10,000 writes/sec

**3-Shard Cluster:**
- Max: ~30,000 writes/sec (3x improvement!)

**6-Shard Cluster:**
- Max: ~60,000 writes/sec (6x improvement!)

**Linear scaling!**

### Read Performance

**Targeted Query (has shard key):**
```javascript
// Goes to ONE shard only
db.orders.find({ user_id: 123 })
```
⚡ Fast! (single shard query)

**Scatter-Gather Query (no shard key):**
```javascript
// Goes to ALL shards
db.orders.find({ status: "PENDING" })
```
⚡ Still fast! (parallel execution)

## Comparison: MongoDB vs MySQL Sharding

### MySQL Sharding (Your Implementation)

```java
// Manual routing in application
public Order save(Order order) {
    int shard = order.getUserId() % 4;
    DataSource ds = shardConfig.getDataSource(shard);
    // Save to specific shard
}

// Cross-shard query
public List<Order> findByStatus(String status) {
    List<Order> all = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
        // Query each shard
        // Merge results
    }
    return all;
}

// Adding 5th shard
// Need to: rewrite code, re-shard data manually
```

### MongoDB Sharding (Automatic)

```java
// No routing code needed!
public Order save(Order order) {
    return repository.save(order);
    // MongoDB handles routing automatically
}

// Cross-shard query (transparent)
public List<Order> findByStatus(String status) {
    return repository.findByStatus(status);
    // MongoDB queries all shards automatically
}

// Adding 5th shard
// Just: sh.addShard("shard5RS/...")
// MongoDB rebalances automatically, NO CODE CHANGES!
```

## Docker Compose for Sharded MongoDB

```yaml
version: '3.8'
services:
  # Config Servers
  config1:
    image: mongo:7.0
    command: mongod --configsvr --replSet configRS --port 27019
    volumes:
      - config1:/data/db
  
  config2:
    image: mongo:7.0
    command: mongod --configsvr --replSet configRS --port 27019
    volumes:
      - config2:/data/db
  
  config3:
    image: mongo:7.0
    command: mongod --configsvr --replSet configRS --port 27019
    volumes:
      - config3:/data/db
  
  # Shard 1
  shard1a:
    image: mongo:7.0
    command: mongod --shardsvr --replSet shard1RS --port 27018
    volumes:
      - shard1a:/data/db
  
  # Shard 2
  shard2a:
    image: mongo:7.0
    command: mongod --shardsvr --replSet shard2RS --port 27018
    volumes:
      - shard2a:/data/db
  
  # Shard 3
  shard3a:
    image: mongo:7.0
    command: mongod --shardsvr --replSet shard3RS --port 27018
    volumes:
      - shard3a:/data/db
  
  # mongos Router
  mongos:
    image: mongo:7.0
    command: mongos --configdb configRS/config1:27019,config2:27019,config3:27019 --port 27017
    ports:
      - "27017:27017"
    depends_on:
      - config1
      - config2
      - config3

volumes:
  config1:
  config2:
  config3:
  shard1a:
  shard2a:
  shard3a:
```

## Summary: MongoDB Sharding Advantages

### vs MySQL Manual Sharding

| Feature | MySQL (Manual) | MongoDB (Auto) |
|---------|---------------|----------------|
| **Setup Complexity** | High (write code) | Medium (config) |
| **Code Changes** | Required | None |
| **Adding Shards** | Code rewrite | Config change |
| **Balancing** | Manual | Automatic |
| **Cross-Shard Queries** | Complex code | Transparent |
| **Hot Shard Detection** | Manual | Automatic |
| **Chunk Migration** | Manual scripting | Automatic |
| **Zero-Downtime Scaling** | Difficult | Built-in |

### Best Practices

1. **Choose Shard Key Carefully**
   - High cardinality (many unique values)
   - Even distribution
   - Used in most queries

2. **Use Hashed Sharding for IDs**
   - Prevents hot shards
   - Even distribution

3. **Monitor Balancer**
   - Check chunk distribution
   - Ensure no hot shards

4. **Plan Capacity**
   - Add shards before needed
   - Allow time for balancing

5. **Use Zone Sharding for Geographic Data**
   - Keep data close to users
   - Comply with regulations

## Conclusion

**MongoDB sharding is SUPERIOR to manual MySQL sharding because:**

✅ **Automatic** - No application code needed
✅ **Transparent** - Same queries work before/after
✅ **Self-Balancing** - Distributes data automatically
✅ **Zero-Downtime Scaling** - Add shards without restart
✅ **Built-in** - Not something you implement, it's a feature
✅ **Production-Tested** - Used by Netflix, eBay, Adobe, etc.

**MySQL sharding is good for:**
- Learning how sharding works
- Full control over distribution
- Specific custom logic

**MongoDB sharding is better for:**
- Production systems
- Rapid scaling needs
- Minimal maintenance
- Less code complexity

Your implementation shows BOTH approaches - excellent! 🎉
