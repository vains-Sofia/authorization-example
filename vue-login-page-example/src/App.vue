<script setup lang="ts">
import { ref } from 'vue'
import HelloWorld from './components/HelloWorld.vue'
import axios from 'axios'
import { type CountdownProps, createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])

// 定义登录提交的对象
const loginModel = ref({
  code: '',
  username: '',
  password: '',
  loginType: '',
  captchaId: '',
  nonceId: getQueryString('nonceId')
})

// 图形验证码的base64数据
let captchaImage = ref('')
// 图形验证码的值
let captchaCode = ''
// 是否开始倒计时
const counterActive = ref(false)

/**
 * 获取图形验证码
 */
const getCaptcha = () => {
  axios({
    method: 'GET',
    url: 'http://192.168.1.102:8080/getCaptcha'
  }).then((r) => {
    let result = r.data
    if (result.success) {
      captchaCode = result.data.code
      captchaImage.value = result.data.imageData
      loginModel.value.captchaId = result.data.captchaId
    } else {
      message.warning(result.message)
    }
  })
}

/**
 * 提交登录表单
 */
const submitLogin = () => {
  loginModel.value.loginType = 'passwordLogin'
  axios({
    method: 'post',
    url: 'http://192.168.1.102:8080/login',
    headers: {
      nonceId: loginModel.value.nonceId,
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data: loginModel.value
  }).then((r) => {
    let result = r.data
    if (result.success) {
      // message.info(`登录成功`)
      let target = getQueryString('target')
      if (target) {
        window.location.href = target
      }
    } else {
      message.warning(result.message)
    }
  })
}

/**
 * 提交短信登录表单
 */
const submitSmsLogin = () => {
  loginModel.value.loginType = 'smsCaptcha'
  axios({
    method: 'post',
    url: 'http://192.168.1.102:8080/login',
    headers: {
      nonceId: loginModel.value.nonceId,
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data: loginModel.value
  }).then((r) => {
    let result = r.data
    if (result.success) {
      message.info(`登录成功`)
      let target = getQueryString('target')
      if (target) {
        window.location.href = target
      }
    } else {
      message.warning(result.message)
    }
  })
}

/**
 * 获取短信验证码
 */
const getSmsCaptcha = () => {
  if (!loginModel.value.username) {
    message.warning('请先输入手机号.')
    return
  }
  if (!loginModel.value.code) {
    message.warning('请先输入验证码.')
    return
  }
  if (loginModel.value.code !== captchaCode) {
    message.warning('验证码错误.')
    return
  }
  axios({
    method: 'get',
    url: `http://192.168.1.102:8080/getSmsCaptcha?phone=${loginModel.value.username}`
  }).then((r) => {
    let result = r.data
    if (result.success) {
      message.info(`获取短信验证码成功，固定为：${result.data}`)
      counterActive.value = true
    } else {
      message.warning(result.message)
    }
  })
}

/**
 * 切换时更新验证码
 */
const handleUpdateValue = () => {
  getCaptcha()
}

/**
 * 倒计时结束
 */
const onFinish = () => {
  counterActive.value = false
}

/**
 * 倒计时显示内容
 */
const renderCountdown: CountdownProps['render'] = ({ hours, minutes, seconds }) => {
  return `${seconds}`
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

getCaptcha()
</script>

<template>
  <header>
    <img alt="Vue logo" class="logo" src="./assets/logo.svg" width="125" height="125" />

    <div class="wrapper">
      <HelloWorld msg="统一认证平台" />
    </div>
  </header>

  <main>
    <n-card title="">
      <n-tabs
        default-value="signin"
        size="large"
        justify-content="space-evenly"
        @update:value="handleUpdateValue"
      >
        <n-tab-pane name="signin" tab="账号登录">
          <n-form>
            <n-form-item-row label="用户名">
              <n-input v-model:value="loginModel.username" placeholder="手机号 / 邮箱" />
            </n-form-item-row>
            <n-form-item-row label="密码">
              <n-input
                v-model:value="loginModel.password"
                type="password"
                show-password-on="mousedown"
                placeholder="密码"
              />
            </n-form-item-row>
            <n-form-item-row label="验证码">
              <n-input-group>
                <n-input v-model:value="loginModel.code" placeholder="请输入验证码" />
                <n-image
                  @click="getCaptcha"
                  width="130"
                  height="34"
                  :src="captchaImage"
                  preview-disabled
                />
              </n-input-group>
            </n-form-item-row>
          </n-form>
          <n-button type="info" @click="submitLogin" block strong> 登录 </n-button>
        </n-tab-pane>
        <n-tab-pane name="signup" tab="短信登录">
          <n-form>
            <n-form-item-row label="手机号">
              <n-input v-model:value="loginModel.username" placeholder="手机号 / 邮箱" />
            </n-form-item-row>
            <n-form-item-row label="验证码">
              <n-input-group>
                <n-input v-model:value="loginModel.code" placeholder="请输入验证码" />
                <n-image
                  @click="getCaptcha"
                  width="130"
                  height="34"
                  :src="captchaImage"
                  preview-disabled
                />
              </n-input-group>
            </n-form-item-row>
            <n-form-item-row label="验证码">
              <n-input-group>
                <n-input v-model:value="loginModel.password" placeholder="请输入验证码" />
                <n-button
                  type="info"
                  @click="getSmsCaptcha"
                  style="width: 130px"
                  :disabled="counterActive"
                >
                  获取验证码
                  <span v-if="counterActive">
                    (
                    <n-countdown
                      :render="renderCountdown"
                      :on-finish="onFinish"
                      :duration="59 * 1000"
                      :active="counterActive"
                    />
                    )</span
                  >
                </n-button>
              </n-input-group>
            </n-form-item-row>
          </n-form>
          <n-button type="info" @click="submitSmsLogin" block strong> 登录 </n-button>
        </n-tab-pane>
      </n-tabs>
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
</style>
