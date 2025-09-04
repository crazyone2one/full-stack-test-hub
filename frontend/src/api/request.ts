import {createAlova} from 'alova';
import adapterFetch from 'alova/fetch';
import VueHook from 'alova/vue';
import {clearToken, getToken, setToken} from "/@/utils/auth.ts";
import useAppStore from "/@/store/modules/app";
import {createServerTokenAuthentication} from "alova/client";
import {authApi} from "/@/api/modules/auth.ts";

const {onAuthRequired, onResponseRefreshToken} = createServerTokenAuthentication({
    refreshTokenOnSuccess: {
        isExpired: (response, method) => {
            const isExpired = method.meta && method.meta.isExpired;
            return response.status === 401 && !isExpired;
        },
        handler: async (_response, method) => {
            method.meta = method.meta || {};
            method.meta.isExpired = true;
            try {
                const {
                    accessToken,
                    refreshToken
                } = await authApi.refreshToken({"refreshToken": getToken().refreshToken});
                setToken(accessToken, refreshToken)
            } catch (error) {
                // token刷新失败，跳转回登录页
                location.href = '/login';
                // 并抛出错误
                throw error;
            }
        }
    },
    logout: () => {
        // 登出处理
        clearToken()
    },
});
export const alovaInstance = createAlova({
    baseURL: `${window.location.origin}/${import.meta.env.VITE_API_BASE_URL}`,
    statesHook: VueHook,
    requestAdapter: adapterFetch(),
    timeout: 300 * 1000,
    beforeRequest: onAuthRequired((method) => {
        const token = getToken();
        const appStore = useAppStore();
        if (token && method.meta?.authRole != null) {
            method.config.headers = {
                ...method.config.headers,
                'ORGANIZATION': appStore.currentOrgId,
                'PROJECT': appStore.currentProjectId,
                'Authorization': `Bearer ${token.accessToken}`,
            };
        }
    }),
    responded: onResponseRefreshToken({
        // 成功响应处理
        onSuccess: async (response, method) => {
            if (method.meta?.isBlob) {
                return response.blob();
            }
            // 检查 HTTP 状态码
            if (response.status >= 200 && response.status < 300) {
                const data = await response.json();
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
    }),

});