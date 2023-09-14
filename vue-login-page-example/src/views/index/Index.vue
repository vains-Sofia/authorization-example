<template>
  <div class="welcome">
    <n-card class="navbar"><b>Spring Authorization Server 前后端分离示例项目</b></n-card>
    <n-space class="features">
      <n-card title="登录页面" @click="pathRoute('/login')" hoverable> /login </n-card>
      <n-card title="授权确认页面" @click="pathRoute('/consent')" hoverable> /consent</n-card>
      <n-card title="设备码验证页面" @click="pathRoute('/activate')" hoverable> /activate</n-card>
      <n-card title="验证成功页面" @click="pathRoute('/activated')" hoverable> /activated </n-card>
      <br />
      <n-card title="授权码模式" @click="pathRoute('/OAuth2Redirect')" hoverable>
        发起授权码模式的授权申请
      </n-card>
      <n-card title="PKCE模式" @click="pathRoute('/PkceRedirect')" hoverable>
        发起PKCE模式的授权申请
      </n-card>
      <n-card title="Token展示" hoverable v-if="accessToken">
        <n-table :single-line="false">
          <thead>
            <tr>
              <th style="width: 105px">Key</th>
              <th>Value</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(v, k) in accessToken" :key="k">
              <td>{{ k }}</td>
              <td>{{ v }}</td>
            </tr>
          </tbody>
        </n-table>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import router from '../../router'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])
// 从缓存中获取token
const accessToken = JSON.parse(String(localStorage.getItem('accessToken')))

/**
 * 根据路径跳转路由
 * @param path 路由路径
 */
const pathRoute = (path: string) => {
  router.push({ path })
}

// const todo = () => {
//   message.info('待开发')
// }
</script>

<style scoped>
.welcome {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
}
.features {
  padding: 8px;
}
.features div {
  cursor: pointer;
}
</style>
