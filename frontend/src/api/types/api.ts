import {
    type FullResponseAssertionType,
    type Language,
    type RequestAssertionConditionType,
    type RequestAuthType,
    type RequestBodyFormatType,
    type RequestConditionProcessor,
    type RequestContentTypeEnumType,
    type RequestExtractEnvType,
    type RequestExtractExpressionEnumType,
    type RequestExtractExpressionRuleType,
    type RequestExtractResultMatchingRule,
    type RequestExtractScope,
    type RequestMethods,
    type RequestParamsTypeType,
    type ResponseAssertionType,
    type ResponseBodyAssertionDocumentType,
    type ResponseBodyAssertionType,
    type ResponseBodyDocumentAssertionType,
    type ResponseBodyFormatType,
    type ResponseBodyXPathAssertionFormat,
    type ResponseCompositionType,
    type ScenarioExecuteStatusType
} from "/@/enums/api-enum.ts";

export interface JsonSchemaCommon {
    id: string;
    title: string; // 参数名称
    type: string; // 参数类型
    description?: string; // 参数描述
    enable: boolean; // 是否启用
    example: string; // 参数值
    defaultValue: string | number | boolean; // 默认值
    pattern?: string; // 正则表达式
    maxLength?: number;
    minLength?: number;
    minimum?: number;
    maximum?: number;
    minItems?: number;
    maxItems?: number;
    format?: string; // 格式化
}

// json-schema 表格组件的表格项
export interface JsonSchemaTableItem extends JsonSchemaCommon {
    required?: boolean; // 是否必填
    children?: JsonSchemaTableItem[];
    parent?: JsonSchemaTableItem; // 父级
    enumValues?: string; // 参数值的枚举
}

// json-schema 规范的结构子项（表格组件转换后的结构）
export interface JsonSchemaItem extends JsonSchemaCommon {
    items?: JsonSchemaItem[]; // 子级，当 type 为array 时，使用该值
    properties?: Record<string, JsonSchemaItem>; // 子级，当 type 为object 时，使用该值
    required?: string[]; // 必填的字段名
    enumValues?: string[]; // 参数值的枚举
}

// json-schema 规范的结构（表格组件转换后的结构）
export interface JsonSchema {
    type: string;
    properties?: Record<string, JsonSchemaItem>;
    items?: JsonSchemaItem[];
    required?: string[];
    description?: string;
    minItems?: number;
    maxItems?: number;
}

export interface ITabItem {
    id: string | number;
    label?: string;
    closable?: boolean;
    unSaved?: boolean; // 未保存
    draggable?: boolean;

    [key: string]: any;
}

// 请求提取结果项
export interface IResponseExtractItem {
    name: string;
    value: string;
    type: RequestExtractEnvType;
    expression: string;
}

// 断言-断言列表表格子项
export interface IResponseAssertionTableItem {
    actualValue: string;
    assertionType: FullResponseAssertionType;
    condition: RequestAssertionConditionType;
    content: string;
    expectedValue: string;
    message: string;
    name: string;
    pass: boolean;
}

// 响应结果
export interface IResponseResult {
    body: string;
    contentType: string;
    filePath?: string;
    headers: string;
    dnsLookupTime: number;
    downloadTime: number;
    latency: number;
    responseCode: number;
    responseTime: number;
    responseSize: number;
    socketInitTime: number;
    sslHandshakeTime: number;
    tcpHandshakeTime: number;
    transferStartTime: number;
    vars: string;
    extractResults: IResponseExtractItem[];
    assertions: IResponseAssertionTableItem[];
    imageUrl?: string; // 返回为图片时的图片地址
}

export interface IRequestResult {
    body: string;
    headers: string;
    url: string;
    method: RequestMethods | string;
    responseResult: IResponseResult;
    isSuccessful?: boolean;
    console?: string;
    status?: ScenarioExecuteStatusType;
    fakeErrorCode?: string;

    [key: string]: any;
}

export interface IRequestTaskResult {
    requestResults: IRequestResult[]; // 请求结果
    console: string;
}

export interface IKeyValueParam {
    id?: string; // id用于前端渲染，后台无此字段
    key: string;
    value: string;

    [key: string]: any; // 用于前端渲染时填充的自定义信息，后台无此字段
}

// 接口请求json-body参数集合信息
export interface ExecuteJsonBody {
    enableJsonSchema?: boolean;
    enableTransition?: boolean;
    jsonSchema?: JsonSchema;
    jsonValue: string;
    // 前端渲染字段
    jsonSchemaTableData?: JsonSchemaTableItem[];
    jsonSchemaTableSelectedRowKeys?: string[];
}

