import {createI18n, LocaleMessages,} from 'vue-i18n';
import enMessages from '@src/locales/en.json';
import nlMessages from '@src/locales/nl.json';


const localeMessages: LocaleMessages<any> = {
    en: enMessages,
    nl: nlMessages
};

const i18n = createI18n({
    locale: "en",
    fallbackLocale: 'en',
    legacy: false,
    globalInjection: true,
    messages: localeMessages
});

export default i18n;


