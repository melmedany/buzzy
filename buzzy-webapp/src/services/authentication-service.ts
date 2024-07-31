import {ApiErrorCode, IToken, LoginForm, SignupForm, TokensResponse} from "@src/types";
import AuthorizationClient from "@src/clients/authorization-client";
import GenericService from "@src/services/generic-service";
import {addSeconds, isBefore} from "date-fns";
import useStore from "@src/store/store";
import TokenExpiredError from "@src/services/errors/TokenExpiredError";
import TokensNotFoundError from "@src/services/errors/TokensNotFoundError";
import {AxiosError} from "axios";

class AuthenticationService extends GenericService {
    private authServerClient = new AuthorizationClient(import.meta.env.VITE_SSO_SERVER_URL!);

    public async signup(signupForm: SignupForm) {

        const ccToken: IToken | null = await this.getCCToken();

        if (ccToken) {
            const response = await this.authServerClient.signup(signupForm, ccToken.accessToken, this.getPreferredLanguage());

            if ('errors' in response && response.errors !== null) {
                return response.errors[0].code;
            }

            if (response instanceof AxiosError) {
                // todo handel error
                return false
            }

            return true
        }
        // todo handel error
        return false

    }

    public async login(loginForm: LoginForm): Promise<boolean | ApiErrorCode | undefined> {
        const response = await this.authServerClient.tokens(loginForm);

        if ('errors' in response && response.errors !== null) {
            return response.errors[0].code;
        }

        if ('access_token' in response) {
            this.updateTokens(response)
            return true
        }

        if (response instanceof AxiosError) {
            return false
        }
    }

    public async logout(): Promise<boolean | ApiErrorCode | undefined> {
        const tokens = this.getTokens()
        let accessTokenResponse,  refreshTokenResponse

        if (tokens.accessToken) {
            accessTokenResponse = await this.authServerClient.logout(this.getTokens().accessToken);
        }

        if (tokens.refreshToken) {
            refreshTokenResponse = await this.authServerClient.logout(this.getTokens().refreshToken!!);
        }

        if (accessTokenResponse && 'errors' in accessTokenResponse && accessTokenResponse.errors !== null) {
            // todo handel error
            return accessTokenResponse.errors[0].code;
        } else if (refreshTokenResponse && 'errors' in refreshTokenResponse && refreshTokenResponse.errors !== null) {
            // todo handel error
            return refreshTokenResponse.errors[0].code;
        }

        return true;
    }

    public async getOrRefreshTokens(): Promise<IToken | null> {
        try {
            return this.getTokens();
        } catch (error) {
            if (error instanceof TokensNotFoundError) {
                return this.handleTokensNotFoundError(error);
            } else if (error instanceof TokenExpiredError) {
                return this.handleTokenExpiredError(error);
            } else {
                console.error('Error get or refresh tokens: ', error);
                return null
            }
        }
    }

    private async getCCToken(): Promise<IToken> {
        const ccToken = useStore().ccToken;

        if (!ccToken) {
            return this.handleNoCCToken()
        }

        const accessTokenExists = !!ccToken.accessToken.trim();
        const tokenExpired = isBefore(ccToken.expiresAt, new Date());

        if (!accessTokenExists || tokenExpired) {
            return this.handleNoCCToken()
        }

        return ccToken as IToken;
    }

    private getTokens(): IToken {
        const tokens = useStore().tokens;

        if (!tokens) {
            throw new TokensNotFoundError("Tokens not found. User must login first.");
        }

        const accessTokenExists = !!tokens.accessToken.trim();
        const tokenExpired = isBefore(tokens.expiresAt, new Date());

        if (!accessTokenExists || tokenExpired) {
            throw new TokenExpiredError("Access token expired or invalid");
        }

        return tokens as IToken;
    }

    private async refreshTokens() {
        const tokens = useStore().tokens;

        if (!tokens) {
            throw new TokensNotFoundError("Tokens not found.");
        }

        const refreshTokenExists = !!tokens.refreshToken!!.trim();

        if (!refreshTokenExists) {
            throw new TokenExpiredError("Refresh token expired or invalid, user must be login again.");
        }

        const response = await this.authServerClient.refreshTokens(tokens.refreshToken!!);

        if (response?.error) {
            console.debug(`error refresh token: ${response.error}`);
            return false
        } else {
            this.updateTokens(response?.data!!)
            return true
        }
    }

    private updateTokens(tokens: TokensResponse) {
        useStore().tokens = {
            accessToken: tokens.access_token,
            refreshToken: tokens.refresh_token,
            expiresAt: addSeconds(new Date(), tokens.expires_in)
        }
    }

    private handleTokensNotFoundError(error: TokensNotFoundError) {
        console.debug(error.message);
        return null
    }

    private async handleTokenExpiredError(error: TokenExpiredError) {
        console.debug(error.message);

        try {
            const refreshed = await this.refreshTokens();
            if (refreshed) {
                return this.getTokens();
            }
            return null;
        } catch (error) {
            if (error instanceof TokensNotFoundError) {
                return this.handleTokensNotFoundError(error);
            } else {
                console.error('Error refreshing refresh tokens: ', error);
                return null;
            }
        }
    }

    private async handleNoCCToken(): Promise<IToken> {
        const response = await this.authServerClient.ccToken();

        if ('errors' in response && response.errors !== null) {
            console.debug(`error get cc token: ${response.errors}`);
        } else if ('access_token' in response) {
            useStore().ccToken = {
                accessToken: response?.access_token,
                expiresAt: addSeconds(new Date(), response.expires_in)
            }
        }

        return useStore().ccToken!!
    }
}

const authenticationService = new AuthenticationService();
export default authenticationService;



