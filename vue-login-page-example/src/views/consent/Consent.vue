<script setup lang="ts">
import { type Ref, ref } from 'vue'
import { createDiscreteApi } from 'naive-ui'
import { getQueryString } from '@/util/GlobalUtils'
import { getConsentParameters, submitApproveScope } from '@/api/Login'

const { message } = createDiscreteApi(['message'])

// 获取授权确认信息响应
const consentResult: Ref<any> = ref()
// 所有的scope
const scopes = ref()
// 已授权的scope
const approvedScopes = ref()
// 提交/拒绝按钮加载状态
const loading = ref(false)

/**
 * 初始化需要授权确认的客户端与scope
 */
getConsentParameters(window.location.search)
  .then((result: any) => {
    if (result.success) {
      consentResult.value = result.data
      scopes.value = [...result.data.previouslyApprovedScopes, ...result.data.scopes]
      approvedScopes.value = result.data.previouslyApprovedScopes.map((e: any) => e.scope)
    } else {
      message.warning(result.message)
    }
  })
  .catch((e: any) => {
    message.warning(`获取客户端与scope信息失败：${e.message || e.statusText}`)
  })

/**
 * 提交授权确认
 *
 * @param cancel true为取消
 */
const submitApprove = (cancel: boolean) => {
  if (!consentResult.value) {
    message.warning(`初始化未完成，无法提交`)
    return
  }
  loading.value = true
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

  submitApproveScope(
    // @ts-ignore
    new URLSearchParams(data),
    consentResult.value.requestURI
  )
    .then((result: any) => {
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
    .catch((e: any) => {
      message.warning(`提交授权确认失败：${e.message || e.statusText}`)
    })
    .finally(() => (loading.value = false))
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
          <n-list-item v-for="scope in scopes" :key="scope">
            <template #prefix>
              <n-checkbox :value="scope.scope"> </n-checkbox>
            </template>
            <n-thing :title="scope.scope" :description="scope.description" />
          </n-list-item>
        </n-list>
      </n-checkbox-group>
    </n-scrollbar>
    <br />
    <n-button type="info" :loading="loading" @click="submitApprove(false)" strong>
      &nbsp;&nbsp;&nbsp;&nbsp;确&nbsp;&nbsp;&nbsp;&nbsp;定&nbsp;&nbsp;&nbsp;&nbsp;
    </n-button>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <n-button type="warning" :loading="loading" @click="submitApprove(true)">
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
