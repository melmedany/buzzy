import useStore from "@src/store/store";

import webSocketClient from "@src/clients/web-socket-client";

export const connectToSocketSever = async () => {
    const store = useStore();

    if (store.tokens) {
        return webSocketClient.init('ws://ws.buzzy.io/ws', store.tokens.accessToken)
            .getInstance()
            .awaitConnect();
    }
    return false
};



