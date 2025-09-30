import type {UserState} from "/@/store/modules/user/types.ts";
import type {RequestMethodsEnum} from "/@/enums/api-enum.ts";
import type {TreeOption} from "naive-ui";

export interface ApiResponse<T> {
    code: number;
    data: T;
    message: string;
    messageDetail: string;
}

export interface IPageResponse<T> {
    [x: string]: any;

    pageSize: number;
    totalPage: number;
    pageNumber: number;
    totalRow: number;
    records: T[];
}
// 模块树节点
export interface ModuleTreeNode extends TreeOption {
    id: string;
    name: string;
    type: 'MODULE' | 'API';
    children: ModuleTreeNode[];
    attachInfo: {
        method?: keyof typeof RequestMethodsEnum;
        protocol: string;
    }; // 附加信息
    count: 0;
    parentId: string;
    path: string;
    parent?: ModuleTreeNode;
}
export interface AuthenticationResponse {
    accessToken: string;
    refreshToken: string;
    user: UserState
}

export interface ITableQueryParams {
    // 当前页
    page?: number;
    // 每页条数
    pageSize?: number;
    // 排序仅针对单个字段
    sort?: object;
    // 排序仅针对单个字段
    sortString?: string;
    // 表头筛选
    filter?: object;
    // 查询条件
    keyword?: string;

    [key: string]: string | number | object | undefined;
}

export interface IUserSelectorOption {
    id: string;
    name: string;
    email: string;
    disabled?: boolean;

    [key: string]: string | number | boolean | undefined;
}

export interface IBatchActionQueryParams {
    excludeIds?: string[]; // 排除的id
    selectedIds?: string[];
    selectAll: boolean; // 是否跨页全选
    params?: ITableQueryParams; // 查询参数
    currentSelectCount?: number; // 当前选中的数量
    condition?: any; // 查询条件
    [key: string]: any;
}

export interface BatchApiParams {
    selectIds: string[]; // 已选 ID 集合，当 selectAll 为 false 时接口会使用该字段
    excludeIds?: string[]; // 需要忽略的用户 id 集合，当selectAll为 true 时接口会使用该字段
    selectAll: boolean; // 是否跨页全选，即选择当前筛选条件下的全部表格数据
    condition: Record<string, any>; // 当前表格查询的筛选条件
    currentSelectCount?: number; // 当前已选择的数量
    projectId?: string; // 项目 ID
    moduleIds?: (string | number)[]; // 模块 ID 集合
    versionId?: string; // 版本 ID
    refId?: string; // 版本来源
    protocols?: string[]; // 协议集合
    // combineSearch?: FilterResult;
}

export interface ITransferOption {
    label: string;
    value: string | number;
    disabled?: boolean;
    selected?: boolean;
    children?: ITransferOption[];
}