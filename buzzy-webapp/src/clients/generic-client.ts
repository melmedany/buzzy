import axios, {AxiosRequestConfig} from "axios";

class GenericClient {
    private readonly baseURL: string;

    constructor(baseURL: string) {
        this.baseURL = baseURL;
        axios.defaults.timeout = 5000;
    }

    protected async getData<T>(endpoint: string, headers: AxiosRequestConfig['headers'], params: AxiosRequestConfig['params']): Promise<T> {
        try {
            return await axios.get(this.getFullUrl(endpoint), {headers, params});
        } catch (error) {
            console.error('Error getting data:', error);
            throw error;
        }
    }

    protected async postData<T>(endpoint: string, headers: AxiosRequestConfig['headers'], params: AxiosRequestConfig['params'], data: any): Promise<T> {
        try {
            return await axios.post(this.getFullUrl(endpoint), data, {headers, params});
        } catch (error) {
            console.error('Error posting data:', error);
            throw error;
        }
    }


    protected async putData<T>(endpoint: string, headers: AxiosRequestConfig['headers'], params: AxiosRequestConfig['params'], data: any): Promise<T> {
        try {
            return await axios.put(this.getFullUrl(endpoint), data, {headers, params});
        } catch (error) {
            console.error('Error putting data:', error);
            throw error;
        }
    }

    private getFullUrl(endpoint: string) {
        return this.baseURL + endpoint;
    }
}

export default GenericClient;
