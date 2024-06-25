<script setup lang="ts">
import { type Ref, ref } from "vue";
import { message } from "@/utils/message";
import { avatar } from "./utils/static";
import ConsentLeft from "@/components/ConsentLeft/Index.vue";
import { getConsentParameters, submitApproveScope } from "@/api/user";

// 获取授权确认信息响应
const consentResult: Ref<any> = ref();
// 所有的scope
const scopes = ref();
// 已授权的scope
const approvedScopes = ref();
// 提交/拒绝按钮加载状态
const loading = ref(false);

defineOptions({
  name: "Consent"
});

/**
 * 初始化需要授权确认的客户端与scope
 */
getConsentParameters(window.location.search)
  .then((result: any) => {
    consentResult.value = result;
    scopes.value = [...result.previouslyApprovedScopes, ...result.scopes];
    approvedScopes.value = result.previouslyApprovedScopes.map(
      (e: any) => e.scope
    );
  })
  .catch((e: any) => {
    message(`获取客户端与scope信息失败：${e.message || e.statusText}`, {
      type: "warning"
    });
  });

/**
 * 提交授权确认
 *
 * @param cancel true为取消
 */
const submitApprove = (cancel: boolean) => {
  if (!consentResult.value) {
    message(`初始化未完成，无法提交`, { type: "warning" });
    return;
  }
  loading.value = true;
  const data = new FormData();
  if (!cancel) {
    // 如果不是取消添加scope
    if (
      approvedScopes.value !== null &&
      typeof approvedScopes.value !== "undefined" &&
      approvedScopes.value.length > 0
    ) {
      approvedScopes.value.forEach((e: any) => data.append("scope", e));
    }
  }
  data.append("state", consentResult.value.state);
  data.append("client_id", consentResult.value.clientId);
  data.append("user_code", consentResult.value.userCode);

  submitApproveScope(
    // @ts-ignore
    new URLSearchParams(data),
    consentResult.value.requestURI
  )
    .then((result: any) => {
      window.location.href = result;
    })
    .catch((e: any) => {
      message(`您未选择scope或拒绝了本次授权申请`, {
        type: "warning"
      });
    })
    .finally(() => (loading.value = false));
};
</script>

<template>
  <el-row :gutter="10">
    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12" class="left">
      <avatar class="avatar logo" />

      <div class="wrapper">
        <ConsentLeft msg="OAuth 授权确认" />
      </div>
    </el-col>
    <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
      <el-card v-if="consentResult && consentResult.userCode">
        您已经提供了代码
        <b>{{ consentResult.userCode }}</b>
        ，请验证此代码是否与设备上显示的代码匹配。
      </el-card>
      <br />
      <el-card v-if="consentResult" shadow="never">
        <template #header style="display: flex">
          <div>
            <h3>{{ consentResult.clientName }} 客户端</h3>
          </div>
          <div>
            账号：
            <b>{{ consentResult.principalName }}</b>
          </div>
        </template>
        此第三方应用请求获得以下权限
      </el-card>
      <el-scrollbar style="max-height: 230px">
        <el-checkbox-group v-model="approvedScopes">
          <ul
            class="infinite-list"
            style="overflow: auto"
            :infinite-scroll-disabled="true"
          >
            <li v-for="scope in scopes" :key="scope" class="infinite-list-item">
              <div class="scopes-wrapper">
                <el-checkbox :value="scope.scope" style="margin-right: 20px">
                </el-checkbox>
                <div style="height: auto; width: 100%">
                  <div class="scope-item scope-title">{{ scope.scope }}</div>
                  <div class="scope-item">
                    {{ scope.description }}
                  </div>
                </div>
              </div>
            </li>
          </ul>
        </el-checkbox-group>
      </el-scrollbar>
      <br />
      <el-button
        type="info"
        :loading="loading"
        @click="submitApprove(false)"
        strong
      >
        &nbsp;&nbsp;&nbsp;&nbsp;确&nbsp;&nbsp;&nbsp;&nbsp;定&nbsp;&nbsp;&nbsp;&nbsp;
      </el-button>
      &nbsp;&nbsp;&nbsp;&nbsp;
      <el-button type="warning" :loading="loading" @click="submitApprove(true)">
        &nbsp;&nbsp;&nbsp;&nbsp;拒&nbsp;&nbsp;&nbsp;&nbsp;绝&nbsp;&nbsp;&nbsp;&nbsp;
      </el-button>
    </el-col>
  </el-row>
</template>

<style scoped>
.avatar {
  width: 125px;
  height: 125px;
}

aside {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

::v-deep(.el-card__header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

@media only screen and (min-width: 992px) {
  .left {
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  .el-row {
    width: 100%;
    height: 100%;
    align-items: center;
  }
}

.el-row {
  padding: 2rem;
}

b,
h3,
::v-deep(.el-card-header__main) {
  font-weight: bold !important;
}

.infinite-list {
  height: 300px;
  padding: 0;
  margin: 0;
  list-style: none;
}
.infinite-list .infinite-list-item {
  display: flex;
  align-items: center;
  justify-content: center;
  /* height: 75px; */
  padding: 10px;
  border-bottom: 1px solid var(--el-color-info-light-8);
}
.infinite-list .infinite-list-item + .list-item {
  margin-top: 10px;
}
.scopes-wrapper {
  display: flex;
  width: 100%;
  align-items: center;
  font-size: 16px;
}
.scope-item {
  display: block;
  height: auto;
  width: 100%;
  color: rgb(51, 54, 57);
  font-size: 14px;
  font-weight: 400;
  line-height: 1.6;
}

.scope-title {
  color: rgb(31, 34, 37) !important;
  font-weight: 500;
  font-size: 16px;
  margin-bottom: 4px;
}
</style>
