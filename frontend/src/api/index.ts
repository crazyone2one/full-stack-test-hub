import {alovaInstance} from "./request";


export const get =<T> (url: string, params = {}, options = {}) => {
    return alovaInstance.Get<T>(url, {
        params,
        ...options
    });
}
// 通用 POST 请求
export const post = <T>(url: string, data = {}, options = {}) => {
    return alovaInstance.Post<T>(url, data, {
        ...options
    });
};

// 通用 PUT 请求
export const put = (url: string, data = {}, options = {}) => {
    return alovaInstance.Put(url, data, {
        ...options
    });
};

// 通用 DELETE 请求
export const del = (url: string, options = {}) => {
    return alovaInstance.Delete(url, {
        ...options
    });
};

// 通用 PATCH 请求
export const patch = (url: string, data = {}, options = {}) => {
    return alovaInstance.Patch(url, data, {
        ...options
    });
};