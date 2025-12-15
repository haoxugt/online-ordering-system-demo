# MySQL vs MongoDB - Which Parts Should Use MongoDB?

## Analysis of Each Service

### ✅ RECOMMENDED FOR MONGODB

#### 1. **Order Service** - BEST CANDIDATE
**Why MongoDB:**
- ✅ Orders have variable structure (different items per order)
- ✅ Items stored as JSON/embedded documents
- ✅ Frequently changing schema (new fields like promo codes, discounts)
- ✅ High write volume (order creation)
- ✅ Flexible document structure (different order types)
- ✅ Natural sharding support (built-in)
- ✅ Read-heavy for user's order history

**MongoDB Features to Use:**
- Embedded documents for order items
- TTL indexes for auto-archiving old orders
- Change streams for real-time updates
- Native sharding by user_id
- Aggregation pipeline for analytics

**Document Structure:**
```json
{
  "_id": ObjectId("..."),
  "userId": 123,
  "items": [
    {
      "menuItemId": 1,
      "name": "Pizza",
      "price": 12.99,
      "quantity": 2,
      "customizations": {
        "size": "Large",
        "toppings": ["Extra Cheese", "Pepperoni"]
      }
    }
  ],
  "totalAmount": 25.98,
  "status": "PENDING",
  "paymentMethod": "CREDIT_CARD",
  "deliveryAddress": {
    "street": "123 Main St",
    "city": "San Francisco",
    "zip": "94102",
    "coordinates": {
      "lat": 37.7749,
      "lng": -122.4194
    }
  },
  "timestamps": {
    "created": ISODate("2024-12-08T10:30:00Z"),
    "updated": ISODate("2024-12-08T10:35:00Z"),
    "delivered": null
  },
  "metadata": {
    "ipAddress": "192.168.1.1",
    "userAgent": "Mozilla/5.0...",
    "promoCode": "SAVE20"
  }
}
```

#### 2. **Menu Service** - GOOD CANDIDATE
**Why MongoDB:**
- ✅ Rich menu item attributes (ingredients, allergens, nutritional info)
- ✅ Varying attributes per category (Pizza has size, Beverage has volume)
- ✅ Frequent updates to availability, prices
- ✅ Image URLs and multiple media files
- ✅ Localization (different languages)
- ✅ Tags and categories (array fields)

**Document Structure:**
```json
{
  "_id": ObjectId("..."),
  "name": "Margherita Pizza",
  "category": "Pizza",
  "description": {
    "en": "Classic tomato and mozzarella",
    "es": "Tomate y mozzarella clásica"
  },
  "price": 12.99,
  "available": true,
  "images": [
    "https://cdn.example.com/pizza1.jpg",
    "https://cdn.example.com/pizza2.jpg"
  ],
  "attributes": {
    "sizes": ["Small", "Medium", "Large"],
    "crust": ["Thin", "Thick", "Stuffed"],
    "vegetarian": true,
    "spicy": false
  },
  "ingredients": ["Tomato", "Mozzarella", "Basil"],
  "allergens": ["Dairy", "Gluten"],
  "nutritionalInfo": {
    "calories": 250,
    "protein": 12,
    "carbs": 30,
    "fat": 8
  },
  "tags": ["Popular", "Classic", "Italian"],
  "ratings": {
    "average": 4.5,
    "count": 150
  },
  "createdAt": ISODate("2024-01-01T00:00:00Z"),
  "updatedAt": ISODate("2024-12-08T00:00:00Z")
}
```

#### 3. **Analytics/Reporting Service** (NEW) - EXCELLENT
**Why MongoDB:**
- ✅ Aggregation pipeline for complex analytics
- ✅ Time-series data for sales trends
- ✅ Flexible event logging
- ✅ Real-time dashboard queries
- ✅ MapReduce for business intelligence

### ⚠️ KEEP IN MYSQL

#### 1. **User Service** - KEEP MYSQL
**Why MySQL:**
- ✅ Fixed schema (username, email, password)
- ✅ Strict ACID transactions for authentication
- ✅ Unique constraints critical (username, email)
- ✅ Relational integrity important
- ✅ Simple, predictable queries
- ✅ Security-critical data (passwords)

#### 2. **Cart Service** - KEEP REDIS
**Why Redis:**
- ✅ Already in Redis (best choice)
- ✅ Temporary data (24hr TTL)
- ✅ Super fast read/write
- ✅ No need for durability

## Hybrid Architecture Recommendation

### OPTION 1: MongoDB for Orders + Menu (RECOMMENDED)

```
┌─────────────┐
│ User Service│──── MySQL (user_db)
└─────────────┘

┌─────────────┐
│ Menu Service│──── MongoDB (menu_db)
└─────────────┘     - Flexible schema
                    - Rich attributes
                    - Fast queries

┌─────────────┐
│ Cart Service│──── Redis
└─────────────┘     - Temporary storage

┌──────────────┐
│ Order Service│──── MongoDB (orders_db)
└──────────────┘    - Native sharding
                    - Embedded documents
                    - Flexible structure
```

