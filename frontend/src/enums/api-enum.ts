export const RequestComposition = {
    BASE_INFO: 'BASE_INFO',
    PLUGIN: 'PLUGIN',
    HEADER: 'HEADER',
    BODY: 'BODY',
    QUERY: 'QUERY',
    REST: 'REST',
    PRECONDITION: 'PRECONDITION',
    POST_CONDITION: 'POST_CONDITION',
    ASSERTION: 'ASSERTION',
    AUTH: 'AUTH',
    SETTING: 'SETTING',
} as const
export const RequestMethods = {
    GET: 'GET',
    POST: 'POST',
    PUT: 'PUT',
    DELETE: 'DELETE',
    PATCH: 'PATCH',
    OPTIONS: 'OPTIONS',
    HEAD: 'HEAD',
    CONNECT: 'CONNECT',
} as const
export const ScenarioExecuteStatus = {
    SUCCESS: 'SUCCESS',
    EXECUTING: 'EXECUTING',
    FAILED: 'FAILED',
    STOP: 'STOP',
    UN_EXECUTE: 'UN_EXECUTE',
    FAKE_ERROR: 'FAKE_ERROR',
} as const
export const FullResponseAssertionTypeEnum = {
    DOCUMENT: 'DOCUMENT',
    RESPONSE_CODE: 'RESPONSE_CODE',
    RESPONSE_HEADER: 'RESPONSE_HEADER',
    RESPONSE_TIME: 'RESPONSE_TIME',
    SCRIPT: 'SCRIPT',
    VARIABLE: 'VARIABLE',
    JSON_PATH: 'JSON_PATH',
    XPATH: 'XPATH',
} as const
export const ResponseAssertionTypeEnum = {
    RESPONSE_CODE: 'RESPONSE_CODE',
    RESPONSE_HEADER: 'RESPONSE_HEADER',
    RESPONSE_TIME: 'RESPONSE_TIME',
    SCRIPT: 'SCRIPT',
    VARIABLE: 'VARIABLE',
    RESPONSE_BODY: 'RESPONSE_BODY',
} as const
export const RequestAssertionCondition = {
    CONTAINS: 'CONTAINS',
    EMPTY: 'EMPTY',
    END_WITH: 'END_WITH',
    EQUALS: 'EQUALS',
    GT: 'GT',
    GT_OR_EQUALS: 'GT_OR_EQUALS',
    LENGTH_EQUALS: 'LENGTH_EQUALS',
    LENGTH_GT: 'LENGTH_GT',
    LENGTH_GT_OR_EQUALS: 'LENGTH_GT_OR_EQUALS',
    LENGTH_LT: 'LENGTH_LT',
    LENGTH_LT_OR_EQUALS: 'LENGTH_LT_OR_EQUALS',
    LT: 'LT',
    LT_OR_EQUALS: 'LT_OR_EQUALS',
    NOT_CONTAINS: 'NOT_CONTAINS',
    NOT_EMPTY: 'NOT_EMPTY',
    START_WITH: 'START_WITH',
} as const
export const RequestExtractEnvTypeEnum = {
    ENVIRONMENT: 'ENVIRONMENT',
    TEMPORARY: 'TEMPORARY',
    GLOBAL: 'GLOBAL',
} as const
export const ResponseBodyFormat = {
    NONE: 'NONE',
    JSON: 'JSON',
    XML: 'XML',
    RAW: 'RAW',
    BINARY: 'BINARY',
} as const
export const RequestBodyFormat = {
    NONE: 'NONE',
    JSON: 'JSON',
    XML: 'XML',
    RAW: 'RAW',
    BINARY: 'BINARY',
    FORM_DATA: 'FORM_DATA',
    WWW_FORM: 'WWW_FORM',
} as const
export const ResponseComposition = {
    BODY: 'BODY',
    HEADER: 'HEADER',
    REAL_REQUEST: 'REAL_REQUEST',
    CONSOLE: 'CONSOLE',
    EXTRACT: 'EXTRACT',
    CODE: 'CODE',
    ASSERTION: 'ASSERTION',
} as const
export const RequestAuthType = {
    BASIC: 'BASIC',
    DIGEST: 'DIGEST',
    NONE: 'NONE',
} as const
export const ResponseBodyAssertionTypeEnum = {
    DOCUMENT: 'DOCUMENT',
    JSON_PATH: 'JSON_PATH',
    REGEX: 'REGEX',
    XPATH: 'XPATH',
    SCRIPT: 'SCRIPT',
} as const
export const RequestParamsType = {
    STRING: 'string',
    NUMBER: 'number',
    JSON: 'json',
    FILE: 'file',
} as const
export const RequestContentTypeEnum = {
    JSON: 'application/json',
    TEXT: 'application/text',
    JAVASCRIPT: 'application/javascript',
    OCTET_STREAM: 'application/octet-stream',
} as const
export const ResponseBodyXPathAssertionFormatEnum = {
    HTML: 'HTML',
    XML: 'XML',
} as const
export const ResponseBodyAssertionDocumentTypeEnum = {
    JSON: 'JSON',
    XML: 'XML',
} as const
export const ResponseBodyDocumentAssertionTypeEnum = {
    NUMBER: 'NUMBER',
    ARRAY: 'ARRAY',
} as const
export const RequestExtractExpressionEnum = {
    REGEX: 'REGEX',
    JSON_PATH: 'JSON_PATH',
    X_PATH: 'X_PATH',
} as const
export const RequestConditionProcessorEnum = {
    SCRIPT: 'SCRIPT',
    SQL: 'SQL',
    TIME_WAITING: 'TIME_WAITING',
    EXTRACT: 'EXTRACT',
    SCENARIO_SCRIPT: 'SCENARIO_SCRIPT',
    REQUEST_SCRIPT: 'REQUEST_SCRIPT',
} as const
export const LanguageEnum = {
    PLAINTEXT: 'PLAINTEXT' as const,
    JAVASCRIPT: 'JAVASCRIPT' as const,
    TYPESCRIPT: 'TYPESCRIPT' as const,
    CSS: 'CSS' as const,
    LESS: 'LESS' as const,
    SASS: 'SASS' as const,
    HTML: 'HTML' as const,
    SQL: 'SQL' as const,
    JSON: 'JSON' as const,
    JAVA: 'JAVA' as const,
    PYTHON: 'PYTHON' as const,
    XML: 'XML' as const,
    YAML: 'YAML' as const,
    SHELL: 'SHELL' as const,
    BEANSHELL: 'BEANSHELL' as const,
    BEANSHELL_JSR233: 'BEANSHELL_JSR233' as const,
    GROOVY: 'GROOVY' as const,
    NASHORNSCRIPT: 'NASHORNSCRIPT' as const,
    RHINOSCRIPT: 'RHINOSCRIPT' as const,
} as const;
export const RequestExtractResultMatchingRuleEnum = {
    ALL: 'ALL',
    RANDOM: 'RANDOM',
    SPECIFIC: 'SPECIFIC',
} as const
export const RequestExtractExpressionRuleTypeEnum = {
    EXPRESSION: 'EXPRESSION',
    GROUP: 'GROUP',
} as const
export const RequestExtractScopeEnum = {
    BODY: 'BODY',
    BODY_AS_DOCUMENT: 'BODY_AS_DOCUMENT',
    UNESCAPED_BODY: 'UNESCAPED_BODY',
    REQUEST_HEADERS: 'REQUEST_HEADERS',
    RESPONSE_CODE: 'RESPONSE_CODE',
    RESPONSE_HEADERS: 'RESPONSE_HEADERS',
    RESPONSE_MESSAGE: 'RESPONSE_MESSAGE',
    URL: 'URL',
} as const
export type Language = (typeof LanguageEnum)[keyof typeof LanguageEnum];
export type RequestExtractScope = (typeof RequestExtractScopeEnum)[keyof typeof RequestExtractScopeEnum];
export type RequestExtractExpressionRuleType = (typeof RequestExtractExpressionRuleTypeEnum)[keyof typeof RequestExtractExpressionRuleTypeEnum];
export type RequestExtractResultMatchingRule = (typeof RequestExtractResultMatchingRuleEnum)[keyof typeof RequestExtractResultMatchingRuleEnum];
export type RequestConditionProcessor = (typeof RequestConditionProcessorEnum)[keyof typeof RequestConditionProcessorEnum];
export type RequestExtractExpressionEnumType = typeof RequestExtractExpressionEnum[keyof typeof RequestExtractExpressionEnum];
export type ResponseBodyDocumentAssertionType = typeof ResponseBodyDocumentAssertionTypeEnum[keyof typeof ResponseBodyDocumentAssertionTypeEnum];
export type ResponseBodyAssertionDocumentType = typeof ResponseBodyAssertionDocumentTypeEnum[keyof typeof ResponseBodyAssertionDocumentTypeEnum];
export type ResponseBodyAssertionType = typeof ResponseBodyAssertionTypeEnum[keyof typeof ResponseBodyAssertionTypeEnum];
export type ResponseBodyXPathAssertionFormat = typeof ResponseBodyXPathAssertionFormatEnum[keyof typeof ResponseBodyXPathAssertionFormatEnum];
export type RequestContentTypeEnumType = typeof RequestContentTypeEnum[keyof typeof RequestContentTypeEnum];
export type RequestParamsTypeType = typeof RequestParamsType[keyof typeof RequestParamsType];
export type RequestBodyFormatType = typeof RequestBodyFormat[keyof typeof RequestBodyFormat];
export type RequestAuthTypeType = typeof RequestAuthType[keyof typeof RequestAuthType];
export type ResponseBodyFormatType = typeof ResponseBodyFormat[keyof typeof ResponseBodyFormat];
export type ResponseCompositionType = typeof ResponseComposition[keyof typeof ResponseComposition];
export type RequestAssertionConditionType = typeof RequestAssertionCondition[keyof typeof RequestAssertionCondition];
export type RequestExtractEnvType = typeof RequestExtractEnvTypeEnum[keyof typeof RequestExtractEnvTypeEnum];
export type FullResponseAssertionType = typeof FullResponseAssertionTypeEnum[keyof typeof FullResponseAssertionTypeEnum];
export type ResponseAssertionType = typeof ResponseAssertionTypeEnum[keyof typeof ResponseAssertionTypeEnum];
export type ScenarioExecuteStatusType = typeof ScenarioExecuteStatus[keyof typeof ScenarioExecuteStatus];
export type RequestMethodsType = typeof RequestMethods[keyof typeof RequestMethods];
export type RequestCompositionType = typeof RequestComposition[keyof typeof RequestComposition];