import GenericClient from "@src/clients/generic-client";
import {APIResponse, LoginForm, RefreshTokenForm, SignupForm, TokensResponse} from "@src/types";
import {AxiosResponse} from "axios";

class AuthorizationClient extends GenericClient {

    async signup(signupForm: SignupForm, accessToken: string, acceptLanguage: string): Promise<APIResponse<void>> {
        try {
            const headers = {
                "Authorization": "Bearer " + accessToken,
                "Accept-Language": acceptLanguage,
            }

            const endpoint = import.meta.env.VITE_API_VERSION! + import.meta.env.VITE_SIGNUP_ENDPOINT!
            const response =  await this.postData<AxiosResponse>(endpoint, headers, {}, signupForm);
            return response.data;
        } catch (error: any) {
            if (error?.code === 'ERR_NETWORK') {
                console.warn('Network error!', error);
                return error;
            }

            console.error('Error posting data:', error);
            return error?.response.data
        }
    }

    async tokens(loginForm: LoginForm): Promise<APIResponse<void> | TokensResponse> {
        try {
            const headers = {
                "Authorization": this.getBasicAuthHeader(),
                "Content-Type": "application/x-www-form-urlencoded"
            }
            const body = {"username": loginForm.username, "password": loginForm.password, "grant_type": "grant_api"}
            const response = await this.postData<AxiosResponse<TokensResponse>>(import.meta.env.VITE_TOKENS_ENDPOINT!, headers, {}, body);
            return response.data;
        } catch (error: any) {
            if (error?.code === 'ERR_NETWORK') {
                console.warn('Network error!', error);
                return error;
            }

            console.error('Error posting data:', error);
            return error?.response.data
        }
    }

    async ccToken(): Promise<APIResponse<void> | TokensResponse> {
        try {
            const headers = {
                "Authorization": this.getBasicAuthHeader(),
                "Content-Type": "application/x-www-form-urlencoded"
            }
            const body = {"grant_type": "client_credentials"}
            const response = await this.postData<AxiosResponse<TokensResponse>>(import.meta.env.VITE_TOKENS_ENDPOINT!, headers, {}, body);
            return response.data;
        } catch (error: any) {
            if (error?.code === 'ERR_NETWORK') {
                console.warn('Network error!', error);
                return error;
            }

            console.error('Error posting data:', error);
            return error?.response.data
        }
    }

    async refreshTokens(refreshToken: string): Promise<any> {
        try {
            const headers = {
                "Authorization": this.getBasicAuthHeader(),
                "Content-Type": "application/x-www-form-urlencoded"
            }

            const refreshTokenForm: RefreshTokenForm = {
                grant_type: "refresh_token",
                refresh_token: refreshToken
            }
            return await this.postData(import.meta.env.VITE_TOKENS_ENDPOINT!, headers, {}, refreshTokenForm);
        } catch (error: any) {
            console.error('Error posting data:', error);
            return error?.response.data
        }
    }

    async logout(accessToken: string): Promise<any> {
        try {
            const headers = {
                "Authorization": this.getBasicAuthHeader(),
                "Content-Type": 'application/x-www-form-urlencoded'
            }
            await this.postData(import.meta.env.VITE_TOKENS_REVOKE_ENDPOINT!, headers, {}, {token: accessToken});
        } catch (error) {
            console.error('Error posting data:', error);
            return error
        }
    }

    private getBasicAuthHeader(): string {
        const credentials = `${import.meta.env.VITE_BUZZY_WEB_APP_CLIENT_ID!}:${import.meta.env.VITE_BUZZY_WEB_APP_CLIENT_SECRET!}`;
        return `Basic ${btoa(credentials)}`;
    }
}
export default AuthorizationClient;



