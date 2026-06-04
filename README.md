# TP : Microservices Spring Boot & Apache Kafka

Ce projet contient deux microservices configurés pour s'échanger des messages en temps réel via Apache Kafka :
1. **`order-service`** : Expose une API REST sur le port `8081` et produit des messages (commandes créées) dans le topic Kafka `orders`.
2. **`notification-service`** : Écoute le topic `orders` sur le port `8082` et simule l'envoi d'e-mails pour chaque commande reçue.

---

## 🚀 Étape 1 : Démarrer Kafka en local

Assurez-vous que Docker Desktop est démarré. Dans votre terminal, placez-vous à la racine de ce dossier `spring-boot-kafka-microservices` et lancez :

```bash
docker-compose up -d
```

---

## 🛠️ Étape 2 : Lancer les Microservices

Vous devez lancer chaque service dans une fenêtre de terminal différente.

### Dans un terminal pour `order-service` :
```bash
cd order-service
mvn spring-boot:run
```

### Dans un autre terminal pour `notification-service` :
```bash
cd notification-service
mvn spring-boot:run
```

*(Note : Assurez-vous d'avoir installé Java 17+ et Maven sur votre machine).*

---

## 🎯 Étape 3 : Tester l'intégration (Passer une Commande)

Une fois que les deux services et Kafka tournent, vous pouvez simuler l'achat d'un client en envoyant une requête HTTP POST sur `order-service` (`http://localhost:8081/api/orders`).

### Avec PowerShell (Windows) :
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/orders" -Method Post -Body '{"customerId":"alex_martin","amount":189.99}' -ContentType "application/json"
```

### Avec cURL (Bash / Git Bash) :
```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"alex_martin", "amount":189.99}'
```

---

## 📝 Étape 4 : Vérifier les résultats

Dès que la commande est envoyée :
1. **`order-service`** affiche un log indiquant l'envoi de la commande dans Kafka.
2. **`notification-service`** réagit instantanément, intercepte l'événement JSON, le convertit en objet Java et affiche les logs suivants :

```text
Message reçu du topic 'orders' !
Détails de la commande : ID=cf48d5d4-47b2-4d2d-8692-a16df332468d, Client=alex_martin, Montant=189.99€, Statut=CRÉÉE
📧 ENVOI D'EMAIL EN COURS à alex_martin@example.com: "Votre commande cf48d5d4-47b2-4d2d-8692-a16df332468d de 189.99€ a bien été enregistrée !"
```

---

## 🧹 Nettoyer l'environnement

Pour arrêter l'instance locale de Kafka, fermez les terminaux des microservices et lancez :
```bash
docker-compose down
```
