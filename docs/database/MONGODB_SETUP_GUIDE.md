# MongoDB Setup Guide

## Why MongoDB for Orders and Menu?

### Order Service Benefits
- ✅ **Flexible Schema**: Orders can have varying structures
- ✅ **Embedded Documents**: Items stored natively, no JSON parsing
- ✅ **Native Sharding**: Built-in horizontal scaling
- ✅ **Aggregation Pipeline**: Complex analytics queries
- ✅ **Change Streams**: Real-time order updates

### Menu Service Benefits
- ✅ **Rich Attributes**: Varying properties per food category
- ✅ **Array Fields**: Tags, ingredients, allergens
- ✅ **Localization**: Multiple language support
- ✅ **Flexible Updates**: Easy schema evolution

## Installation

### Option 1: Docker (Recommended)

```bash
cd infrastructure/docker
docker-compose -f docker-compose-with-mongodb.yml up -d
```

This starts:
- MySQL (port 3306) - For users
- MongoDB (port 27017) - For orders and menu
- Redis (port 6379) - For cart and cache
- RabbitMQ (ports 5672, 15672)

### Option 2: Local MongoDB

```bash
# Install MongoDB 7.0
brew install mongodb-community@7.0  # macOS
# OR
sudo apt-get install mongodb-org     # Ubuntu

# Start MongoDB
brew services start mongodb-community@7.0
# OR
sudo systemctl start mongod
```

## Initialize Databases

### Using Mongo Shell

```bash
mongosh mongodb://localhost:27017

# Create orders database
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

# Create menu database
use menu_db
db.createUser({
  user: 'menuservice',
  pwd: 'menupass',
  roles: [{ role: 'readWrite', db: 'menu_db' }]
})

db.menu_items.createIndex({ "category": 1 })
db.menu_items.createIndex({ "available": 1 })
```

## MongoDB Sharding Setup (Production)

### 1. Start Config Servers (3 instances)

```bash
mongod --configsvr --replSet configReplSet --port 27019 --dbpath /data/configdb1
mongod --configsvr --replSet configReplSet --port 27020 --dbpath /data/configdb2
mongod --configsvr --replSet configReplSet --port 27021 --dbpath /data/configdb3
```

### 2. Initialize Config Server Replica Set

```javascript
rs.initiate({
  _id: "configReplSet",
  configsvr: true,
  members: [
    { _id: 0, host: "localhost:27019" },
    { _id: 1, host: "localhost:27020" },
    { _id: 2, host: "localhost:27021" }
  ]
})
```

### 3. Start Shard Servers (2 shards, each with replica set)

```bash
# Shard 1
mongod --shardsvr --replSet shard1 --port 27030 --dbpath /data/shard1a
mongod --shardsvr --replSet shard1 --port 27031 --dbpath /data/shard1b

# Shard 2
mongod --shardsvr --replSet shard2 --port 27040 --dbpath /data/shard2a
mongod --shardsvr --replSet shard2 --port 27041 --dbpath /data/shard2b
```

### 4. Start mongos (Query Router)

```bash
mongos --configdb configReplSet/localhost:27019,localhost:27020,localhost:27021 --port 27017
```

### 5. Add Shards

```javascript
sh.addShard("shard1/localhost:27030,localhost:27031")
sh.addShard("shard2/localhost:27040,localhost:27041")
```

### 6. Enable Sharding on Database

```javascript
sh.enableSharding("orders_db")
```

### 7. Shard the Orders Collection

```javascript
// Shard by user_id (hashed for even distribution)
sh.shardCollection("orders_db.orders", { user_id: "hashed" })

// OR shard by compound key
sh.shardCollection("orders_db.orders", { user_id: 1, "_id": 1 })
```

## Document Examples

### Order Document

```javascript
{
  "_id": ObjectId("6576f1a2b3c4d5e6f7a8b9c0"),
  "user_id": 123,
  "items": [
    {
      "menu_item_id": 1,
      "name": "Margherita Pizza",
      "price": NumberDecimal("12.99"),
      "quantity": 2,
      "subtotal": NumberDecimal("25.98"),
      "customizations": {
        "size": "Large",
        "toppings": ["Extra Cheese", "Basil"],
        "notes": "Well done"
      }
    }
  ],
  "total_amount": NumberDecimal("25.98"),
  "status": "PAID",
  "payment_method": "CREDIT_CARD",
  "delivery_address": {
    "street": "123 Main St",
    "city": "San Francisco",
    "state": "CA",
    "zip": "94102",
    "coordinates": {
      "latitude": 37.7749,
      "longitude": -122.4194
    }
  },
  "timestamps": {
    "created": ISODate("2024-12-08T10:30:00Z"),
    "updated": ISODate("2024-12-08T10:35:00Z"),
    "paid": ISODate("2024-12-08T10:32:00Z"),
    "delivered": null
  },
  "metadata": {
    "ip_address": "192.168.1.1",
    "user_agent": "Mozilla/5.0...",
    "promo_code": "SAVE20",
    "referral_source": "mobile_app"
  }
}
```

