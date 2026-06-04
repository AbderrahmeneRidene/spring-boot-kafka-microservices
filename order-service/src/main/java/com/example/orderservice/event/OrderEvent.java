package com.example.orderservice.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String orderId; // Sera généré par le backend

    @NotBlank(message = "L'ID du client ne peut pas être vide")
    private String customerId;

    @Min(value = 1, message = "Le montant de la commande doit être d'au moins 1")
    private double amount;

    private String status;
}
