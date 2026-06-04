package com.example.orderservice.producer;

import com.example.orderservice.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderEvent event) {
        log.info("Envoi de la commande dans Kafka: {}", event);
        
        // On envoie le message. La clé est l'ID de commande, pour garantir l'ordre de traitement
        kafkaTemplate.send("orders", event.getOrderId(), event);
    }
}
