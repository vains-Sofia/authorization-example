<script setup lang="ts">
import { getCaptcha } from "@/api/user";
import { initRouter } from "@/router/utils";
import { storageLocal } from "@pureadmin/utils";
import { type CSSProperties, ref, computed, reactive } from "vue";
import { useUserStoreHook } from "@/store/modules/user";
import { usePermissionStoreHook } from "@/store/modules/permission";

defineOptions({
  name: "PermissionPage"
});

const elStyle = computed((): CSSProperties => {
  return {
    width: "85vw",
    justifyContent: "start"
  };
});

const ruleForm = reactive({
  username: "admin",
  password: "123456",
  code: "",
  captchaId: ""
});

getCaptcha().then((result: any) => {
  ruleForm.code = result.code;
  ruleForm.captchaId = result.captchaId;
});

const username = ref(useUserStoreHook()?.username);

const options = [
  {
    value: "admin",
    label: "管理员角色"
  },
  {
    value: "common",
    label: "普通角色"
  }
];

function onChange() {
  ruleForm.username = username.value;
  useUserStoreHook()
    .loginByUsername(ruleForm)
    .then(res => {
      if (res.success) {
        storageLocal().removeItem("async-routes");
        usePermissionStoreHook().clearAllCachePage();
        initRouter();
      }
    });
}
</script>

<template>
  <div>
    <p class="mb-2">
      模拟后台根据不同角色返回对应路由，观察左侧菜单变化（管理员角色可查看系统管理菜单、普通角色不可查看系统管理菜单）
    </p>
    <el-card shadow="never" :style="elStyle">
      <template #header>
        <div class="card-header">
          <span>当前角色：{{ username }}</span>
        </div>
      </template>
      <el-select v-model="username" class="!w-[160px]" @change="onChange">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-card>
  </div>
</template>
