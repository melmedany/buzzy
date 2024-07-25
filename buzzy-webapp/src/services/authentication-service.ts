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
        const response = await this.authServerClient.signup(signupForm, this.getPreferredLanguage());

        if (response.errors) {
            // todo handel error
            return false
        } else {
            return true
        }
    }

    public async login(loginForm: LoginForm): Promise<boolean | ApiErrorCode | undefined> {
        const response = await this.authServerClient.tokens(loginForm);

        if ('errors' in response && response.errors !== null) {
            // todo handel error
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
        const response = await this.authServerClient.logout(this.getTokens().accessToken);

        if (response && 'errors' in response && response.errors !== null) {
            // todo handel error
            return response.errors[0].code;
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

        const refreshTokenExists = !!tokens.refreshToken.trim();

        if (!refreshTokenExists) {
            throw new TokenExpiredError("Refresh token expired or invalid, user must be login again.");
        }

        const response = await this.authServerClient.refreshTokens(tokens.refreshToken);

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
                console.error('Error get or refresh tokens: ', error);
                return null;
            }
        }
    }
}

const authenticationService = new AuthenticationService();
export default authenticationService;