### Menu Item Document

```javascript
{
  "_id": ObjectId("6576f2b3c4d5e6f7a8b9c123"),
  "name": "Margherita Pizza",
  "category": "Pizza",
  "description": {
    "en": "Classic tomato and mozzarella",
    "es": "Tomate y mozzarella clásico",
    "fr": "Tomate et mozzarella classique"
  },
  "price": NumberDecimal("12.99"),
  "available": true,
  "images": [
    "https://cdn.example.com/pizza1.jpg",
    "https://cdn.example.com/pizza2.jpg"
  ],
  "attributes": {
    "sizes": ["Small", "Medium", "Large"],
    "crust_types": ["Thin", "Thick", "Stuffed"],
    "vegetarian": true,
    "vegan": false,
    "gluten_free": false,
    "spicy_level": 0
  },
  "ingredients": ["Tomato Sauce", "Mozzarella", "Basil", "Olive Oil"],
  "allergens": ["Dairy", "Gluten"],
  "nutritional_info": {
    "calories": 250,
    "protein": 12,
    "carbohydrates": 30,
    "fat": 8,
    "fiber": 2,
    "sodium": 600
  },
  "tags": ["Popular", "Classic", "Italian", "Vegetarian"],
  "ratings": {
    "average": 4.5,
    "count": 150,
    "distribution": {
      "5": 80,
      "4": 50,
      "3": 15,
      "2": 3,
      "1": 2
    }
  },
  "created_at": ISODate("2024-01-01T00:00:00Z"),
  "updated_at": ISODate("2024-12-08T00:00:00Z")
}
```

## Useful Queries

### Find Recent Orders

```javascript
db.orders.find({ user_id: 123 })
  .sort({ "timestamps.created": -1 })
  .limit(10)
```

### Find Orders by Status

```javascript
db.orders.find({ status: "PENDING" })
```

### Find Orders in Date Range

```javascript
db.orders.find({
  "timestamps.created": {
    $gte: ISODate("2024-01-01"),
    $lte: ISODate("2024-12-31")
  }
})
```

### Find Orders with Specific Item

```javascript
db.orders.find({
  "items.menu_item_id": 1
})
```

### Aggregation: Revenue by Day

```javascript
db.orders.aggregate([
  {
    $match: {
      status: "PAID",
      "timestamps.paid": {
        $gte: ISODate("2024-12-01"),
        $lte: ISODate("2024-12-31")
      }
    }
  },
  {
    $group: {
      _id: {
        $dateToString: { format: "%Y-%m-%d", date: "$timestamps.paid" }
      },
      total_revenue: { $sum: "$total_amount" },
      order_count: { $sum: 1 }
    }
  },
  { $sort: { "_id": 1 } }
])
```

### Aggregation: Top Selling Items

```javascript
db.orders.aggregate([
  { $match: { status: "PAID" } },
  { $unwind: "$items" },
  {
    $group: {
      _id: "$items.menu_item_id",
      name: { $first: "$items.name" },
      total_quantity: { $sum: "$items.quantity" },
      total_revenue: { $sum: "$items.subtotal" }
    }
  },
  { $sort: { total_revenue: -1 } },
  { $limit: 10 }
])
```

## Performance Tips

### 1. Use Indexes

```javascript
// Compound index for common queries
db.orders.createIndex({ user_id: 1, status: 1, "timestamps.created": -1 })

// Text index for search
db.menu_items.createIndex({ name: "text", description: "text" })
```

### 2. Use Projection

```javascript
// Only return needed fields
db.orders.find(
  { user_id: 123 },
  { items: 1, total_amount: 1, status: 1 }
)
```

### 3. Use Explain

```javascript
// Analyze query performance
db.orders.find({ user_id: 123 }).explain("executionStats")
```

## Monitoring

### Check Shard Distribution

```javascript
sh.status()
```

### Database Statistics

```javascript
db.stats()
db.orders.stats()
```

### Current Operations

```javascript
db.currentOp()
```

## Backup and Restore

### Backup

```bash
mongodump --uri="mongodb://localhost:27017/orders_db" --out=/backup/
```

### Restore

```bash
mongorestore --uri="mongodb://localhost:27017" /backup/
```

## Connection String for Application

### Development (Single Node)

```
mongodb://localhost:27017/orders_db
```

### Production (Sharded Cluster)

```
mongodb://mongos1:27017,mongos2:27017/orders_db?replicaSet=rs0
```

### With Authentication

```
mongodb://orderservice:orderpass@localhost:27017/orders_db?authSource=orders_db
```

## Conclusion

MongoDB provides:
- ✅ Flexible schema for varying order structures
- ✅ Native sharding for horizontal scaling
- ✅ Powerful aggregation for analytics
- ✅ Better performance for document-oriented data
- ✅ Easier to work with nested data (no joins!)

Perfect fit for Order and Menu services!
