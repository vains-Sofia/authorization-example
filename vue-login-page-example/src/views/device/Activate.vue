<script setup lang="ts">
import { ref } from 'vue'
import { createDiscreteApi } from 'naive-ui'
import { deviceVerification } from '@/api/Login'
import { getQueryString } from '@/util/GlobalUtils'

const { message } = createDiscreteApi(['message'])

// 提交按钮加载状态
const loading = ref(false)

const userCode = ref({
  userCode: getQueryString('userCode')
})

/**
 * 验证设备码
 */
const submit = () => {
  if (!userCode.value.userCode) {
    message.warning(`请输入设备码`)
    return
  }
  loading.value = true
  const data = {
    user_code: userCode.value.userCode
  }

  deviceVerification(data, getQueryString('nonceId') as string)
    .then((result: any) => {
      if (result.success) {
        window.location.href = result.data
      } else {
        message.warning(result.message)
      }
    })
    .catch((e: any) => {
      message.warning(`提交设备码失败：${e.message || e.statusText}`)
    })
    .finally(() => (loading.value = false))
}

// 如果地址栏有参数直接提交
if (userCode.value.userCode) {
  submit()
}
</script>

<template>
  <header>
    <img alt="Vue logo" class="logo" src="../../assets/devices.png" width="125" height="125" />

    <div class="wrapper">
      <HelloWorld msg="设备激活" />
    </div>
  </header>

  <main>
    <n-card> 输入激活码对设备进行授权。 </n-card>
    <br />
    <n-card>
      <n-form-item-row label="Activation Code">
        <n-input
          v-model:value="userCode.userCode"
          placeholder="User Code，格式：XXXX-XXXX，错误的格式后端会报错"
          maxlength="9"
          show-count
          clearable
        />
      </n-form-item-row>
      <n-button type="info" :loading="loading" @click="submit" block strong> 提交 </n-button>
    </n-card>
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
