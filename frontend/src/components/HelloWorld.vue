<script setup lang="ts">
import {ref} from 'vue'
import {useRequest} from "alova/client";
import {authApi} from "../api/modules/auth.ts";

defineProps<{ msg: string }>()

const count = ref(0)
const {send} = useRequest(() => authApi.login({username: 'admin', password: '123456'}), {immediate: false})
const handleLogin = () => {
  send().then(res => {
    console.log(res)
  })
}
</script>

<template>
  <h1>{{ msg }}</h1>

  <div class="card">
    <button type="button" @click="count++">count is {{ count }}</button>
    <p>
      Edit
      <code>components/HelloWorld.vue</code> to test HMR
    </p>
  </div>
  <button type="button" @click="handleLogin">test login</button>

</template>

<style scoped>
.read-the-docs {
  color: #888;
}
</style>
