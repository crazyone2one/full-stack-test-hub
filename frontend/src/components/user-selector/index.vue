<script setup lang="ts">

import {initOptionsFunc, UserRequestTypeEnum, type UserRequestTypeEnumType} from "/@/components/user-selector/utils.ts";
import {onMounted, ref, watch} from "vue";
import type {SelectOption} from "naive-ui";

const props = withDefaults(
    defineProps<{
      disabled?: boolean; // 是否禁用
      disabledKey?: string; // 禁用的key
      placeholder?: string;
      valueField?: string;
      labelField?: string;
      loadOptionParams: Record<string, any>; // 加载选项的参数
      type?: UserRequestTypeEnumType; // 加载选项的类型
      atLeastOne?: boolean; // 是否至少选择一个
    }>(),
    {
      disabled: false,
      disabledKey: 'disabled',
      type: UserRequestTypeEnum.SYSTEM_USER_GROUP,
      atLeastOne: false,
      valueField: 'id',
      labelField: 'name',
    }
);
const currentValue = defineModel<string[]>('modelValue', {default: []});
const innerValue = ref<string[]>([]);
const options = ref<Array<SelectOption>>([])
const loadList = async () => {
  const {keyword, ...rest} = props.loadOptionParams;
  const list = (await initOptionsFunc(props.type, {keyword, ...rest})) || [];
  if (list.length > 0) {
    options.value = []
    list.forEach(u => options.value.push(
        {
          'label': u.email !== '' ? `${u.name} (${u.email})` : `${u.name}`,
          'value': u.id,
          'disabled': u.checkRoleFlag as boolean
        }
    ))
  }
}
watch(
    () => innerValue.value,
    (value) => {
      const values: string[] = [];
      value.forEach((item) => {
        values.push(item);
      });
      currentValue.value = values;
    }
);
onMounted(() => {
  loadList()
})
</script>

<template>
  <n-select v-model:value="innerValue" :options="options" multiple :disabled="props.disabled"
            :placeholder="props.placeholder || '请选择成员'">

  </n-select>
</template>

<style scoped>

</style>