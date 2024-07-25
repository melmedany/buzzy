import useStore from "@src/store/store";
import {ISettings} from "@src/types";
import {defaultSettings} from "@src/store/defaults";

class GenericService {

    protected getPreferredLanguage(): string {
        return useStore().settings.preferredLanguage || defaultSettings.preferredLanguage
    }

    protected getUsername(): string {
        return useStore().user!!.username
    }

    protected getSettings(): ISettings {
        return useStore().settings
    }
}

export default GenericService;
