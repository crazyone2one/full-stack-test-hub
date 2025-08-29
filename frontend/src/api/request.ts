import {createAlova} from 'alova';
import adapterFetch from 'alova/fetch';
import VueHook from 'alova/vue';


export const alovaInstance = createAlova({
    baseURL: `${window.location.origin}/${import.meta.env.VITE_API_BASE_URL}`,
    statesHook: VueHook,
    requestAdapter: adapterFetch(),
    timeout: 300 * 1000,
    responded: {
        // 成功响应处理
        onSuccess: async (response, method) => {
            if (method.meta?.isBlob) {
                return response.blob();
            }
            // 检查 HTTP 状态码
            if (response.status >= 200 && response.status < 300) {
                const data = await response.json();

                // 根据后端 ResultHolder 结构处理响应
                if (data.code === 100200) {
                    // 成功响应，返回数据部分
                    return data.data !== undefined ? data.data : data;
                } else {
                    // 业务错误处理
                    throw new Error(data.message || '请求失败');
                }
            } else {
                // HTTP 错误处理
                throw new Error(`HTTP Error: ${response.status}`);
            }
        },
        // 错误响应处理
        onError: (error, _method) => {
            console.error('API Error:', error);
            throw new Error(error.message || '网络请求失败');
        }
    }
});