import vue from '@vitejs/plugin-vue'
import monacoEditorPlugin from 'vite-plugin-monaco-editor'

import createAutoImport from './auto-import'
import createSvgIcon from './svg-icon'
import createCompression from './compression'
import createSetupExtend from './setup-extend'

export default function createVitePlugins(viteEnv, isBuild = false) {
    const vitePlugins = [vue()]
    vitePlugins.push(createAutoImport())
	vitePlugins.push(createSetupExtend())
    vitePlugins.push(createSvgIcon(isBuild))
    vitePlugins.push(monacoEditorPlugin.default({
        languageWorkers: ['editorWorkerService', 'css', 'html', 'json', 'typescript']
    }))
	isBuild && vitePlugins.push(...createCompression(viteEnv))
    return vitePlugins
}
