import router from "@src/router";
import "@src/style.css";
import {createPinia} from "pinia";
import {createApp} from "vue";
import vClickOutside from "click-outside-vue3";
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import '@fortawesome/fontawesome-free/css/all.css';

import i18n from '@src/i18n';

import App from "@src/App.vue";

const pinia = createPinia();

const app = createApp(App);


app.use(Toast, {
    transition: "Vue-Toastification__bounce",
    maxToasts: 10,
    newestOnTop: true
})

app.use(i18n)
    .use(pinia)
    .use(router)
    .use(vClickOutside)
    .mount("#app");
