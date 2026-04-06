import DefaultTheme from 'vitepress/theme'
import Mermaid from '../components/Mermaid.vue'
import type { Theme } from 'vitepress'
import './xinclude.css'

export default {
  extends: DefaultTheme,
  enhanceApp({ app }) {
    app.component('Mermaid', Mermaid)
  }
} satisfies Theme
