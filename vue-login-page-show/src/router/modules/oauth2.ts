import { $t } from "@/plugins/i18n";
const Layout = () => import("@/layout/index.vue");

export default [
  {
    path: "/OAuth2Redirect",
    name: "OAuth2Redirect",
    component: () => import("@/views/login/OAuthRedirect.vue"),
    meta: {
      title: $t("status.pureLoad"),
      showLink: false,
      rank: 103
    }
  },
  {
    path: "/consent",
    name: "consent",
    component: () => import("@/views/login/Consent.vue"),
    meta: {
      title: $t("menus.consent"),
      showLink: false,
      rank: 103
    }
  }
] satisfies Array<RouteConfigsTable>;
