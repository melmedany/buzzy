import {APIResponse, ISettings, IUser} from "@src/types";
import GenericClient from "@src/clients/generic-client";

const api_version = "/v1";
const get_user_profile_endpoint = api_version + "/profile";
const search_user_profiles_endpoint = api_version + "/profile/search";
const put_connection_endpoint = api_version + "/profile/connection/{connectionId}";
const put_user_profile_settings_endpoint = api_version + "/profile/settings";

class UserProfileClient extends GenericClient {
    async getUserProfile(accessToken: string, acceptLanguage: string): Promise<IUser | null> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        };

        try {
            const response: APIResponse<IUser> = await this.getData<APIResponse<IUser>>(get_user_profile_endpoint, headers, {})
            return response.data
        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response.data
        }
    };

    async searchUserProfiles(keyword: string, accessToken: string, acceptLanguage: string): Promise<IUser[] | null> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        };

        try {
            const response: APIResponse<IUser[]> = await this.getData<APIResponse<IUser[]>>(search_user_profiles_endpoint, headers, {keyword: keyword})
            return response.data
        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response.data
        }
    };

    async addConnection(connectionId: string, accessToken: string, acceptLanguage: string): Promise<any | null> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        };

        try {
            await this.putData<ISettings>(put_connection_endpoint.replace("{connectionId}", connectionId), headers, {}, null);
        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response.data
        }
    };

    async updateUserProfileSettings(accessToken: string, acceptLanguage: string, settings: ISettings) {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            // "Content-Type": "application/json",
        };

        try {
            await this.putData<ISettings>(put_user_profile_settings_endpoint, headers, {}, settings);
        } catch (error) {
            console.error('Error updating settings:', error);
        }
    };
}

const userProfileClient = new UserProfileClient('http://api.buzzy.io');
export default userProfileClient;

