### HOW TO USE

Step 1:  Setup Razorpay Account
Step 2:  Generate a new API key and save the Key ID and Key Secret securely for Test/Live Mode.
Step 3:  Create Springboot Project
        a) add necessary dependencies: 
         i) Springboot starter web
        ii)	Springboot data jpa
       iii) mysql drvier		
	      iv) Lombok
		     v) Thymeleaf
		    vi) Razorpay Java Dependency
      
       <dependency>
        <groupId>com.razorpay</groupId>
        <artifactId>razorpay-java</artifactId>
        <version>1.3.0</version>
       </dependency>

       ###Configurations
        spring.application.name=RazorPayIntegeration
        server.port = 8081
        #DATABASE CONFIGURATIONS
        spring.datasource.url= jdbc:mysql://localhost:3306/cosmostaker_orders
        spring.datasource.username = root
        spring.datasource.password= root
        spring.datasource.driver-class = com.cj.jdbc.Driver
        #JPA CONFIGURATION
        spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL8Dialect
        spring.jpa.hibernate.ddl-auto= update
        spring.jpa.show-sql = true
        spring.jpa.properties.hibernate.format_sql= true
        spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
        # Razorpay configurations
        razorpay.key =rzp_test_abczUJfEMHgyOt
        razorpay.secret =klvUKclzmiCp5KAPirBgeI6r

Step 4:  Create Order
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
Step 7: Test Application 
    BASE_URL = " http://localhost:8081/api/orders"
   1) Create Order
   post end points: /create-order 
   Payload:    {
                "userId": 1,
                "productId": 1,
                "amount": 180000
              }
      
  3) Callback to update Order
     1) Upate Order
   post end points: /callback 
   Payload:    {
               "razorpayOrderId": "order_PjcL2DFPm4AYDT",
               "razorpayPaymentId": "1234",
               "razorpaySignature": "Hs12312"
             }
   
Step 8: Frontend Integration
   Include Razorpay Checkout Script
   <script src="https://checkout.razorpay.com/v1/checkout.js"></script>


###Future Works :
1) We can Provide user Interface to create Order using frontend technologies such as Reactjs/Angular.
2) Provide Live mode credentials to get payments in real time 
3) Orders can be maintained using message broker(Apache kafka, RabbitMQ) for high throughput. 
   


