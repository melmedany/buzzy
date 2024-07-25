import {createRouter, createWebHistory} from "vue-router";
import AccessView from "@src/components/views/AccessView/AccessView.vue";
import HomeView from "@src/components/views/HomeView/HomeView.vue";
import PasswordResetView from "@src/components/views/PasswordResetView/PasswordResetView.vue";
import Chat from "@src/components/views/HomeView/Chat/Chat.vue";
import authenticationService from "@src/services/authentication-service";

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

const isLoggedIn = async (): Promise<boolean> => {
  const tokens = await authenticationService.getOrRefreshTokens();
  return tokens !== null
}


// (router gaurd) when navigating in mobile screen from chat to chatlist,
// don't navigate to the previous chat navigate to the chatlist.
router.beforeEach(async (to, from, next) => {
  // console.log(window.innerWidth);

  if (to.matched.some(record => record.meta.requiresAuth)) {
    const loggedIn = await isLoggedIn()

    if (!loggedIn) {
      console.debug("User is not logged in or must login again. Redirecting to login page")
      next("/access/sign-in/");
    } else {
      next(); // Continue to the route
    }
  } else if (from.name === "Chat" && to.name === "Chat" && window.innerWidth <= 967) {
    next({name: "No-Chat"});
  } else {
    next();
  }

});

export default router;
