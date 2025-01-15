# Razorpay Integration Guide

### Step 1: Setup Razorpay Account

---

### Step 2: Generate a new API key
Generate a new API key and save the **Key ID** and **Key Secret** securely for Test/Live Mode.

---

### Step 3: Create Spring Boot Project

#### Add Necessary Dependencies
To create a Spring Boot project, include the following dependencies:

1. **Spring Boot Starter Web**
2. **Spring Boot Data JPA**
3. **MySQL Driver**
4. **Lombok**
5. **Thymeleaf**
6. **Razorpay Java Dependency**

Add the Razorpay dependency in the `pom.xml` file:

```xml
<dependency>
    <groupId>com.razorpay</groupId>
    <artifactId>razorpay-java</artifactId>
    <version>1.3.0</version>
</dependency>
```

---

### Configurations

#### Application Configuration
Add the following properties to your `application.properties` file:

```properties
spring.application.name = RazorPayIntegration
server.port = 8081

# Database Configurations
spring.datasource.url = jdbc:mysql://localhost:3306/cosmostaker_orders
spring.datasource.username = root
spring.datasource.password = root
spring.datasource.driver-class-name = com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto = update
spring.jpa.show-sql = true
spring.jpa.properties.hibernate.format_sql = true
spring.jpa.properties.hibernate.enable_lazy_load_no_trans = true

# Razorpay Configurations
razorpay.key = rzp_test_abczUJfEMHgyOt
razorpay.secret = klvUKclzmiCp5KAPirBgeI6r
```

---

### Step 4: Create Order

```java
@Service
public class OrderServiceImpl implements OrderService {

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(RazorpayClient razorpayClient, OrderRepository orderRepository) {
        this.razorpayClient = razorpayClient;
        this.orderRepository = orderRepository;
    }

    @Override
    public Orders createOrder(Long userId, Long productId, Double amount) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_" + userId);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            Orders order = new Orders();
            order.setUserId(userId);
            order.setProductId(productId);
            order.setAmount(amount);
            order.setRazorpayOrderId(razorpayOrder.get("id"));
            order.setOrderStatus(razorpayOrder.get("status"));
            return orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order", e);
        }
    }

    @Override
    public void handlePaymentCallback(OrderCallbackDto callback) {
        Orders order = orderRepository.findByRazorpayOrderId(callback.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (callback.getRazorpayPaymentId() != null && callback.getRazorpaySignature() != null) {
            order.setRazorpayPaymentId(callback.getRazorpayPaymentId());
            order.setRazorpaySignature(callback.getRazorpaySignature());
            order.setOrderStatus("SUCCESS");
        } else {
            order.setOrderStatus("FAILED");
        }
        orderRepository.save(order);
    }
}
```

---

### Step 7: Test Application

**Base URL:** `http://localhost:8081/api/orders`

#### 1) Create Order

Endpoint: `POST /create`

Request Body:
```json
{
  "userId": 1,
  "productId": 1,
  "amount": 180000
}
```

#### 2) Callback to Update Order

Endpoint: `POST /callback`

Request Body:
```json
{
  "razorpayOrderId": "order_PjcL2DFPm4AYDT",
  "razorpayPaymentId": "1234",
  "razorpaySignature": "Hs12312"
}
```

---

### Step 8: Frontend Integration

Include the Razorpay Checkout Script in your HTML file:

```html
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
```

---

### Future Works

1. Provide a user interface to create orders using frontend technologies such as React.js or Angular.
2. Use live mode credentials to process real-time payments.
3. Integrate a message broker (e.g., Apache Kafka, RabbitMQ) for high-throughput order management.

