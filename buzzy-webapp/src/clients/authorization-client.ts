import GenericClient from "@src/clients/generic-client";
import {APIResponse, LoginForm, RefreshTokenForm, SignupForm, TokensResponse} from "@src/types";
import {AxiosResponse} from "axios";

class AuthorizationClient extends GenericClient {

    public async signup(signupForm: SignupForm, acceptLanguage: string): Promise<any> {
        try {
            const headers = {
                "Authorization": this.getBasicAuthHeader(), //"Basic YnV6enktd2ViYXBwOmJ1enp5LXdlYmFwcC1zZWNyZXQ=",
                "Accept-Language": acceptLanguage,
            }

            const endpoint = import.meta.env.VITE_API_VERSION! + import.meta.env.VITE_SIGNUP_ENDPOINT!
            return await this.postData(endpoint, headers, {}, signupForm);
        } catch (error) {
            console.error('Error posting data:', error);
            return error
        }
    }

    async tokens(loginForm: LoginForm): Promise<APIResponse<void> | TokensResponse> {
        try {
            const headers = {
                "Authorization": this.getBasicAuthHeader(), //"Basic YnV6enktd2ViYXBwOmJ1enp5LXdlYmFwcC1zZWNyZXQ=",
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

    public async refreshTokens(refreshToken: string): Promise<any> {
        try {
            const headers = {
                "Authorization": this.getBasicAuthHeader(), //"Basic YnV6enktd2ViYXBwOmJ1enp5LXdlYmFwcC1zZWNyZXQ=",
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

    public async logout(accessToken: string): Promise<any> {
        try {
            const headers = {
                "Authorization": this.getBasicAuthHeader(), //"Basic YnV6enktd2ViYXBwOmJ1enp5LXdlYmFwcC1zZWNyZXQ=",
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



