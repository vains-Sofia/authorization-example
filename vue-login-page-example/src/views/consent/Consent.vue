<script setup lang="ts">
import { type Ref, ref } from 'vue'
import axios from 'axios'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])

// 获取授权确认信息响应
const consentResult: Ref<any> = ref()
// 所有的scope
const scopes = ref()
// 已授权的scope
const approvedScopes = ref()

axios({
  method: 'GET',
  url: `http://192.168.1.102:8080/oauth2/consent/parameters${window.location.search}`
})
  .then((r) => {
    let result = r.data
    if (result.success) {
      consentResult.value = result.data
      scopes.value = [...result.data.previouslyApprovedScopes, ...result.data.scopes]
      approvedScopes.value = result.data.previouslyApprovedScopes.map((e: any) => e.scope)
    } else {
      message.warning(result.message)
    }
  })
  .catch((e) => message.error(e.message))

/**
 * 提交授权确认
 *
 * @param cancel true为取消
 */
const submitApprove = (cancel: boolean) => {
  const data = new FormData()
  if (!cancel) {
    // 如果不是取消添加scope
    if (
      approvedScopes.value !== null &&
      typeof approvedScopes.value !== 'undefined' &&
      approvedScopes.value.length > 0
    ) {
      approvedScopes.value.forEach((e: any) => data.append('scope', e))
    }
  }
  data.append('state', consentResult.value.state)
  data.append('client_id', consentResult.value.clientId)
  data.append('user_code', consentResult.value.userCode)
  axios({
    method: 'POST',
    // @ts-ignore
    data: new URLSearchParams(data),
    headers: {
      nonceId: getQueryString('nonceId'),
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    url: `http://192.168.1.102:8080${consentResult.value.requestURI}`
  })
    .then((r) => {
      let result = r.data
      if (result.success) {
        window.location.href = result.data
      } else {
        if (result.message && result.message.indexOf('access_denied') > -1) {
          // 可以跳转至一个单独的页面提醒.
          message.warning('您未选择scope或拒绝了本次授权申请.')
        } else {
          message.warning(result.message)
        }
      }
    })
    .catch((e) => message.error(e.message))
}

/**
 * 获取地址栏参数
 * @param name 地址栏参数的key
 */
function getQueryString(name: string) {
  var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')

  var r = window.location.search.substr(1).match(reg)

  if (r != null) {
    return unescape(r[2])
  }

  return null
}
</script>

<template>
  <header>
    <img alt="Vue logo" class="logo" src="../../assets/logo.svg" width="125" height="125" />

    <div class="wrapper">
      <HelloWorld msg="OAuth 授权请求" />
    </div>
  </header>

  <main>
    <n-card v-if="consentResult && consentResult.userCode">
      您已经提供了代码
      <b>{{ consentResult.userCode }}</b>
      ，请验证此代码是否与设备上显示的代码匹配。
    </n-card>
    <br />
    <n-card :title="`${consentResult.clientName} 客户端`" v-if="consentResult">
      <template #header-extra>
        账号：
        <b>{{ consentResult.principalName }}</b>
      </template>
      此第三方应用请求获得以下权限
    </n-card>
    <n-scrollbar style="max-height: 230px">
      <n-checkbox-group v-model:value="approvedScopes">
        <n-list>
          <n-list-item v-for="scope in scopes">
            <template #prefix>
              <n-checkbox :value="scope.scope"> </n-checkbox>
            </template>
            <n-thing :title="scope.scope" :description="scope.description" />
          </n-list-item>
        </n-list>
      </n-checkbox-group>
    </n-scrollbar>
    <br />
    <n-button type="info" @click="submitApprove(false)" strong>
      &nbsp;&nbsp;&nbsp;&nbsp;确&nbsp;&nbsp;&nbsp;&nbsp;定&nbsp;&nbsp;&nbsp;&nbsp;
    </n-button>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <n-button type="warning" @click="submitApprove(true)">
      &nbsp;&nbsp;&nbsp;&nbsp;拒&nbsp;&nbsp;&nbsp;&nbsp;绝&nbsp;&nbsp;&nbsp;&nbsp;
    </n-button>
  </main>
</template>

<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}

b,
h3,
::v-deep(.n-card-header__main) {
  font-weight: bold !important;
}
</style>
