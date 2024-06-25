import { $t } from "@/plugins/i18n";

const operates = [
  {
    title: $t("login.purePhoneLogin")
  },
  {
    title: $t("login.pureQRCodeLogin")
  },
  {
    title: $t("login.pureRegister")
  }
];

// const thirdParty = [
//   {
//     title: $t("login.pureWeChatLogin"),
//     icon: "wechat"
//   },
//   {
//     title: $t("login.pureAlipayLogin"),
//     icon: "alipay"
//   },
//   {
//     title: $t("login.pureQQLogin"),
//     icon: "qq"
//   },
//   {
//     title: $t("login.pureWeiBoLogin"),
//     icon: "weibo"
//   }
// ];

const thirdParty = [
  {
    title: $t("login.pureWeChatLogin"),
    icon: "wechat",
    color: "#1AAD19",
    type: 'wechat'
  },
  {
    title: $t("login.pureGithubLogin"),
    icon: "github",
    color: "#000000",
    type: 'github'
  },
  {
    title: $t("login.pureGiteeLogin"),
    icon: "gitee.svg",
    type: 'gitee'
  }
];

export { operates, thirdParty };
