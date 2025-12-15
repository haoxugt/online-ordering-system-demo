# API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
Use JWT token in Authorization header:
```
Authorization: Bearer <token>
```

## Endpoints

### User Service

#### Register User
```http
POST /users/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "+1234567890"
}

Response:
{
  "success": true,
  "message": "Success",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "role": "USER"
  }
}
```

#### Login
```http
POST /users/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}

Response:
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### Menu Service

#### Get All Menu Items
```http
GET /menu/items

Response:
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Margherita Pizza",
      "description": "Classic tomato and mozzarella",
      "price": 12.99,
      "category": "Pizza",
      "imageUrl": null,
      "available": true
    }
  ]
}
```

#### Get Menu Item by ID
```http
GET /menu/items/{id}
```

#### Create Menu Item (Admin)
```http
POST /menu/items
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "name": "Pepperoni Pizza",
  "description": "Spicy pepperoni with cheese",
  "price": 14.99,
  "category": "Pizza",
  "available": true
}
```

#### Update Menu Item (Admin)
```http
PUT /menu/items/{id}
Authorization: Bearer <admin-token>
```

#### Delete Menu Item (Admin)
```http
DELETE /menu/items/{id}
Authorization: Bearer <admin-token>
```

### Cart Service

#### Get Cart
```http
GET /cart/{userId}

Response:
{
  "success": true,
  "data": {
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
}
```

#### Add Item to Cart
```http
POST /cart/{userId}/items
Content-Type: application/json

{
  "menuItemId": 1,
  "quantity": 2
}
```

#### Remove Item from Cart
```http
DELETE /cart/{userId}/items/{menuItemId}
```

#### Clear Cart
```http
DELETE /cart/{userId}
```

### Order Service

#### Create Order
```http
POST /orders
Content-Type: application/json

{
  "userId": 1,
  "paymentMethod": "CREDIT_CARD",
  "deliveryAddress": "123 Main St, City, State 12345"
}

Response:
{
  "success": true,
  "data": {
    "id": 1,
    "userId": 1,
    "totalAmount": 25.98,
    "status": "PENDING",
    "paymentMethod": "CREDIT_CARD",
    "deliveryAddress": "123 Main St, City, State 12345",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

#### Get Order by ID
```http
GET /orders/{id}
```

#### Get User Orders
```http
GET /orders/user/{userId}
```

#### Process Payment
```http
POST /orders/{id}/payment

Response:
{
  "success": true,
  "data": {
    "id": 1,
    "status": "PAID"
  }
}
```

#### Update Order Status
```http
PUT /orders/{id}/status
Content-Type: application/json

{
  "status": "DELIVERED"
}
```

## Error Responses

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

## Status Codes
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 404: Not Found
- 500: Internal Server Error
