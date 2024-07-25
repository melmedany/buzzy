import {APIResponse, ISettings, IUser} from "@src/types";
import GenericClient from "@src/clients/generic-client";

class UserProfileClient extends GenericClient {
    public async getUserProfile(accessToken: string, acceptLanguage: string): Promise<APIResponse<void> | IUser | null> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        }

        try {
            const endpoint = import.meta.env.VITE_API_VERSION! + import.meta.env.VITE_USER_PROFILE_ENDPOINT!
            return (await this.getData<APIResponse<IUser>>(endpoint, headers, {})).data
        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response
        }
    }

    public async searchUserProfiles(keyword: string, accessToken: string, acceptLanguage: string): Promise<APIResponse<void> | IUser[] | null> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        }

        try {
            const endpoint = import.meta.env.VITE_API_VERSION! + import.meta.env.VITE_SEARCH_USER_PROFILES_ENDPOINT!
            return (await this.getData<APIResponse<IUser[]>>(endpoint, headers, {keyword: keyword})).data
        } catch (error: any) {
            console.error('Error getting data:', error);
            return error?.response
        }
    }

    public async addConnection(connectionId: string, accessToken: string, acceptLanguage: string): Promise<void | APIResponse<any>> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            "Content-Type": "application/json",
        }

        const endpoint = import.meta.env.VITE_API_VERSION! + import.meta.env.VITE_ADD_CONNECTION_ENDPOINT!.replace("{connectionId}", connectionId)
        try {
            await this.putData<ISettings>(endpoint, headers, {}, null);
        } catch (error: any) {
            console.error('Error adding connection:', error);
            return error?.response
        }
    }

    public async updateUserProfileSettings(settings: ISettings, accessToken: string, acceptLanguage: string): Promise<void | APIResponse<any>> {
        const headers = {
            "Authorization": "Bearer " + accessToken,
            "Accept-Language": acceptLanguage,
            // "Content-Type": "application/json",
        }

        try {
            const endpoint = import.meta.env.VITE_API_VERSION! + import.meta.env.VITE_UPDATE_USER_PROFILE_SETTINGS_ENDPOINT!
            await this.putData<ISettings>(endpoint, headers, {}, settings);
        } catch (error: any) {
            console.error('Error updating settings:', error);
            return error?.response
        }
    }
}

export default UserProfileClient;

