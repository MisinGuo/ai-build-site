<template>
  <div
    ref="scrollContainer"
    class="scroll-container"
    @wheel.prevent="handleScroll"
  >
    <div ref="scrollWrapper" class="scroll-wrapper">
      <slot />
    </div>
  </div>
</template>

<script setup>
import useTagsViewStore from '@/store/modules/tagsView'

const tagAndTagSpacing = ref(4)
const scrollContainer = ref(null)

const emits = defineEmits(['scroll'])

onMounted(() => {
  scrollContainer.value.addEventListener('scroll', emitScroll, true)
})

onBeforeUnmount(() => {
  scrollContainer.value.removeEventListener('scroll', emitScroll)
})

function handleScroll(e) {
  // 直接使用 deltaY 作为横向滚动量，deltaY 对应鼠标滚轮上下滚动
  scrollContainer.value.scrollLeft += e.deltaY || e.deltaX
}

const emitScroll = () => {
  emits('scroll')
}

const tagsViewStore = useTagsViewStore()
const visitedViews = computed(() => tagsViewStore.visitedViews)

function moveToTarget(currentTag) {
  const $container = scrollContainer.value
  const $containerWidth = $container.offsetWidth

  let firstTag = null
  let lastTag = null

  if (visitedViews.value.length > 0) {
    firstTag = visitedViews.value[0]
    lastTag = visitedViews.value[visitedViews.value.length - 1]
  }

  if (firstTag === currentTag) {
    $container.scrollLeft = 0
  } else if (lastTag === currentTag) {
    $container.scrollLeft = $container.scrollWidth - $containerWidth
  } else {
    const tagListDom = document.getElementsByClassName('tags-view-item')
    const currentIndex = visitedViews.value.findIndex(item => item === currentTag)
    let prevTag = null
    let nextTag = null
    for (const k in tagListDom) {
      if (k !== 'length' && Object.hasOwnProperty.call(tagListDom, k)) {
        if (tagListDom[k].dataset.path === visitedViews.value[currentIndex - 1]?.path) {
          prevTag = tagListDom[k]
        }
        if (tagListDom[k].dataset.path === visitedViews.value[currentIndex + 1]?.path) {
          nextTag = tagListDom[k]
        }
      }
    }

    if (!prevTag || !nextTag) return

    const afterNextTagOffsetLeft = nextTag.offsetLeft + nextTag.offsetWidth + tagAndTagSpacing.value
    const beforePrevTagOffsetLeft = prevTag.offsetLeft - tagAndTagSpacing.value

    if (afterNextTagOffsetLeft > $container.scrollLeft + $containerWidth) {
      $container.scrollLeft = afterNextTagOffsetLeft - $containerWidth
    } else if (beforePrevTagOffsetLeft < $container.scrollLeft) {
      $container.scrollLeft = beforePrevTagOffsetLeft
    }
  }
}

defineExpose({ moveToTarget })
</script>

<style lang='scss' scoped>
.scroll-container {
  white-space: nowrap;
  position: relative;
  width: 100%;
  height: 34px;
  overflow-x: auto;
  overflow-y: hidden;

  // 自定义滚动条（Webkit 内核浏览器）
  &::-webkit-scrollbar {
    height: 6px;
  }
  &::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.05);
    border-radius: 3px;
  }
  &::-webkit-scrollbar-thumb {
    background: rgba(144, 147, 153, 0.6);
    border-radius: 3px;
    cursor: pointer;
    &:hover {
      background: rgba(100, 100, 100, 0.8);
    }
    &:active {
      background: rgba(60, 60, 60, 0.9);
    }
  }

  .scroll-wrapper {
    display: inline-block;
    min-width: 100%;
  }
}
</style>