import { RxStompConfig } from '@stomp/rx-stomp';

export const myRxStompConfig: RxStompConfig = {
  // L'URL du broker WebSocket configuré dans Spring Boot
  brokerURL: 'ws://localhost:8082/ws-notifications',

  // Paramètres de reconnexion automatique (très utile en cas de coupure !)
  heartbeatIncoming: 0,
  heartbeatOutgoing: 20000,
  reconnectDelay: 5000,

  // Débogage dans la console (à retirer en prod)
  debug: (msg: string): void => {
    console.log(new Date(), msg);
  },
};
