<script setup lang="ts">
import router from '../../router'
import { getToken } from '@/api/Login'
import { getQueryString } from '@/util/GlobalUtils'
import { createDiscreteApi } from 'naive-ui'
import { generateCodeVerifier, generateCodeChallenge } from '@/util/pkce'

const { message } = createDiscreteApi(['message'])

// 生成CodeVerifier
let codeVerifier: string = generateCodeVerifier()
// codeChallenge
let codeChallenge: string = generateCodeChallenge(codeVerifier)
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
        const code_verifier = localStorage.getItem('codeVerifier')
        getToken({
            grant_type: 'authorization_code',
            client_id: import.meta.env.VITE_OAUTH_CLIENT_ID,
            redirect_uri: import.meta.env.VITE_OAUTH_REDIRECT_URI,
            code,
            code_verifier,
            state
        })
        .then((res: any) => {
            localStorage.setItem('accessToken', JSON.stringify(res, null, 2))
            router.push({ path: '/' })
        })
        .catch((e) => {
            message.warning(`请求token失败：${e.data.error || e.message || e.statusText}`)
        })
    }
    
} else {
    // 缓存state
    localStorage.setItem('state', state)
    // 缓存codeVerifier
    localStorage.setItem('codeVerifier', codeVerifier)
    window.location.href = `${
        import.meta.env.VITE_OAUTH_ISSUER
    }/oauth2/authorize?response_type=code&client_id=${import.meta.env.VITE_OAUTH_CLIENT_ID}&redirect_uri=${
        encodeURIComponent(import.meta.env.VITE_OAUTH_REDIRECT_URI)
    }&scope=message.write%20message.read&code_challenge=${codeChallenge}&code_challenge_method=S256&state=${state}`
}
</script>

<template>
    加载中...
</template>