import GenericService from "@src/services/generic-service";
import UserProfileClient from "@src/clients/user-profile-client";
import {defaultSettings} from "@src/store/defaults";
import useStore from "@src/store/store";
import {IToken, IUser} from "@src/types";
import authenticationService from "@src/services/authentication-service";

class UserProfileService extends GenericService {
    private userProfileClient = new UserProfileClient(import.meta.env.VITE_API_SERVER_URL!);

    public async getUserProfile() {
        const tokens: IToken | null = await authenticationService.getOrRefreshTokens();

        if (tokens) {
            const response = await this.userProfileClient.getUserProfile(tokens.accessToken, this.getPreferredLanguage())

            if (response === null) {
                // todo handel error
                return false;
            }

            if ('errors' in response && response.errors && response.errors.length > 0) {
                // todo handel error
                console.debug(response?.errors);
                return response.errors[0].code;
            }

            if ('data' in response && response.data) {
                const user: IUser = response.data
                useStore().$patch({
                    status: "success",
                    user: user || undefined,
                    settings: user.settings || defaultSettings,
                    notifications: [],
                    archivedConversations: [],
                    delayLoading: false
                });
            }
        }
    }

    public async searchUserProfiles(keyword: string){
        const tokens: IToken | null = await authenticationService.getOrRefreshTokens();

        if (tokens) {
            const response = await this.userProfileClient.searchUserProfiles(keyword, tokens.accessToken, this.getPreferredLanguage());

            if (response === null) {
                // todo handel error
                return false;
            }

            if ('errors' in response && response.errors && response.errors.length > 0) {
                // todo handel error
                console.debug(response?.errors);
                return response.errors[0].code;
            }

            if ('data' in response && response.data) {
                useStore().user!!.contacts = response?.data!!
            }
        }
    }

    public async addConnection(connectionId: string){
        const tokens: IToken | null = await authenticationService.getOrRefreshTokens();

        if (tokens) {
            const response = await this.userProfileClient.addConnection(connectionId, tokens.accessToken, this.getPreferredLanguage());

            if (response?.errors) {
                // todo // handle error
            } else {
                return true
            }
        }
    }

    public async updateUserProfileSettings(){
        const tokens: IToken | null = await authenticationService.getOrRefreshTokens();

        if (tokens) {
            const response = await this.userProfileClient.updateUserProfileSettings(this.getSettings(), tokens.accessToken, this.getPreferredLanguage());

            if (response?.errors) {
                // todo // handle error
            } else {
                return true
            }
        }
    }
}

const userProfileService = new UserProfileService();
export default userProfileService;