// 执行请求- body 配置-文本格式的 body
export interface ExecuteValueBody {
    value: string;
}

// 接口请求文件信息
export interface RequestFileInfo {
    fileId: string;
    fileName: string;
    fileAlias: string; // 文件别名
    local: boolean; // 是否是本地上传的文件
    delete?: boolean; // 关联文件是否被删除
    [key: string]: any; // 用于前端渲染时填充的自定义信息，后台无此字段
}

// 接口请求binary-body参数集合信息
export interface ExecuteBinaryBody {
    description: string;
    file?: RequestFileInfo;
    sendAsBody?: boolean; // 是否作为正文发送，只有 定义/mock 的响应体有此字段
}

// 响应定义-body
export interface ResponseDefinitionBody {
    bodyType: ResponseBodyFormatType;
    jsonBody: ExecuteJsonBody;
    xmlBody: ExecuteValueBody;
    rawBody: ExecuteValueBody;
    binaryBody: ExecuteBinaryBody;
}

export interface IResponseDefinition {
    id: string | number;
    statusCode: string | number;
    defaultFlag: boolean; // 默认响应标志
    name: string; // 响应名称
    headers: IKeyValueParam[];
    body: ResponseDefinitionBody;

    [key: string]: any; // 用于前端渲染时填充的自定义信息，后台无此字段
}

export interface IResponseItem extends ITabItem, IResponseDefinition {
    showPopConfirm?: boolean; // 是否显示确认弹窗
    showRenamePopConfirm?: boolean; // 是否显示重命名确认弹窗
    responseActiveTab?: ResponseCompositionType; // 当前激活的tab
}

// 执行请求-认证配置
export interface ExecuteAuthConfig {
    authType: RequestAuthType;
    basicAuth: {
        password: string;
        userName: string;
    };
    digestAuth: {
        password: string;
        userName: string;
    };
}

// key-value参数信息
export interface KeyValueParam {
    id?: string; // id用于前端渲染，后台无此字段
    key: string;
    value: string;

    [key: string]: any; // 用于前端渲染时填充的自定义信息，后台无此字段
}

// 接口请求-带开启关闭的参数集合信息
export interface EnableKeyValueParam extends KeyValueParam {
    description: string;
    enable: boolean; // 参数是否启用
}

// 接口请求公共参数集合信息
export interface ExecuteRequestCommonParam extends EnableKeyValueParam {
    encode: boolean; // 是否编码
    maxLength?: number;
    minLength?: number;
    paramType: RequestParamsTypeType; // 参数类型
    required: boolean;
    description: string;
    enable: boolean; // 参数是否启用
}

// 接口请求form-data、x-www-form-urlencoded参数集合信息
export type ExecuteRequestFormBodyFormValue = ExecuteRequestCommonParam & {
    files?: {
        fileId: string;
        fileName: string;
        local: boolean; // 是否是本地上传的文件
        fileAlias: string; // 文件别名
        delete: boolean; // 是否删除
        [key: string]: any; // 用于前端渲染时填充的自定义信息，后台无此字段
    }[];
    contentType?: RequestContentTypeEnumType & string;
};

export interface ExecuteRequestFormBody {
    formValues: ExecuteRequestFormBodyFormValue[];
}

// 执行请求- body 配置
export interface ExecuteBody {
    bodyType: RequestBodyFormatType;
    binaryBody: ExecuteBinaryBody;
    formDataBody: ExecuteRequestFormBody;
    jsonBody: ExecuteJsonBody;
    rawBody: ExecuteValueBody;
    wwwFormBody: ExecuteRequestFormBody;
    xmlBody: ExecuteValueBody;
}

// 执行请求配置
export interface ExecuteOtherConfig {
    autoRedirects: boolean; // 是否自动重定向 默认 false
    certificateAlias: string; // 证书别名
    connectTimeout: number; // 连接超时时间
    followRedirects: boolean; // 是否跟随重定向 默认 false
    responseTimeout: number; // 响应超时时间
}

// 断言-断言公共信息
export interface ResponseAssertionCommon {
    name: string; // 断言名称
    enable: boolean; // 是否启用断言
    assertionType: ResponseAssertionType; // 断言类型
}

// 断言-响应头断言子项
export interface ResponseHeaderAssertionItem {
    condition: RequestAssertionConditionType;
    enable: boolean;
    expectedValue: string;
    header: string; // 响应头
}

