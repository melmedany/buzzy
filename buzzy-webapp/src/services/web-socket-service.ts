import GenericService from "@src/services/generic-service";
import WebSocketClient from "@src/clients/web-socket-client";
import {IMessage} from "@stomp/stompjs";
import {ISession, IToken} from "@src/types";
import authenticationService from "@src/services/authentication-service";
import {StompSubscription} from "@stomp/stompjs/src/stomp-subscription";
import useStore from "@src/store/store";

class WebSocketService extends GenericService {

    private client!: WebSocketClient;

    public async subscribeToUserUpdates(callback: (message: IMessage) => void) {
        const destination = import.meta.env.VITE_USER_DESTINATION!.replace("{username}", this.getUsername())

        await this.lazyInit().then(() => {
            this.client.subscribe(destination, (message: IMessage) => {
                callback(message)
            });
        });
    }

    public async subscribeToConversation(conversationId: string, callback: (message: IMessage) => void) {
        const destination = import.meta.env.VITE_CONVERSATION_DESTINATION!.replace("{conversationId}", conversationId)
            .replace("{username}", this.getUsername());

        await this.lazyInit().then(() => {
            const subscription: StompSubscription = this.client.subscribe(destination, (message: IMessage) => {
                callback(message)
            });

            if (subscription) {
                const session: ISession = {id: subscription.id, destination: destination};
                useStore().sessions.push(session);
            }
        });
    }

    public async unSubscribeFromConversation(conversationId: string) {
        const destination = import.meta.env.VITE_CONVERSATION_DESTINATION!.replace("{conversationId}", conversationId)
            .replace("{username}", this.getUsername());

        const session: ISession | undefined = useStore().sessions.find(session => session.destination === destination);

        if (session) {
            console.log("UnSubscribeFromConversation", conversationId);
            await this.lazyInit().then(() => {
                this.client.unsubscribe(session.id);
            });
        }
    }

    public async messageStateUpdate(messageId: string, conversationId: string) {
        const body = {messageId: messageId, conversationId: conversationId};
        const destination = import.meta.env.VITE_MESSAGE_STATE_READ_DESTINATION!.replace("{conversationId}", conversationId)
            .replace("{messageId}", messageId);

        return this.client.publish(destination, JSON.stringify(body));
    }

    public async bulkMessageStateUpdate(messageId: string, conversationId: string) {
        const body = {messageId: messageId, conversationId: conversationId};
        const destination = import.meta.env.VITE_MESSAGE_STATE_BULK_READ_DESTINATION!.replace("{conversationId}", conversationId)
            .replace("{messageId}", messageId);

        return this.client.publish(destination, JSON.stringify(body));
    }

    private async lazyInit() {
        if (!this.client) {
            const tokens: IToken | null = await authenticationService.getOrRefreshTokens();

            if (tokens) {
                this.client = WebSocketClient.init(import.meta.env.VITE_WS_SERVER_URL!, tokens.accessToken).getInstance();
                await this.client.awaitConnect();
            }

        }
    }
}

const webSocketService = new WebSocketService();
export default webSocketService;
