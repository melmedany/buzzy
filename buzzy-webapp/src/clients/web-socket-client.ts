import {Client, IMessage} from "@stomp/stompjs";

interface AwaitConnectConfig {
    retries?: number;
    current?: number;
    timeInterval?: number;
}

class WebSocketClient {
    private static _instance: WebSocketClient | null = null;
    private _client: Client;

    private constructor(baseURL: string, accessToken: string) {
        this._client = new Client({
            brokerURL: baseURL,
            connectHeaders: {
                Authorization: `Bearer ${accessToken}`,
            },
            reconnectDelay: 5000 * 60 * 1000,
            heartbeatIncoming: 60 * 1000,
            heartbeatOutgoing: 60 * 1000,
            // debug: function (str) {
            //     console.log(str);
            // },
            onConnect: (frame) => {
                console.debug("connected! ", frame);
            },
            onDisconnect(frame) {
                console.debug("disconnected! ", frame);
            },
            onStompError: function (frame) {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            }
        });

        this._client.activate();
    }

    public static init(baseURL: string, accessToken: string): typeof WebSocketClient {
        if (!this._instance) {
            this._instance = new WebSocketClient(baseURL, accessToken);
        }
        return this;
    }

    public static getInstance(): WebSocketClient {
        if (!WebSocketClient._instance) {
            throw new Error("SocketClient is not initialized. Call init() first.");
        }
        return WebSocketClient._instance;
    }

    public subscribe(destination: string, callback: (message: IMessage) => void, headers?:  any) {
        return this._client.subscribe(destination, (message: IMessage) => {
            callback(message);
        }, headers);
    }

    public unsubscribe(destination: string) {
        return this._client.unsubscribe(destination);
    }

    public publish(destination: string, body: string) {
        return this._client.publish({destination: destination, body: body});
    }


    public async awaitConnect(awaitConnectConfig?: AwaitConnectConfig): Promise<void> {
        const {retries = 3, current = 0, timeInterval = 3000} = awaitConnectConfig || {};

        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (this.connected) {
                    resolve();
                } else {
                    console.debug("failed to connect! retrying");
                    if (current >= retries) {
                        console.debug("failed to connect within the specified time interval");
                        reject(new Error("Failed to connect within the specified time interval"));
                    } else {
                        this.awaitConnect({ ...awaitConnectConfig, current: current + 1 }).then(resolve).catch(reject);
                    }
                }
            }, timeInterval);
        });
    }

    private get connected(): boolean {
        return this._client.connected;
    }
}

export default WebSocketClient;