**Benefits:**
- Best tool for each job
- Leverage MongoDB's strengths
- Keep MySQL where it's best
- Reduce complexity vs all MongoDB

### OPTION 2: Polyglot Persistence (ADVANCED)

```
MySQL:     Users (authentication)
MongoDB:   Orders, Menu, Reviews
Redis:     Cart, Sessions, Cache
ElasticSearch: Search functionality
PostgreSQL: Analytics warehouse
```

## Migration Strategy

### Phase 1: Add MongoDB to Order Service
1. Keep existing MySQL order_db
2. Add MongoDB alongside
3. Dual-write to both databases
4. Read from MongoDB
5. Validate data consistency
6. Remove MySQL after validation

### Phase 2: Migrate Menu to MongoDB
1. Export menu_items from MySQL
2. Transform to MongoDB documents
3. Import with rich attributes
4. Update Menu Service code
5. Switch over

### Phase 3: Add Analytics Service
1. Create new MongoDB analytics_db
2. Stream order events
3. Build aggregation pipelines
4. Create dashboards

## Performance Comparison

### Order Queries

**MySQL (Current):**
```sql
SELECT * FROM orders WHERE user_id = 123;
-- Join to get items (stored as JSON string)
-- Parse JSON in application
```
⏱️ ~50ms

**MongoDB (Proposed):**
```javascript
db.orders.find({ userId: 123 }).sort({ "timestamps.created": -1 })
// Items already embedded, no parsing needed
```
⏱️ ~10ms (5x faster)

### Complex Analytics

**MySQL:**
```sql
-- Multiple joins, subqueries
-- JSON parsing
-- Application-level aggregation
```
⏱️ ~2000ms

**MongoDB:**
```javascript
db.orders.aggregate([
  { $match: { "timestamps.created": { $gte: ISODate("2024-01-01") }}},
  { $unwind: "$items" },
  { $group: { 
      _id: "$items.menuItemId",
      totalSold: { $sum: "$items.quantity" },
      revenue: { $sum: { $multiply: ["$items.price", "$items.quantity"] }}
  }},
  { $sort: { revenue: -1 }},
  { $limit: 10 }
])
```
⏱️ ~300ms (6x faster)

## Code Changes Required

### MongoDB Dependencies (pom.xml)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### Configuration (application.yml)
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/orders_db
      # Sharding already handled by MongoDB cluster
```

### Entity Class
```java
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private Long userId;
    @Field("items")
    private List<OrderItem> items;  // Embedded documents
    // No need for JSON serialization!
}
```

### Repository
```java
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(String status);
    
    // Complex queries with @Query annotation
    @Query("{ 'timestamps.created': { $gte: ?0, $lte: ?1 } }")
    List<Order> findOrdersInDateRange(Date start, Date end);
}
```

## When to Use What

### Use MySQL When:
- ✅ Fixed, predictable schema
- ✅ Strong ACID requirements
- ✅ Complex joins needed
- ✅ Data integrity critical
- ✅ Traditional relational model fits

### Use MongoDB When:
- ✅ Schema varies by document
- ✅ Nested/embedded data
- ✅ Rapid schema evolution
- ✅ High write throughput
- ✅ Flexible queries needed
- ✅ Aggregation pipelines useful
- ✅ Horizontal scaling important

### Use Redis When:
- ✅ Temporary data
- ✅ Sub-millisecond latency needed
- ✅ Simple key-value storage
- ✅ Caching

## Recommendation Summary

### Immediate Migration (High Value)
1. **Order Service → MongoDB** 
   - Biggest benefit
   - Natural fit
   - Easy migration

2. **Menu Service → MongoDB**
   - Better flexibility
   - Richer data model

### Keep as MySQL
1. **User Service**
   - Security critical
   - Simple schema
   - No benefit from change

### Keep as Redis
1. **Cart Service**
   - Perfect fit
   - Already optimal

## Final Architecture

```
┌──────────────────────────────────────────┐
│         API Gateway (8080)                │
└──────────────┬───────────────────────────┘
               │
       ┌───────┼────────┬──────────┐
       │       │        │          │
   ┌───▼──┐ ┌─▼───┐ ┌──▼──┐  ┌───▼────┐
   │ User │ │Menu │ │Cart │  │ Order  │
   │ Svc  │ │ Svc │ │ Svc │  │  Svc   │
   └───┬──┘ └──┬──┘ └──┬──┘  └────┬───┘
       │       │       │           │
   ┌───▼──┐ ┌─▼──────▼─┐     ┌────▼──────┐
   │MySQL │ │  Redis   │     │  MongoDB  │
   │Users │ │Cart+Cache│     │Orders+Menu│
   └──────┘ └──────────┘     └───────────┘
```

This is the OPTIMAL hybrid architecture!
