package com.example.orderservice.controller;

import com.example.orderservice.event.OrderEvent;
import com.example.orderservice.producer.OrderProducer;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
// curl -X POST http://localhost:8081/api/orders -H "Content-Type: application/json" -d '{"customerId":"alex_martin", "amount":189.99}'
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200") // Autoriser Angular
public class OrderController {

    private final OrderProducer orderProducer;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }

    @PostMapping
    public ResponseEntity<OrderEvent> createOrder(@Valid @RequestBody OrderRequest request) {
        // Simulation de la création de commande
        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                request.getCustomerId(),
                request.getAmount(),
                "CREATED"
        );

        // Publication de l'événement Kafka
        orderProducer.sendOrderEvent(event);

        // 202 Accepted : "J'ai reçu la demande, je la traite de manière asynchrone"
        return ResponseEntity.accepted().body(event);
    }

    // DTO d'entrée simple
    public static class OrderRequest {
        @NotBlank(message = "L'ID du client est requis")
        private String customerId;

        private double amount;

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }
}
