<script setup lang="ts">
import router from '../../router'
import { getToken } from '@/api/Login'
import { createDiscreteApi } from 'naive-ui'
import { generateCodeVerifier } from '@/util/pkce'
import { getQueryString } from '@/util/GlobalUtils'

const { message } = createDiscreteApi(['message'])

// 生成state
let state: string = generateCodeVerifier()

// 获取地址栏授权码
const code = getQueryString('code')

if (code) {
  // 从缓存中获取 codeVerifier
  const state = localStorage.getItem('state')
  // 校验state，防止cors
  const urlState = getQueryString('state')
  if (urlState !== state) {
    message.warning('state校验失败.')
  } else {
    // 从缓存中获取 codeVerifier
    getToken({
      grant_type: 'authorization_code',
      client_id: import.meta.env.VITE_OAUTH_CLIENT_ID,
      client_secret: import.meta.env.VITE_OAUTH_CLIENT_SECRET,
      redirect_uri: import.meta.env.VITE_OAUTH_REDIRECT_URI,
      code,
      state
    })
      .then((res: any) => {
        localStorage.setItem('accessToken', JSON.stringify(res))
        router.push({ path: '/' })
      })
      .catch((e) => {
        message.warning(`请求token失败：${e.data.error || e.message || e.statusText}`)
      })
  }
} else {
  // 缓存state
  localStorage.setItem('state', state)
  window.location.href = `${import.meta.env.VITE_OAUTH_ISSUER}/oauth2/authorize?client_id=${
    import.meta.env.VITE_OAUTH_CLIENT_ID
  }&response_type=code&scope=openid%20profile%20message.read%20message.write&redirect_uri=${
    import.meta.env.VITE_OAUTH_REDIRECT_URI
  }&state=${state}`
}
</script>

<template>加载中...</template>
