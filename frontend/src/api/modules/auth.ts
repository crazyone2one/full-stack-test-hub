import {post} from "/@/api";

export const authApi = {
    login: (data: { username: string, password: string }) => post('/auth/login', data),
}