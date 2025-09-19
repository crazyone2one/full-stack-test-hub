import {
    defineConfig,
    presetAttributify,
    presetIcons,
    presetTypography,
    presetWind3,
    transformerDirectives,
    transformerVariantGroup
} from 'unocss'
// loader helpers
import {FileSystemIconLoader} from '@iconify/utils/lib/loader/node-loaders'

const iconsDir = "./src/assets/icons";
export default defineConfig({
    shortcuts: {
        'add-icon': 'i-mdi:plus-circle cursor-pointer',
        'one-line-text': 'overflow-hidden overflow-ellipsis whitespace-nowrap'
    },
    theme: {
        colors: {
            // ...
        }
    },
    presets: [
        presetWind3(),
        presetAttributify(),
        presetIcons({
            // 设置全局图标默认属性
            extraProperties: {
                width: "1em",
                height: "1em",
            },
            collections: {
                'local': FileSystemIconLoader('./src/assets/icons', (svg) => {
                    // 如果 SVG 文件未定义 `fill` 属性，则默认填充 `currentColor`
                    // 这样图标颜色会继承文本颜色，方便在不同场景下适配
                    return svg.includes('fill="')
                        ? svg
                        : svg.replace(/^<svg /, '<svg fill="currentColor" ');
                }),
                'hub': {
                    'down': '<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 24 24"><path d="M6 9l6 6l6-6H6" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path></svg>',
                },
                mdi: () => import('@iconify-json/mdi/icons.json').then(i => i.default)
            }
        }),
        presetTypography(),
    ],
    transformers: [
        transformerDirectives(),
        transformerVariantGroup(),
    ],
})