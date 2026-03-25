import { Client, IMessage } from '@stomp/stompjs';

interface WebSocketMessage {
  type?: string;
  orderId?: string;
  orderNumber?: string;
  status?: string;
  [key: string]: any;
}

type MessageHandler = (message: WebSocketMessage) => void;

class WebSocketService {
  private client: Client | null = null;
  private subscriptions: Map<string, Set<MessageHandler>> = new Map();

  connect() {
    if (this.client?.active) {
      console.log('WebSocket already connected');
      return;
    }

    this.client = new Client({
      brokerURL: 'ws://localhost:8080/api/ws-grocky',
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
      },
      debug: (str) => console.log('STOMP:', str),
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('WebSocket connected');
        // Resubscribe to all channels
        this.subscriptions.forEach((_, channel) => this.subscribe(channel));
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
      },
    });

    this.client.activate();
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
      this.subscriptions.clear();
    }
  }

  subscribe(channel: string, handler?: MessageHandler) {
    if (!this.client?.active) {
      console.warn('WebSocket not connected, connecting...');
      this.connect();
    }

    if (handler) {
      if (!this.subscriptions.has(channel)) {
        this.subscriptions.set(channel, new Set());
      }
      this.subscriptions.get(channel)!.add(handler);
    }

    const subscription = this.client?.subscribe(channel, (message: IMessage) => {
      const data: WebSocketMessage = JSON.parse(message.body);
      console.log(`Message received on ${channel}:`, data);

      this.subscriptions.get(channel)?.forEach((h) => h(data));
    });

    return () => subscription?.unsubscribe();
  }

  unsubscribe(channel: string, handler?: MessageHandler) {
    if (handler && this.subscriptions.has(channel)) {
      this.subscriptions.get(channel)!.delete(handler);
      if (this.subscriptions.get(channel)?.size === 0) {
        this.subscriptions.delete(channel);
      }
    }
  }

  // Order-specific methods
  subscribeToOrder(orderId: string, handler: MessageHandler) {
    return this.subscribe(`/topic/order/${orderId}`, handler);
  }

  subscribeToCustomer(customerId: string, handler: MessageHandler) {
    return this.subscribe(`/topic/customer/${customerId}`, handler);
  }

  subscribeToAdminOrders(handler: MessageHandler) {
    return this.subscribe(`/topic/admin/orders`, handler);
  }

  subscribeToNewOrders(handler: MessageHandler) {
    return this.subscribe(`/topic/admin/new-orders`, handler);
  }

  subscribeToInventory(handler: MessageHandler) {
    return this.subscribe(`/topic/admin/inventory`, handler);
  }

  subscribeToAnalytics(handler: MessageHandler) {
    return this.subscribe(`/topic/admin/analytics`, handler);
  }

  // Send message
  send(destination: string, message: any) {
    if (this.client?.active) {
      this.client.publish({
        destination,
        body: JSON.stringify(message),
      });
    } else {
      console.error('WebSocket not connected');
    }
  }

  isConnected() {
    return this.client?.active ?? false;
  }
}

export const webSocketService = new WebSocketService();
