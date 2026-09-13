# M2 E-Commerce Spring Boot Application

A simple Spring Boot e-commerce backend application demonstrating REST APIs for users, products, shopping carts, and orders.

## Technologies Used

- Java
- Spring Boot
- Spring Data JPA
- Hibernate / JPA
- H2 Database
- Maven
- Lombok
- REST APIs
- Postman for API testing

## Base URL

The application uses the default Spring Boot port:

```text
http://localhost:8080
```

API base path:

```text
http://localhost:8080/api
```

---

# Features

## User Management

- Create a user
- Get all users
- Get a user by ID
- Update a user
- Store user address

## Product Management

- Create products
- Get all active products
- Update products
- Search products by name
- Soft-delete products by setting `active = false`

## Cart Management

- Add products to cart
- Increase quantity of an existing cart item
- Check available stock before adding to cart
- Get the user's cart
- Remove products from cart
- Clear cart after successful order creation

## Order Management

- Create an order from the user's cart
- Calculate total order amount
- Create order items
- Reduce product stock when an order is created
- Clear the cart after successful order creation
- Perform order creation and cart clearing in a transaction
- Roll back the transaction if an order operation fails

---

# Project API Structure

```text
/api
├── /users
├── /products
├── /cart
└── /orders
```

---

# API Endpoints

## 1. Create User

### POST

```http
POST http://localhost:8080/api/users
```

### Header

```text
Content-Type: application/json
```

### Body

```json
{
    "fname": "Atul",
    "lname": "Uttam",
    "email": "atul@gmail.com",
    "phone": "9876543210",
    "address": {
        "street": "MG Road",
        "city": "Mathura",
        "country": "India",
        "pincode": "281001"
    }
}
```

---

## 2. Get All Users

### GET

```http
GET http://localhost:8080/api/users
```

No request body is required.

---

## 3. Get One User

Suppose the user ID is `1`.

```http
GET http://localhost:8080/api/users/1
```

---

## 4. Update User

```http
PUT http://localhost:8080/api/users/1
```

### Header

```text
Content-Type: application/json
```

### Body

```json
{
    "fname": "Atul Kumar",
    "lname": "Uttam",
    "email": "atul.kumar@gmail.com",
    "phone": "9999999999",
    "address": {
        "street": "Station Road",
        "city": "Mathura",
        "country": "India",
        "pincode": "281002"
    }
}
```

---

# Product APIs

## 5. Create Product

```http
POST http://localhost:8080/api/products
```

### Header

```text
Content-Type: application/json
```

### Body

```json
{
    "name": "Laptop",
    "description": "Dell Inspiron Laptop",
    "price": 55000,
    "stockQunatity": 10,
    "category": "Electronics",
    "imageUrl": "https://example.com/laptop.jpg"
}
```

Example response:

```json
{
    "id": 1,
    "name": "Laptop",
    "description": "Dell Inspiron Laptop",
    "price": 55000,
    "stockQunatity": 10,
    "category": "Electronics",
    "imageUrl": "https://example.com/laptop.jpg",
    "active": true
}
```

> **Note:** `stockQunatity` is the field name currently used by the project.

---

## 6. Create More Products

### Mobile Phone

```json
{
    "name": "Mobile Phone",
    "description": "Samsung Galaxy Smartphone",
    "price": 25000,
    "stockQunatity": 20,
    "category": "Electronics",
    "imageUrl": "https://example.com/mobile.jpg"
}
```

### Headphones

```json
{
    "name": "Headphones",
    "description": "Wireless Bluetooth Headphones",
    "price": 2500,
    "stockQunatity": 15,
    "category": "Accessories",
    "imageUrl": "https://example.com/headphones.jpg"
}
```

---

## 7. Get All Active Products

```http
GET http://localhost:8080/api/products
```

Only products with:

```text
active = true
```

are returned.

---

## 8. Update Product

```http
PUT http://localhost:8080/api/products/1
```

### Header

```text
Content-Type: application/json
```

### Body

```json
{
    "name": "Dell Laptop",
    "description": "Dell Inspiron 15 Laptop",
    "price": 60000,
    "stockQunatity": 8,
    "category": "Electronics",
    "imageUrl": "https://example.com/dell-laptop.jpg"
}
```

---

## 9. Search Product

Search is performed using the product name.

```http
GET http://localhost:8080/api/products/search?keyword=laptop
```

Other examples:

```http
GET http://localhost:8080/api/products/search?keyword=mobile
```

```http
GET http://localhost:8080/api/products/search?keyword=phone
```

---

## 10. Delete Product

The delete operation is a **soft delete**.

```http
DELETE http://localhost:8080/api/products/1
```

Instead of physically deleting the database record, the application executes:

```java
product.setActive(false);
```

Therefore, the product remains in the database but is not returned by the active-product API.

---

# Cart APIs

## 11. Add Product to Cart

Suppose:

```text
User ID = 1
Product ID = 1
```

### POST

```http
POST http://localhost:8080/api/cart
```

### Headers

```text
Content-Type: application/json
X-User-Id: 1
```

### Body

```json
{
    "productId": 1,
    "quantity": 2
}
```

The service verifies:

1. Product exists.
2. Product is active.
3. Quantity is greater than zero.
4. User exists.
5. Existing cart quantity is considered.
6. Total cart quantity does not exceed available stock.

For example, if stock is `10` and the cart already contains `8`, adding `3` more will be rejected because:

```text
8 + 3 = 11
11 > 10
```

---

## 12. Add Another Product to Cart

