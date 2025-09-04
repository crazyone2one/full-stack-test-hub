import type {UserState} from "/@/store/modules/user/types.ts";

export interface ApiResponse<T> {
    code: number;
    data: T;
    message: string;
    messageDetail: string;
}

export interface AuthenticationResponse {
    accessToken: string;
    refreshToken: string;
    user: UserState
}