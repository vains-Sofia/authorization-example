<template>
  <div class="welcome">
    <n-card class="navbar"><b>Spring Authorization Server 前后端分离示例项目</b></n-card>
    <n-space class="features">
      <n-card title="登录页面" @click="pathRoute('/login')" hoverable> /login </n-card>
      <n-card title="授权确认页面" @click="pathRoute('/consent')" hoverable> /consent</n-card>
      <n-card title="设备码验证页面" @click="pathRoute('/activate')" hoverable> /activate</n-card>
      <n-card title="验证成功页面" @click="pathRoute('/activated')" hoverable> /activated </n-card>
      <br />
      <n-card title="授权码模式" @click="todo" hoverable> 发起授权码模式的授权申请 </n-card>
      <n-card title="PKCE模式" @click="todo" hoverable> 发起PKCE模式的授权申请 </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import router from '../../router'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])
/**
 * 根据路径跳转路由
 * @param path 路由路径
 */
const pathRoute = (path: string) => {
  router.push({ path })
}

const todo = () => {
  message.info('待开发')
}
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
