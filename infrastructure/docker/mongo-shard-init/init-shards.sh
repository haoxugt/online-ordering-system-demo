#!/bin/bash

echo "Waiting for MongoDB instances to be ready..."
sleep 10

echo "Initializing Config Server Replica Set..."
mongosh --host mongo-config-1:27019 --eval "
rs.initiate({
  _id: 'configRS',
  configsvr: true,
  members: [
    { _id: 0, host: 'mongo-config-1:27019' },
    { _id: 1, host: 'mongo-config-2:27019' },
    { _id: 2, host: 'mongo-config-3:27019' }
  ]
})
"

echo "Waiting for config servers..."
sleep 10

echo "Initializing Shard 1 Replica Set..."
mongosh --host mongo-shard1:27018 --eval "
rs.initiate({
  _id: 'shard1RS',
  members: [
    { _id: 0, host: 'mongo-shard1:27018' }
  ]
})
"

echo "Initializing Shard 2 Replica Set..."
mongosh --host mongo-shard2:27018 --eval "
rs.initiate({
  _id: 'shard2RS',
  members: [
    { _id: 0, host: 'mongo-shard2:27018' }
  ]
})
"

echo "Initializing Shard 3 Replica Set..."
mongosh --host mongo-shard3:27018 --eval "
rs.initiate({
  _id: 'shard3RS',
  members: [
    { _id: 0, host: 'mongo-shard3:27018' }
  ]
})
"

echo "Waiting for shard replica sets..."
sleep 15

echo "Adding shards to cluster..."
mongosh --host mongos:27017 --eval "
sh.addShard('shard1RS/mongo-shard1:27018');
sh.addShard('shard2RS/mongo-shard2:27018');
sh.addShard('shard3RS/mongo-shard3:27018');
"

echo "Enabling sharding on orders_db..."
mongosh --host mongos:27017 --eval "
sh.enableSharding('orders_db');
sh.shardCollection('orders_db.orders', { user_id: 'hashed' });

// Create indexes
use orders_db;
db.orders.createIndex({ user_id: 1 });
db.orders.createIndex({ status: 1 });
db.orders.createIndex({ 'timestamps.created': -1 });

print('MongoDB Sharded Cluster Setup Complete!');
print('');
print('Cluster Status:');
"

mongosh --host mongos:27017 --eval "sh.status()"

echo ""
echo "================================================"
echo "MongoDB Sharded Cluster is ready!"
echo "Connect to: mongodb://mongos:27017/orders_db"
echo "From host: mongodb://localhost:27017/orders_db"
echo "================================================"
