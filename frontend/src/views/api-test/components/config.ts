// 请求的默认 body 参数
import type {ExecuteBody, IRequestTaskResult, IResponseDefinition} from "/@/api/types/api.ts";
import {RequestBodyFormat, ResponseBodyFormat, ResponseComposition} from "/@/enums/api-enum.ts";
import {getGenerateId} from "/@/utils";

export const defaultBodyParams: ExecuteBody = {
    bodyType: RequestBodyFormat.NONE,
    formDataBody: {
        formValues: [],
    },
    wwwFormBody: {
        formValues: [],
    },
    jsonBody: {
        jsonValue: '',
        enableJsonSchema: true,
        jsonSchemaTableData: [],
        jsonSchemaTableSelectedRowKeys: [],
    },
    xmlBody: { value: '' },
    rawBody: { value: '' },
    binaryBody: {
        description: '',
        file: undefined,
        sendAsBody: false,
    },
};

// 默认的响应内容结构
export const defaultResponse: IRequestTaskResult = {
    requestResults: [
        {
            body: '',
            headers: '',
            method: '',
            url: '',
            responseResult: {
                body: '',
                contentType: '',
                headers: '',
                dnsLookupTime: 0,
                downloadTime: 0,
                latency: 0,
                responseCode: 0,
                responseTime: 0,
                responseSize: 0,
                socketInitTime: 0,
                tcpHandshakeTime: 0,
                transferStartTime: 0,
                sslHandshakeTime: 0,
                vars: '',
                extractResults: [],
                assertions: [],
            },
        },
    ],
    console: '',
};
export const defaultResponseItem: IResponseDefinition = {
    id: getGenerateId(),
    name: 'apiTestManagement.response',
    label: 'apiTestManagement.response',
    closable: false,
    statusCode: 200,
    defaultFlag: true,
    showPopConfirm: false,
    showRenamePopConfirm: false,
    responseActiveTab: ResponseComposition.BODY,
    headers: [],
    body: {
        bodyType: ResponseBodyFormat.JSON,
        jsonBody: {
            jsonValue: '',
            enableJsonSchema: true,
            enableTransition: false,
            jsonSchemaTableData: [],
            jsonSchemaTableSelectedRowKeys: [],
        },
        xmlBody: {
            value: '',
        },
        rawBody: {
            value: '',
        },
        binaryBody: {
            description: '',
            file: undefined,
            sendAsBody: false,
        },
    },
};