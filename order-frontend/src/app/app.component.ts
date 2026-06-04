import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { RxStomp } from '@stomp/rx-stomp';
import { myRxStompConfig } from './rx-stomp.config';
import { Subscription } from 'rxjs';

// Contrat de données strict (TypeScript Interface) correspondant au Backend
export interface OrderEvent {
  orderId?: string;
  customerId: string;
  amount: number;
  status?: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'Order Dashboard - Async UI';
  
  // State variables for UI
  customerId: string = '';
  amount: number = 0;
  isLoading: boolean = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  recentOrders: OrderEvent[] = [];

  private rxStomp: RxStomp;
  private topicSubscription?: Subscription;

  constructor(private http: HttpClient) {
    this.rxStomp = new RxStomp();
    this.rxStomp.configure(myRxStompConfig);
  }

  ngOnInit() {
    this.rxStomp.activate();
    
    // Abonnement au topic WebSocket pour recevoir les notifications Kafka
    this.topicSubscription = this.rxStomp
      .watch('/topic/orders')
      .subscribe((message) => {
        const order: OrderEvent = JSON.parse(message.body);
        
        // Mettre à jour l'UI avec l'événement temps réel
        this.recentOrders.unshift(order); // Ajouter au début de la liste
        
        // Si c'est la commande qu'on vient juste d'envoyer, on enlève le spinner !
        this.isLoading = false;
        this.successMessage = `🎉 Succès ! Commande ${order.orderId} confirmée via Kafka !`;
        
        // Masquer le message après 5 secondes
        setTimeout(() => this.successMessage = null, 5000);
      });
  }

  ngOnDestroy() {
    this.topicSubscription?.unsubscribe();
    this.rxStomp.deactivate();
  }

  // Méthode asynchrone (retour 202 Accepted)
  submitOrder() {
    if (!this.customerId || this.amount <= 0) {
      this.errorMessage = "Données invalides. Vérifiez le contrat de schéma.";
      return;
    }

    this.errorMessage = null;
    this.isLoading = true; // Activer le spinner ! Le traitement asynchrone commence.

    const requestPayload: OrderEvent = {
      customerId: this.customerId,
      amount: this.amount
    };

    // On envoie la requête HTTP au Order Service
    this.http.post('http://localhost:8081/api/orders', requestPayload).subscribe({
      next: (response) => {
        // Le serveur répond 202 Accepted. Mais on garde le spinner actif !
        // Car on attend que Kafka traite le message et nous le dise via WebSocket.
        console.log('HTTP 202 Accepted : ', response);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = "Erreur HTTP : " + (err.error?.message || err.message);
      }
    });
  }
}
