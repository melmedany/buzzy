import GenericClient from "@src/clients/generic-client";
import {APIResponse, IConversation} from "@src/types";

const api_version = "/v1";
const get_conversations_summary_endpoint = api_version + "/conversations/summary";
const get_conversation_endpoint = api_version + "/conversations/";
const post_message_endpoint = api_version + "/conversations/{conversationId}/messages";

class ConversationsClient extends GenericClient {

    async getConversationsSummary(accessToken: string, acceptLanguage: string): Promise<IConversation[] | null> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        };

        try {
            const response: APIResponse<IConversation[]> = await this.getData(get_conversations_summary_endpoint, headers, {});
            return response.data;

        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response.data
        }
    };

    async getConversation(id: string, accessToken: string, acceptLanguage: string): Promise<IConversation | null> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        };

        try {
            const response: APIResponse<IConversation> = await this.getData(get_conversation_endpoint + id, headers, {});
            return response.data;

        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response.data
        }
    };

    async postMessage(accessToken: string, acceptLanguage: string, conversationId:string, messageData: any) {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        };

        try {
            const response: APIResponse<IConversation> = await this.postData(post_message_endpoint.replace("{conversationId}", conversationId), headers, {}, messageData);
            return response.data;
        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response.data
        }
    };
}

const conversationsClient = new ConversationsClient('http://api.buzzy.io');
export default conversationsClient;
