<script setup lang="ts">
import {reactive, ref} from 'vue';
import {type FormInst, NButton, NCard, NCheckbox, NForm, NFormItem, NInput, NText, useMessage} from 'naive-ui';
import {authApi} from "/@/api/modules/auth.ts";
import {useForm} from "alova/client";
import {setToken} from "/@/utils/auth.ts";
import {useAppStore, useUserStore} from "/@/store";
import {useRouter} from "vue-router";
import {NO_PROJECT_ROUTE_NAME, NO_RESOURCE_ROUTE_NAME} from "/@/router/constants.ts";
import {getFirstRouteNameByPermission, routerNameHasPermission} from "/@/utils/permissions.ts";

// 消息提示
const message = useMessage();
const userStore = useUserStore()
const appStore = useAppStore()
// 表单引用
const loginFormRef = ref<FormInst | null>(null);

// 加载状态
const isLoading = ref(false);

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  rememberMe: false
});

// 表单验证规则
const formRules = {
  username: [{required: true, message: '请输入用户名', trigger: ['blur', 'input']}],
  password: [
    {required: true, message: '请输入密码', trigger: ['blur', 'input']},
    {min: 6, message: '密码长度不能少于6位', trigger: 'blur'}
  ]
};
const router = useRouter()
const {send} = useForm(() => authApi.login(formData), {immediate: false})
// 登录处理
const handleLogin = async () => {
  // 表单验证
  const valid = await loginFormRef.value?.validate();
  if (!valid) return;
  isLoading.value = true;
  try {
    send().then(res => {
      const {accessToken, refreshToken, user} = res
      setToken(accessToken, refreshToken)
      userStore.setInfo(user)
      appStore.setCurrentOrgId(user.lastOrganizationId || '');
      appStore.setCurrentProjectId(user.lastProjectId || '');
      // 登录成功处理
      message.success('登录成功');
      const {redirect, ...othersQuery} = router.currentRoute.value.query;
      const redirectHasPermission =
          redirect &&
          ![NO_RESOURCE_ROUTE_NAME, NO_PROJECT_ROUTE_NAME].includes(redirect as string) &&
          routerNameHasPermission(redirect as string, router.getRoutes());
      const currentRouteName = getFirstRouteNameByPermission(router.getRoutes());
      // console.log(currentRouteName)
      router.push({
        name: redirectHasPermission ? (redirect as string) : currentRouteName,
        query: {
          ...othersQuery,
          orgId: appStore.currentOrgId,
          pId: appStore.currentProjectId,
        },
      });
      // const route = router.currentRoute.value
      // const redirect = route.query.redirect?.toString()
      // router.replace(redirect ?? route.redirectedFrom?.fullPath ?? '/')
    })
  } catch (error) {
    message.error('登录失败: 用户名或密码错误');
    console.error('登录错误:', error);
  } finally {
    isLoading.value = false;
  }
};

// 忘记密码处理
const handleForgotPassword = () => {
  message.info('跳转到密码重置页面');
  // 实际项目中添加密码重置逻辑
};
</script>

<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="login-bg"></div>

    <!-- 登录卡片 -->
    <n-card
        class="login-card"
        title="系统登录"
        :bordered="false"
        size="large"
    >
      <n-form
          ref="loginFormRef"
          :model="formData"
          :rules="formRules"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
          class="mt-[20px]"
      >
        <!-- 用户名输入 -->
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="formData.username" placeholder="请输入用户名" clearable/>
        </n-form-item>

        <!-- 密码输入 -->
        <n-form-item label="密码" path="password">
          <n-input v-model:value="formData.password" type="password" placeholder="请输入密码" clearable
                   show-password-on="click"/>
        </n-form-item>

        <!-- 记住密码与忘记密码 -->
        <div class="form-actions">
          <n-checkbox v-model:checked="formData.rememberMe">
            记住密码
          </n-checkbox>
          <n-text tag="a" class="forgot-password" @click="handleForgotPassword">
            忘记密码?
          </n-text>
        </div>

        <!-- 登录按钮 -->
        <n-form-item>
          <n-button type="primary" size="large" class="w-full" :loading="isLoading" @click="handleLogin">
            登录
          </n-button>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  box-sizing: border-box;
  position: relative;
}

.login-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #cf83ea 0%, #c9c9ec 100%);
  z-index: -1;
}

.login-card {
  width: 100%;
  max-width: 400px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  background-color: #fff;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.forgot-password {
  color: #6366f1;
  cursor: pointer;
}
</style>