```http
POST http://localhost:8080/api/cart
```

Headers:

```text
Content-Type: application/json
X-User-Id: 1
```

Body:

```json
{
    "productId": 2,
    "quantity": 1
}
```

The cart can now contain:

```text
Laptop       2
Mobile       1
```

---

## 13. Get User Cart

```http
GET http://localhost:8080/api/cart/items
```

Header:

```text
X-User-Id: 1
```

No request body is required.

> **Note:** The current implementation returns the `CartItem` entity directly, so the JSON response can contain nested user and product information.

---

## 14. Remove Product from Cart

```http
DELETE http://localhost:8080/api/cart/items/1
```

Header:

```text
X-User-Id: 1
```

No request body is required.

---

# Order API

## 15. Create Order

The order is created from the current user's cart.

```http
POST http://localhost:8080/api/orders
```

Header:

```text
X-User-Id: 1
```

No request body is required.

The order process performs the following operations:

```text
Get Cart
   ↓
Check User
   ↓
Check Product Stock
   ↓
Reduce Product Stock
   ↓
Calculate Order Total
   ↓
Create Order
   ↓
Create Order Items
   ↓
Clear Cart
   ↓
Commit Transaction
```

---

# Transaction Management

Order creation uses Spring's `@Transactional` annotation:

```java
@Transactional
public Optional<OrderResponse> createOrder(String userId) {
    // create order
    // reduce product stock
    // clear cart
}
```

This ensures that order creation and cart clearing are part of the same transaction.

If an exception occurs during the operation:

```text
Reduce Stock
     ↓
Create Order
     ↓
Exception
     ↓
ROLLBACK
```

The database changes are rolled back rather than leaving the system in an inconsistent state.

---

# Product Stock Reduction

When an order is successfully created, the stock is reduced according to the quantity ordered.

Example:

```text
Before Order

Product: Laptop
Stock: 10

Cart:
Laptop × 2
```

After successful order:

```text
Product: Laptop
Stock: 8

Cart:
Empty

Order:
Laptop × 2
```

The stock check is performed again during order creation because a product's stock may have changed after it was added to the cart.

---

# Order Price Calculation

The application treats `OrderItems.price` as the **unit price**.

For example:

```text
Product price = ₹60,000
Quantity      = 2
```

Then:

```text
Subtotal = ₹60,000 × 2
         = ₹1,20,000
```

For multiple products:

```text
Laptop       2 × ₹60,000 = ₹1,20,000
Mobile       1 × ₹25,000 = ₹25,000
-----------------------------------
Total                      ₹1,45,000
```

---

# Complete Postman Testing Sequence

For testing the application, execute the APIs in approximately this order:

| # | Method | URI | Header | Body |
|---|---|---|---|---|
| 1 | POST | `/api/users` | Content-Type | User JSON |
| 2 | GET | `/api/users` | — | — |
| 3 | GET | `/api/users/1` | — | — |
| 4 | PUT | `/api/users/1` | Content-Type | User JSON |
| 5 | POST | `/api/products` | Content-Type | Product JSON |
| 6 | POST | `/api/products` | Content-Type | Product JSON |
| 7 | POST | `/api/products` | Content-Type | Product JSON |
| 8 | GET | `/api/products` | — | — |
| 9 | PUT | `/api/products/1` | Content-Type | Product JSON |
| 10 | GET | `/api/products/search?keyword=laptop` | — | — |
| 11 | POST | `/api/cart` | X-User-Id | Cart JSON |
| 12 | POST | `/api/cart` | X-User-Id | Cart JSON |
| 13 | GET | `/api/cart/items` | X-User-Id | — |
| 14 | DELETE | `/api/cart/items/1` | X-User-Id | — |
| 15 | POST | `/api/cart` | X-User-Id | Cart JSON |
| 16 | POST | `/api/orders` | X-User-Id | — |
| 17 | GET | `/api/cart/items` | X-User-Id | — |
| 18 | DELETE | `/api/products/1` | — | — |

---


## Product GET by ID


```http
GET /api/products/{id}
```

A product can currently be retrieved through:

```http
GET /api/products
```

or searched through:

```http
GET /api/products/search?keyword=laptop
```

## H2 Database

The application uses an in-memory H2 database:

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
```

The data will normally be lost when the application is restarted.

## H2 Console

H2 console is enabled at:

```text
http://localhost:8080/h2-console
```

Database URL:

```text
jdbc:h2:mem:testdb
```

Username:

```text
sa
```

Password:

```text
```

---

# How to Run the Project

## 1. Clone the repository

```bash
git clone <your-github-repository-url>
```

## 2. Open the project

Open the project in IntelliJ IDEA or another Java IDE.

## 3. Build the project

Using Maven:

```bash
mvn clean install
```

## 4. Run the application

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application class from your IDE.

## 5. Test APIs

Use Postman and the API endpoints documented above.

---

# Project Flow

```text
             ┌──────────────┐
             │     User     │
             └──────┬───────┘
                    │
                    ▼
             ┌──────────────┐
             │   Product    │
             └──────┬───────┘
                    │
                    ▼
             ┌──────────────┐
             │     Cart     │
             └──────┬───────┘
                    │
                    ▼
             ┌──────────────┐
             │    Order     │
             └──────┬───────┘
                    │
             ┌──────┴───────┐
             ▼              ▼
       Reduce Stock      Clear Cart
```

---

# Author

**Atul Kumar Uttam**

Java Backend | Spring Boot | Hibernate | REST API
