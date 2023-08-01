<script setup lang="ts">
import { ref } from 'vue'
import axios from 'axios'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])

const userCode = ref({
  userCode: getQueryString('userCode')
})

/**
 * 提交授权确认
 *
 * @param cancel true为取消
 */
const submit = () => {
  const data = {
    user_code: userCode.value.userCode
  }
  axios({
    method: 'POST',
    data,
    headers: {
      nonceId: getQueryString('nonceId'),
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    url: `http://192.168.1.102:8080/oauth2/device_verification`
  })
    .then((r) => {
      let result = r.data
      if (result.success) {
        window.location.href = result.data
      } else {
        message.warning(result.message)
      }
    })
    .catch((e) => message.error(e.message))
}

if (userCode.value.userCode) {
  submit()
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
          placeholder="User Code"
          maxlength="9"
          show-count
          clearable
        />
      </n-form-item-row>
      <n-button type="info" @click="submit" block strong> 登录 </n-button>
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
