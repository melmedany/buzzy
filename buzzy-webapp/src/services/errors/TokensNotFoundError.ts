class TokensNotFoundError extends Error {
    constructor(message: string) {
        super(message);
        this.name = "TokensNotFoundError";
    }
}

export default TokensNotFoundError;