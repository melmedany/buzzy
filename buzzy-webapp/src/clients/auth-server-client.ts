import GenericClient from "@src/clients/generic-client";
import {SignupForm, TokensResponse} from "@src/types";

const signup_endpoint = "/v1/signup";
const tokens_endpoint = "/oauth2/token";
const tokens_revoke_endpoint = "/oauth2/revoke";

class AuthServerClient extends GenericClient {
    public async signup(signupForm: SignupForm, acceptLanguage: string): Promise<any | null> {
        try {
            const headers = {
                "Authorization": "Basic YnV6enktd2ViYXBwOmJ1enp5LXdlYmFwcC1zZWNyZXQ=",
                "Accept-Language": acceptLanguage,
            };

            return await this.postData(signup_endpoint, headers, {}, signupForm);
        } catch (error) {
            console.error('Error posting data:', error);
            return error
        }
    };

    public async tokens(signinForm: any): Promise<TokensResponse | null> {
        try {
            const headers = {
                "Authorization": "Basic YnV6enktd2ViYXBwOmJ1enp5LXdlYmFwcC1zZWNyZXQ=",
                "Content-Type": "application/x-www-form-urlencoded"
            };
            return  await this.postData(tokens_endpoint, headers, {}, signinForm);
        } catch (error: any) {
            console.error('Error posting data:', error);
            return error?.response.data
        }
    };

    // todo add refresh token

    public async logout(accessToken: string) {
        try {
            const headers = {
                Authorization: "Basic YnV6enktd2ViYXBwOmJ1enp5LXdlYmFwcC1zZWNyZXQ=",
                "Content-Type": 'application/x-www-form-urlencoded'
            };
            await this.postData(tokens_revoke_endpoint, headers, {}, {token: accessToken});
        } catch (error) {
            console.error('Error posting data:', error);
            return error
        }
    };
}

const authServerClient = new AuthServerClient( 'http://login.buzzy.io');
export default authServerClient;



