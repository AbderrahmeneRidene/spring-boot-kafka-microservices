package com.example.notificationservice.consumer;

import com.example.notificationservice.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@Slf4j
public class OrderConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    public OrderConsumer(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // @RetryableTopic active automatiquement la mécanique de retries et envoie le message 
    // dans 'orders-dlt' s'il échoue 3 fois de suite (Poison Pill)
    @RetryableTopic(attempts = "3", dltTopicSuffix = "-dlt")
    @KafkaListener(topics = "orders", groupId = "notification-group")
    public void consumeOrder(OrderEvent event) {
        log.info("-----------------------------------------------------------------");
        log.info("Message reçu du topic 'orders' !");
        
        // Simuler une erreur (Poison Pill) si le montant est négatif
        if (event.getAmount() < 0) {
            throw new RuntimeException("Montant invalide, impossible de traiter FROM BACKEND !");
        }

        log.info("Détails de la commande : ID={}, Client={}, Montant={}€, Statut={}", 
                event.getOrderId(), event.getCustomerId(), event.getAmount(), event.getStatus());
        
        // Simulation d'un traitement d'envoi d'email
        sendSimulatedEmail(event);
        
        // Pousser l'événement traité vers le Frontend Angular via WebSocket
        messagingTemplate.convertAndSend("/topic/orders", event);
        log.info("🌐 Message envoyé au Frontend via WebSocket");
        
        log.info("-----------------------------------------------------------------");
    }

    private void sendSimulatedEmail(OrderEvent event) {
        log.info("📧 ENVOI D'EMAIL EN COURS à {}: \"Votre commande {} de {}€ a bien été enregistrée !\"", 
                event.getCustomerId() + "@example.com", event.getOrderId(), event.getAmount());
    }

    @DltHandler
    public void handleDlt(OrderEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("❌ Message envoyé à la Dead Letter Queue (DLQ) depuis le topic: {}", topic);
        event.setStatus("ÉCHEC (DLQ)");
        
        // Pousser la notification d'échec vers le Frontend Angular via WebSocket pour débloquer l'UI !
        messagingTemplate.convertAndSend("/topic/orders", event);
        log.info("🌐 Message d'échec envoyé au Frontend via WebSocket");
    }
}

