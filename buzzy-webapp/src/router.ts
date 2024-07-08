import {createRouter, createWebHistory} from "vue-router";
import AccessView from "@src/components/views/AccessView/AccessView.vue";
import HomeView from "@src/components/views/HomeView/HomeView.vue";
import PasswordResetView from "@src/components/views/PasswordResetView/PasswordResetView.vue";
import Chat from "@src/components/views/HomeView/Chat/Chat.vue";
import useStore from "@src/store/store";
import {isBefore} from "date-fns";
import {IToken} from "@src/types";

const routes = [
  {
    path: "/chat/",
    name: "Home",
    alias: "/",
    component: HomeView,
    children: [
      {
        path: "/chat/",
        alias: "/",
        name: "No-Chat",
        component: Chat,
        meta: { requiresAuth: true }
      },
      {
        path: "/chat/:id/",
        name: "Chat",
        component: Chat,
        meta: { requiresAuth: true }
      },
    ],
    meta: { requiresAuth: true }
  },
  {
    path: "/access/:method/",
    name: "Access",
    component: AccessView,
  },
  {
    path: "/reset/",
    name: "Password Reset",
    component: PasswordResetView,
  },
];

// create the router
const router = createRouter({
  history: createWebHistory(),
  routes,
});

const isLoggedIn = (tokens?: IToken): boolean => {
  if (!tokens) {
    return false;
  } else {
    const accessTokenExists = !!tokens.accessToken.trim();
    const refreshTokenExists = !!tokens.refreshToken.trim();
    const tokenExpired = isBefore(tokens.expiresAt, new Date());
    return accessTokenExists && refreshTokenExists && !tokenExpired;
  }
};


// (router gaurd) when navigating in mobile screen from chat to chatlist,
// don't navigate to the previous chat navigate to the chatlist.
router.beforeEach((to, from, next) => {
  // console.log(window.innerWidth);

  const store = useStore();

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isLoggedIn(store.tokens)) {
      // Redirect to login page if token is not present
      next("/access/sign-in/");
    } else {
      next(); // Continue to the route
    }
  } else if (from.name === "Chat" && to.name === "Chat" && window.innerWidth <= 967) {
    next({ name: "No-Chat" });
  } else {
    next();
  }

});

export default router;
