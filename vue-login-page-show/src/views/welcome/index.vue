<script setup lang="ts">
defineOptions({
  name: "Welcome"
});
import router from "../../router";
import UserLock from "@iconify-icons/ri/admin-line";
import UnLock from "@iconify-icons/ri/lock-unlock-line";
import Consent from "@iconify-icons/ri/drag-move-fill";
import ShieldCheck from "@iconify-icons/ri/shield-keyhole-line";
import ShieldCheckSuccess from "@iconify-icons/ri/shield-check-line";
import { useDark } from "./utils";
import ReCol from "@/components/ReCol";

const { isDark } = useDark();

/**
 * 根据路径跳转路由
 * @param path 路由路径
 */
const pathRoute = (path: string) => {
  router.push({ path });
};
</script>

<template>
  <div>
    <el-row :gutter="24" justify="start">
      <re-col
        v-motion
        class="mb-[18px]"
        :value="6"
        :md="12"
        :sm="12"
        :xs="24"
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 80 * (0 + 1)
          }
        }"
        @click="pathRoute('/OAuth2Redirect')"
      >
        <el-card class="line-card" shadow="hover">
          <div class="flex justify-between">
            <span class="text-md font-medium"> 授权码模式 </span>
            <div
              class="w-8 h-8 flex justify-center items-center rounded-md"
              :style="{
                backgroundColor: isDark ? 'transparent' : '#effaff'
              }"
            >
              <IconifyIconOffline
                :icon="ShieldCheck"
                color="#41b6ff"
                width="18"
              />
            </div>
          </div>
          <div class="flex justify-between items-start mt-3">
            以授权码模式发起授权申请
          </div>
        </el-card>
      </re-col>
      <re-col
        v-motion
        class="mb-[18px]"
        :value="6"
        :md="12"
        :sm="12"
        :xs="24"
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 80 * (1 + 1)
          }
        }"
      >
        <el-card class="line-card" shadow="hover">
          <div class="flex justify-between">
            <span class="text-md font-medium"> PKCE模式 </span>
            <div
              class="w-8 h-8 flex justify-center items-center rounded-md"
              :style="{
                backgroundColor: isDark ? 'transparent' : '#effaff'
              }"
            >
              <IconifyIconOffline
                :icon="ShieldCheck"
                color="#41b6ff"
                width="18"
              />
            </div>
          </div>
          <div class="flex justify-between items-start mt-3">
            以PKCE模式发起授权申请
          </div>
        </el-card>
      </re-col>
      <re-col
        v-motion
        class="mb-[18px]"
        :value="6"
        :md="12"
        :sm="12"
        :xs="24"
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 80 * (2 + 1)
          }
        }"
        @click="pathRoute('/login')"
      >
        <el-card class="line-card" shadow="hover">
          <div class="flex justify-between">
            <span class="text-md font-medium"> 登录页面 </span>
            <div
              class="w-8 h-8 flex justify-center items-center rounded-md"
              :style="{
                backgroundColor: isDark ? 'transparent' : '#effaff'
              }"
            >
              <IconifyIconOffline :icon="UnLock" color="#41b6ff" width="18" />
            </div>
          </div>
          <div class="flex justify-between items-start mt-3">
            独立登录页面，完成以后返回首页
          </div>
        </el-card>
      </re-col>
      <re-col
        v-motion
        class="mb-[18px]"
        :value="6"
        :md="12"
        :sm="12"
        :xs="24"
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 80 * (3 + 1)
          }
        }"
        @click="pathRoute('/consent')"
      >
        <el-card class="line-card" shadow="hover">
          <div class="flex justify-between">
            <span class="text-md font-medium"> 授权确认页面 </span>
            <div
              class="w-8 h-8 flex justify-center items-center rounded-md"
              :style="{
                backgroundColor: isDark ? 'transparent' : '#effaff'
              }"
            >
              <IconifyIconOffline :icon="Consent" color="#41b6ff" width="18" />
            </div>
          </div>
          <div class="flex justify-between items-start mt-3">
            单独打开会报错,oauth2流程中查看
          </div>
        </el-card>
      </re-col>
      <re-col
        v-motion
        class="mb-[18px]"
        :value="6"
        :md="12"
        :sm="12"
        :xs="24"
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 80 * (4 + 1)
          }
        }"
      >
        <el-card class="line-card" shadow="hover">
          <div class="flex justify-between">
            <span class="text-md font-medium"> 设备码验证页面 </span>
            <div
              class="w-8 h-8 flex justify-center items-center rounded-md"
              :style="{
                backgroundColor: isDark ? 'transparent' : '#effaff'
              }"
            >
              <IconifyIconOffline :icon="UserLock" color="#41b6ff" width="18" />
            </div>
          </div>
          <div class="flex justify-between items-start mt-3">
            单独打开无效,oauth2流程中使用
          </div>
        </el-card>
      </re-col>
      <re-col
        v-motion
        class="mb-[18px]"
        :value="6"
        :md="12"
        :sm="12"
        :xs="24"
        :initial="{
          opacity: 0,
          y: 100
        }"
        :enter="{
          opacity: 1,
          y: 0,
          transition: {
            delay: 80 * (4 + 1)
          }
        }"
      >
        <el-card class="line-card" shadow="hover">
          <div class="flex justify-between">
            <span class="text-md font-medium"> 验证成功页面 </span>
            <div
              class="w-8 h-8 flex justify-center items-center rounded-md"
              :style="{
                backgroundColor: isDark ? 'transparent' : '#effaff'
              }"
            >
              <IconifyIconOffline
                :icon="ShieldCheckSuccess"
                color="#41b6ff"
                width="18"
              />
            </div>
          </div>
          <div class="flex justify-between items-start mt-3">
            设备码流程验证成功后展示
          </div>
        </el-card>
      </re-col>
    </el-row>
  </div>
</template>
