import axios, {AxiosRequestConfig} from "axios";

class APICalls {
    private readonly baseURL: string;
    private readonly defaultHeaders: any;


    constructor(baseURL: string) {
        this.baseURL = baseURL;
        this.defaultHeaders = {
            "Accept": "application/json",
            "Accept-Language": "en",
            "Content-Type": "application/json",
        };

        axios.defaults.headers.common = {
            ...axios.defaults.headers.common,
            ...this.defaultHeaders,
        };
    }

    private getFullUrl(endpoint: string) {
        return this.baseURL + endpoint;
    }

    async postData(endpoint: string, headers: AxiosRequestConfig['headers'], data: any) {
        try {
            const response = await axios.post(this.getFullUrl(endpoint), data, {headers});
            return response.data;
        } catch (error) {
            console.error('Error posting data:', error);
            throw error;
        }
    }
}

export default APICalls;
