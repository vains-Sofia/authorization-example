<script setup lang="ts">
defineOptions({
  name: "OAuth2Redirect"
});
import router from "../../router";
import { storageLocal } from "@pureadmin/utils";
import {
  setToken,
  type DataInfo,
  getQueryString,
  type OAuthClientInfo,
  buildAuthorizeUri,
  redirectToBeforeLoginUri
} from "@/utils/auth";
import { getAccessToken, getUserinfo } from "@/api/user";
import { message } from "@/utils/message";

const oAuthClinet: OAuthClientInfo = {
  grantType: "authorization_code",
  scopes: ["openid", "profile", "message.read", "message.write"],
  clientId: import.meta.env.VITE_OAUTH_CLIENT_ID,
  clientSecret: import.meta.env.VITE_OAUTH_CLIENT_SECRET,
  redirectUri: import.meta.env.VITE_OAUTH_REDIRECT_URI,
  authorizeUri: `${import.meta.env.VITE_OAUTH_BASE_URL}/oauth2/authorize`
};

// 获取地址栏授权码
const code = getQueryString("code");

if (code) {
  // 从缓存中获取 codeVerifier
  const state = storageLocal().getItem("state");
  // 校验state，防止cors
  const urlState = getQueryString("state");
  if (urlState !== state) {
    message("state校验失败.");
  } else {
    // 请求access_token
    getAccessToken(oAuthClinet, code, state)
      .then((res: any) => {
        let dataInfo: DataInfo<number> = {
          accessToken: res.access_token,
          expires: new Date().getTime() + res.expires_in * 1000,
          refreshToken: res.refresh_token,
          tokenType: res.token_type,
          idToken: res.id_token,
          scopes: res.scope.split(" ")
        };
        setToken(dataInfo);
        getUserinfo()
          .then((user: any) => {
            dataInfo.username = user.name;
            dataInfo.roles = user.authorities.flatMap(item =>
              Object.values(item)
            );
            setToken(dataInfo);
            redirectToBeforeLoginUri();
          })
          .catch(error => {
            console.log(error)
            message(
              `${error.response.data.error}: ${error.response.data.message}`,
              { type: "error" }
            )
          });
      })
      .catch(error =>
        error.response?.status === 400
          ? message(
              `${error.response.data.error}: 请检查authorization_code是否已被使用或已过期`,
              { type: "error" }
            )
          : message(`${error.code}: ${error.message}`, { type: "error" })
      );
  }
} else {
  window.location.href = buildAuthorizeUri(oAuthClinet);
}
</script>

<template>加载中...</template>
