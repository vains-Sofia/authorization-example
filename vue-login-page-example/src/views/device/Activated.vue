<script lang="ts" setup></script>
<template>
  <header>
    <img alt="Vue logo" class="logo" src="../../assets/devices.png" width="125" height="125" />

    <div class="wrapper">
      <HelloWorld msg="设备激活" />
    </div>
  </header>

  <main>
    <div style="font-size: 30px">
      您已成功激活您的设备。
      <br />
      请返回到您的设备继续。
    </div>
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
