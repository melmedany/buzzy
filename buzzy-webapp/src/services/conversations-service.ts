import {IConversation, IMessage, IToken} from "@src/types";
import ConversationsClient from "@src/clients/conversations-client";
import GenericService from "@src/services/generic-service";
import useStore from "@src/store/store";
import authenticationService from "@src/services/authentication-service";

class ConversationsService extends GenericService {
    private conversationsClient = new ConversationsClient(import.meta.env.VITE_API_SERVER_URL!);

    public async getConversationsSummary() {
        const tokens: IToken | null = await authenticationService.getOrRefreshTokens();

        if (tokens) {
            const response = await this.conversationsClient.getConversationsSummary(tokens.accessToken, this.getPreferredLanguage());

            if (response?.errors) {
                // todo // handle error
                console.debug(response?.errors);
            } else {
                useStore().$patch({
                    conversations: response?.data || [],
                });
            }
        }
    }

    public async getConversation(conversationId: string): Promise<IConversation | null> {
        const tokens: IToken | null = await authenticationService.getOrRefreshTokens();

        if (tokens) {
            const response = await this.conversationsClient.getConversation(conversationId, tokens.accessToken, this.getPreferredLanguage());

            if (response?.errors) {
                // todo // handle error
                console.debug(response?.errors);
                return null
            } else {
                return response?.data
            }
        }
        return null;
    }

    public async getConversationMessage(messageId: string, conversationId: string): Promise<IMessage | null> {
        const tokens: IToken | null = await authenticationService.getOrRefreshTokens();

        if (tokens) {
            const response = await this.conversationsClient.getConversationMessage(messageId, conversationId, tokens.accessToken, this.getPreferredLanguage());

            if (response?.errors) {
                // todo // handle error
                console.debug(response?.errors);
                return null
            } else {
                return response?.data
            }
        }
        return null;
    }

    public async postMessage(messageData: any, conversationId: string) {
        const tokens: IToken | null = await authenticationService.getOrRefreshTokens();

        if (tokens) {
            const response = await this.conversationsClient.postMessage(messageData, conversationId, tokens.accessToken, this.getPreferredLanguage());

            if (response?.errors) {
                // todo // handle error
                console.debug(response?.errors);
                return null
            } else {
                return response?.data
            }
        }
    }
}

const conversationsService = new ConversationsService()
export default conversationsService;
