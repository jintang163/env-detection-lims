import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAppStore = defineStore('app', () => {
  const collapsed = ref<boolean>(false)
  const title = ref<string>(import.meta.env.VITE_APP_TITLE || 'LIMS系统')

  const toggleCollapsed = () => {
    collapsed.value = !collapsed.value
  }

  const setCollapsed = (value: boolean) => {
    collapsed.value = value
  }

  return {
    collapsed,
    title,
    toggleCollapsed,
    setCollapsed
  }
})
