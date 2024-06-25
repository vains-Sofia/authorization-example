<script setup lang="ts">
import router from "@/router";
import { useI18n } from "vue-i18n";
import Motion from "../utils/motion";
import { getTopMenu } from "@/router/utils";
import { Ref, ref, onUnmounted } from "vue";
import { getQueryString } from "@/utils/auth";
import { generateQrCode, fetchByQrCodeId, QrCodeInfo } from "@/api/user";
import ReQrcode from "@/components/ReQrcode";
import { useUserStoreHook } from "@/store/modules/user";

const { t } = useI18n();
const qrCodeInfo: Ref<QrCodeInfo> = ref({});
const qrCodeContent: Ref<string | null> = ref(null);
const leave = ref(false);

const refreshQrCode = () => {
  // 获取二维码id
  generateQrCode().then(data => {
    qrCodeContent.value = data.qrCodeId;
    fetchQrCodeInfo(data.qrCodeId);
  });
};

/**
 * 根据二维码id轮询二维码信息
 * @param qrCodeId 二维码id
 */
const fetchQrCodeInfo = (qrCodeId: string) => {
  fetchByQrCodeId(qrCodeId).then((data: QrCodeInfo) => {
    qrCodeInfo.value = data;
    if (data.qrCodeStatus !== 0 && data.avatarUrl) {
      // 只要不是待扫描并且头像不为空
      // getQrCodeInfo.value.imageData = qrCodeInfo.value.avatarUrl
    }

    if (data.qrCodeStatus !== 2 && !qrCodeInfo.value.expired) {
      setTimeout(() => {
        if (leave.value) {
          return;
        }
        fetchQrCodeInfo(qrCodeId);
      }, 1000);
      return;
    }
    if (data.expired) {
      // 二维码过期
      return;
    }
    if (data.qrCodeStatus === 2) {
      // 已确认
      let href = getQueryString("target");
      if (href) {
        // 确认后将地址重定向
        window.location.href = href;
      } else {
        // 跳转到首页
        router.push(getTopMenu(true).path);
      }
    }
  });
};

// 刷新二维码
refreshQrCode();

onUnmounted(() => {
  leave.value = true;
});
</script>

<template>
  <Motion class="-mt-2 -mb-2">
    <!-- <ReQrcode :text="t('login.pureTest')" /> -->
    <ReQrcode
      :text="qrCodeContent"
      :disabled="qrCodeInfo.expired"
      @disabled-click="refreshQrCode"
    />
  </Motion>
  <Motion :delay="100">
    <el-divider>
      <p class="text-gray-500 text-xs">{{ t("login.pureTip") }}</p>
    </el-divider>
  </Motion>
  <Motion :delay="150">
    <el-button
      class="w-full mt-4"
      @click="useUserStoreHook().SET_CURRENTPAGE(0)"
    >
      {{ t("login.pureBack") }}
    </el-button>
  </Motion>
</template>