// 断言-断言列表泛型
export interface ResponseAssertionGenerics<T> {
    assertions: T[];
    responseFormat?: ResponseBodyXPathAssertionFormat;
}

// 断言-状态码断言
export type ResponseCodeAssertion = Pick<ResponseHeaderAssertionItem, 'condition' | 'expectedValue'>;

// 断言-文档断言-JSON断言\XML断言
export interface ResponseDocumentAssertionElement {
    id?: string;
    arrayVerification: boolean; // 是否组内验证
    children: ResponseDocumentAssertionElement[];
    condition: RequestAssertionConditionType;
    expectedResult: Record<string, any>; // 匹配值 即预期结果
    include: boolean; // 是否必含
    paramName: string; // 参数名
    type: ResponseBodyDocumentAssertionType; // 断言类型
    typeVerification: boolean; // 是否类型验证
}

// 断言-文档断言
export interface ResponseDocumentAssertion {
    enable: boolean; // 是否启用
    documentType: ResponseBodyAssertionDocumentType; // 文档类型
    followApiId: string; // 跟随定义的apiId 传空为不跟随接口定义
    jsonAssertion: ResponseDocumentAssertionElement;
    xmlAssertion: ResponseDocumentAssertionElement;
}

// 断言-断言列表的断言子项
export interface ResponseAssertionItem {
    condition?: RequestAssertionConditionType;
    expectedValue?: string;
    expression: string;
    enable?: boolean;
    extractType?: RequestExtractExpressionEnumType;
    valid?: boolean;
}

// 断言-JSONPath断言子项
export type ResponseJSONPathAssertionItem = ResponseAssertionItem;
// 断言-正则断言子项
export type ResponseRegexAssertionItem = Pick<ResponseAssertionItem, 'expression'>;
// 断言-Xpath断言子项
export type ResponseXPathAssertionItem = Pick<ResponseAssertionItem, 'expression' | 'expectedValue'>;

// 断言-响应体断言
export interface ResponseBodyAssertion {
    assertionBodyType: ResponseBodyAssertionType; // 断言类型
    documentAssertion: ResponseDocumentAssertion; // 文档断言
    jsonPathAssertion: ResponseAssertionGenerics<ResponseJSONPathAssertionItem>; // JSONPath断言
    regexAssertion: ResponseAssertionGenerics<ResponseRegexAssertionItem>; // 正则断言
    xpathAssertion: ResponseAssertionGenerics<ResponseXPathAssertionItem>; // XPath断言
}

// 脚本公共配置
export interface CommonScriptInfo {
    id: string; // 公共脚本id
    name: string; // 公共脚本名称
    script: string; // 公共脚本内容
    params: KeyValueParam[]; // 公共脚本参数
    scriptLanguage: Language; // 脚本语言
}

export interface ScriptCommonConfig {
    enableCommonScript: boolean; // 是否启用公共脚本
    script: string; // 脚本内容
    scriptId: string; // 脚本id
    scriptName: string; // 脚本名称
    scriptLanguage: Language; // 脚本语言
    commonScriptInfo: CommonScriptInfo; // 公共脚本信息
}

// 断言-响应时间断言
export type ResponseTimeAssertion = Pick<ResponseAssertionItem, 'expectedValue'>;
// 断言-脚本断言
export type ResponseScriptAssertion = ScriptCommonConfig;

// 断言-变量断言
export interface ResponseVariableAssertion {
    variableAssertionItems: ResponseAssertionItem[];
}

// 执行请求-断言配置子项
export type ExecuteAssertionItem = ResponseAssertionCommon &
    ResponseCodeAssertion &
    ResponseAssertionGenerics<ResponseHeaderAssertionItem> &
    ResponseBodyAssertion &
    ResponseTimeAssertion &
    ResponseScriptAssertion &
    ResponseVariableAssertion;

// 执行请求-断言配置
export interface ExecuteAssertionConfig {
    enableGlobal?: boolean; // 是否启用全局断言，部分地方没有
    assertions: ExecuteAssertionItem[];
}

// 执行请求-前后置操作-脚本处理器
export interface ScriptProcessorChild {
    polymorphicName: string; // 协议多态名称，写死MsCommonElement
    assertionConfig: ExecuteAssertionConfig;
}

export interface ScriptProcessor extends ScriptCommonConfig, ExecuteConditionProcessorCommon {
    children: ScriptProcessorChild[]; // 协议共有的子项配置
}

