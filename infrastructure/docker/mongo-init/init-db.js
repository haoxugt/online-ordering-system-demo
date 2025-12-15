// Create orders database
db = db.getSiblingDB('orders_db');

// Create user
db.createUser({
  user: 'orderservice',
  pwd: 'orderpass',
  roles: [
    { role: 'readWrite', db: 'orders_db' }
  ]
});

// Create indexes for performance
db.orders.createIndex({ "user_id": 1 });
db.orders.createIndex({ "status": 1 });
db.orders.createIndex({ "timestamps.created": -1 });
db.orders.createIndex({ "items.menu_item_id": 1 });
db.orders.createIndex({ "delivery_address.city": 1 });

// Create menu database
db = db.getSiblingDB('menu_db');

db.createUser({
  user: 'menuservice',
  pwd: 'menupass',
  roles: [
    { role: 'readWrite', db: 'menu_db' }
  ]
});

// Create indexes
db.menu_items.createIndex({ "category": 1 });
db.menu_items.createIndex({ "available": 1 });
db.menu_items.createIndex({ "tags": 1 });
db.menu_items.createIndex({ "price": 1 });

print('MongoDB databases initialized successfully!');
