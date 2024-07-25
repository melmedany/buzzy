import GenericClient from "@src/clients/generic-client";
import {APIResponse, IConversation} from "@src/types";

class ConversationsClient extends GenericClient {

    public async getConversationsSummary(accessToken: string, acceptLanguage: string): Promise<any> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        }

        try {
            const endpoint = import.meta.env.VITE_API_VERSION! + import.meta.env.VITE_CONVERSATIONS_SUMMARY_ENDPOINT!
            const response: APIResponse<IConversation[]> = await this.getData(endpoint, headers, {});
            return response.data;

        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response.data
        }
    }

    public async getConversation(id: string, accessToken: string, acceptLanguage: string): Promise<any> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        }

        try {
            const endpoint = import.meta.env.VITE_API_VERSION! + import.meta.env.VITE_CONVERSATIONS_ENDPOINT!  + id
            const response: APIResponse<IConversation> = await this.getData(endpoint, headers, {});
            return response.data;

        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response.data
        }
    }

    public async getConversationMessage(messageId: string,conversationId: string, accessToken: string, acceptLanguage: string): Promise<any> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        }

        try {
            const endpoint = import.meta.env.VITE_API_VERSION! + import.meta.env.VITE_CONVERSATION_MESSAGE_ENDPOINT!.replace("{conversationId}", conversationId).replace("{messageId}", messageId)
            const response: APIResponse<IConversation> = await this.getData(endpoint, headers, {});
            return response.data;

        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response.data
        }
    }

    async postMessage(messageData: any, conversationId:string, accessToken: string, acceptLanguage: string) {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        }

        const endpoint = import.meta.env.VITE_API_VERSION! + import.meta.env.VITE_POST_MESSAGE_ENDPOINT!.replace("{conversationId}", conversationId)
        try {
            const response: APIResponse<IConversation> = await this.postData(endpoint, headers, {}, messageData);
            return response.data;
        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response.data
        }
    }
}

export default ConversationsClient;