// 执行请求-前后置操作-SQL脚本处理器
export interface SQLProcessor extends ExecuteConditionProcessorCommon {
    name: string; // 描述
    dataSourceId: string; // 数据源ID
    dataSourceName: string; // 数据源名称
    queryTimeout: number; // 超时时间
    resultVariable: string; // 按结果存储时的结果变量
    script: string; // 脚本内容
    variableNames: string; // 按列存储时的变量名集合,多个列可以使用逗号分隔
    extractParams: KeyValueParam[]; // 提取参数列表
}

// 执行请求-前后置操作-等待时间处理器
export interface TimeWaitingProcessor extends ExecuteConditionProcessorCommon {
    delay: number; // 等待时间 单位：毫秒
}

// 表达式类型
export type ExpressionType = RequestExtractExpressionEnumType;

// 表达式配置
export interface ExpressionCommonConfig {
    id?: number | string; // 前端渲染使用字段
    enable: boolean; // 是否启用
    expression: string;
    extractType: ExpressionType; // 表达式类型
    variableName?: string;
    variableType?: RequestExtractEnvType;
    resultMatchingRule?: RequestExtractResultMatchingRule; // 结果匹配规则
    resultMatchingRuleNum?: number; // 匹配第几条结果
}

// 正则提取配置
export interface RegexExtract extends ExpressionCommonConfig {
    expressionMatchingRule?: RequestExtractExpressionRuleType; // 正则表达式匹配规则
    extractScope?: RequestExtractScope; // 正则提取范围
}

// JSONPath提取配置
export type JSONPathExtract = ExpressionCommonConfig;

// XPath提取配置
export interface XPathExtract extends ExpressionCommonConfig {
    responseFormat?: ResponseBodyXPathAssertionFormat; // 响应格式
}

// 执行请求-前后置操作-参数提取处理器
export interface ExtractProcessor extends ExecuteConditionProcessorCommon {
    extractors: (RegexExtract | JSONPathExtract | XPathExtract)[];
}

// 执行请求-前后置操作处理器
export interface ExecuteConditionProcessorCommon {
    id: string | number; // 处理器ID，前端列表渲染需要，后台无此字段
    enable: boolean; // 是否启用
    name?: string; // 条件处理器名称
    processorType: RequestConditionProcessor;
    associateScenarioResult?: boolean; // 是否关联场景结果
    ignoreProtocols: string[]; // 忽略协议
    beforeStepScript: boolean; // 是否是步骤内前置脚本前
    assertionType?: RequestConditionProcessor;
}

// 执行请求-前后置操作配置
export type ExecuteConditionProcessor =
    Partial<ScriptProcessor & SQLProcessor & TimeWaitingProcessor & ExtractProcessor>
    & ExecuteConditionProcessorCommon;

export interface ExecuteConditionConfig {
    enableGlobal?: boolean; // 是否启用全局前/后置 默认为 true
    processors: ExecuteConditionProcessor[];
    activeItemId?: number | string;
}

// 执行请求-共用配置子项
export interface ExecuteCommonChild {
    polymorphicName: string; // 协议多态名称，写死MsCommonElement
    assertionConfig: ExecuteAssertionConfig;
    postProcessorConfig: ExecuteConditionConfig; // 后置处理器配置
    preProcessorConfig: ExecuteConditionConfig; // 前置处理器配置
}

// 执行HTTP请求入参
export interface ExecuteApiRequestFullParams {
    authConfig: ExecuteAuthConfig;
    body: ExecuteBody;
    headers: EnableKeyValueParam[];
    method: RequestMethods | string;
    otherConfig: ExecuteOtherConfig;
    path: string;
    query: ExecuteRequestCommonParam[];
    rest: ExecuteRequestCommonParam[];
    url: string;
    polymorphicName: string; // 协议多态名称
    children: ExecuteCommonChild[]; // 协议共有的子项配置
    aiCreate?: boolean; // 是否AI创建
}
// 执行插件请求入参
export interface ExecutePluginRequestParams {
    polymorphicName: string; // 协议多态名称
    children: ExecuteCommonChild[]; // 协议共有的子项配置
    [key: string]: any; // key-value形式的插件参数
}
// 执行接口调试入参
export interface ExecuteRequestParams {
    id?: string;
    reportId?: string;
    environmentId: string;
    uploadFileIds: string[];
    linkFileIds: string[];
    request: ExecuteApiRequestFullParams | ExecutePluginRequestParams;
    projectId: string;
    frontendDebug?: boolean; // 是否本地调试，该模式下接口会返回执行参数，用来调用本地执行服务
    apiDefinitionId?: string | number; // 接口用例执行和调试时需要传
}