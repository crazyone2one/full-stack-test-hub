import {RouteEnum} from "/@/enums/route-enum.ts";

export const MENU_LEVEL = ['SYSTEM', 'ORGANIZATION', 'PROJECT'] as const; // 菜单级别
export type PathMapKey = keyof typeof RouteEnum;
export type PathMapRoute = (typeof RouteEnum)[PathMapKey];
export interface PathMapItem {
    key: PathMapKey | string; // 系统设置
    locale: string;
    route: PathMapRoute | string;
    permission?: [];
    level: (typeof MENU_LEVEL)[number]; // 系统设置里有系统级别也有组织级别，按最低权限级别配置
    children?: PathMapItem[];
    routeQuery?: Record<string, any>;
    hideInModule?: boolean; // 在日志选项中隐藏
}

export const pathMap:PathMapItem[]=[
    {
        key: 'SETTING', // 系统设置
        locale: 'menu.settings',
        route: RouteEnum.SETTING,
        permission: [],
        level: MENU_LEVEL[1], // 系统设置里有系统级别也有组织级别，按最低权限级别配置
        children: [
            {
                key: 'SETTING_SYSTEM', // 系统设置-系统
                locale: 'menu.settings.system',
                route: RouteEnum.SETTING_SYSTEM,
                permission: [],
                level: MENU_LEVEL[0],
                children: [
                    {
                        key: 'SETTING_SYSTEM_USER_SINGLE', // 系统设置-系统-用户
                        locale: 'menu.settings.system.user',
                        route: RouteEnum.SETTING_SYSTEM_USER_SINGLE,
                        permission: [],
                        level: MENU_LEVEL[0],
                    },
                    {
                        key: 'SETTING_SYSTEM_USER_GROUP', // 系统设置-系统-用户组
                        locale: 'menu.settings.system.usergroup',
                        route: RouteEnum.SETTING_SYSTEM_USER_GROUP,
                        permission: [],
                        level: MENU_LEVEL[0],
                    },
                    {
                        key: 'SETTING_SYSTEM_ORGANIZATION', // 系统设置-系统-组织与项目
                        locale: 'menu.settings.system.organizationAndProject',
                        route: RouteEnum.SETTING_SYSTEM_ORGANIZATION,
                        permission: [],
                        level: MENU_LEVEL[0],
                    },
                    {
                        key: 'SETTING_SYSTEM_PARAMETER', // 系统设置-系统-系统参数
                        locale: 'menu.settings.system.parameter',
                        route: RouteEnum.SETTING_SYSTEM_PARAMETER,
                        permission: [],
                        level: MENU_LEVEL[0],
                        children: [
                            {
                                key: 'SETTING_SYSTEM_PARAMETER_BASE_CONFIG', // 系统设置-系统-系统参数-基础设置
                                locale: 'system.config.baseConfig',
                                route: RouteEnum.SETTING_SYSTEM_PARAMETER,
                                permission: [],
                                level: MENU_LEVEL[0],
                            },
                            {
                                key: 'SETTING_SYSTEM_PARAMETER_PAGE_CONFIG', // 系统设置-系统-系统参数-界面设置
                                locale: 'system.config.pageConfig',
                                route: RouteEnum.SETTING_SYSTEM_PARAMETER,
                                permission: [],
                                routeQuery: {
                                    tab: 'pageConfig',
                                },
                                level: MENU_LEVEL[0],
                            },
                            {
                                key: 'SETTING_SYSTEM_PARAMETER_AUTH_CONFIG', // 系统设置-系统-系统参数-认证设置
                                locale: 'system.config.authConfig',
                                route: RouteEnum.SETTING_SYSTEM_PARAMETER,
                                permission: [],
                                routeQuery: {
                                    tab: 'authConfig',
                                },
                                level: MENU_LEVEL[0],
                            },
                        ],
                    },
                    {
                        key: 'SETTING_SYSTEM_RESOURCE_POOL', // 系统设置-系统-资源池
                        locale: 'menu.settings.system.resourcePool',
                        route: RouteEnum.SETTING_SYSTEM_RESOURCE_POOL,
                        permission: [],
                        level: MENU_LEVEL[0],
                    },
                    {
                        key: 'SETTING_SYSTEM_AUTHORIZED_MANAGEMENT', // 系统设置-系统-授权管理
                        locale: 'License',
                        route: RouteEnum.SETTING_SYSTEM_AUTHORIZED_MANAGEMENT,
                        permission: [],
                        level: MENU_LEVEL[0],
                    },
                    {
                        key: 'SETTING_SYSTEM_LOG', // 系统设置-系统-日志
                        locale: 'menu.settings.system.log',
                        route: RouteEnum.SETTING_SYSTEM_LOG,
                        permission: [],
                        level: MENU_LEVEL[0],
                    },
                    {
                        key: 'SETTING_SYSTEM_TASK_CENTER', // 系统设置-系统-任务中心
                        locale: 'menu.projectManagement.taskCenter',
                        route: RouteEnum.SETTING_SYSTEM_TASK_CENTER,
                        permission: [],
                        level: MENU_LEVEL[0],
                    },
                    {
                        key: 'SETTING_SYSTEM_PLUGIN_MANAGEMENT', // 系统设置-系统-插件管理
                        locale: 'menu.settings.system.pluginManager',
                        route: RouteEnum.SETTING_SYSTEM_PLUGIN_MANAGEMENT,
                        permission: [],
                        level: MENU_LEVEL[0],
                    },
                ],
            },
            {
                key: 'SETTING_ORGANIZATION', // 系统设置-组织
                locale: 'menu.settings.organization',
                route: RouteEnum.SETTING_ORGANIZATION,
                permission: [],
                level: MENU_LEVEL[1],
                children: [
                    {
                        key: 'SETTING_ORGANIZATION_MEMBER', // 系统设置-组织-成员
                        locale: 'menu.settings.organization.member',
                        route: RouteEnum.SETTING_ORGANIZATION_MEMBER,
                        permission: [],
                        level: MENU_LEVEL[1],
                    },
                    {
                        key: 'SETTING_ORGANIZATION_USER_ROLE', // 系统设置-组织-用户组
                        locale: 'menu.settings.organization.userGroup',
                        route: RouteEnum.SETTING_ORGANIZATION_USER_GROUP,
                        permission: [],
                        level: MENU_LEVEL[1],
                    },
                    {
                        key: 'SETTING_ORGANIZATION_PROJECT', // 系统设置-组织-项目
                        locale: 'menu.settings.organization.project',
                        route: RouteEnum.SETTING_ORGANIZATION_PROJECT,
                        permission: [],
                        level: MENU_LEVEL[1],
                    },
                    {
                        key: 'SETTING_ORGANIZATION_SERVICE', // 系统设置-组织-服务集成
                        locale: 'menu.settings.organization.serviceIntegration',
                        route: RouteEnum.SETTING_ORGANIZATION_SERVICE,
                        permission: [],
                        level: MENU_LEVEL[1],
                    },
                    {
                        key: 'SETTING_ORGANIZATION_TASK_CENTER', // 系统设置-组织-任务中心
                        locale: 'menu.projectManagement.taskCenter',
                        route: RouteEnum.SETTING_ORGANIZATION_TASK_CENTER,
                        permission: [],
                        level: MENU_LEVEL[1],
                    },
                    {
                        key: 'SETTING_ORGANIZATION_TEMPLATE', // 系统设置-组织-模板管理
                        locale: 'menu.projectManagement.templateManager',
                        route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE,
                        permission: [],
                        level: MENU_LEVEL[1],
                        children: [
                            {
                                key: 'SETTING_ORGANIZATION_TEMPLATE_FUNCTIONAL', // 模板管理-用例模板
                                locale: 'system.orgTemplate.caseTemplates',
                                route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT,
                                permission: [],
                                routeQuery: {
                                    type: 'FUNCTIONAL',
                                },
                                level: MENU_LEVEL[1],
                                children: [
                                    {
                                        key: 'SETTING_ORGANIZATION_TEMPLATE_FUNCTIONAL_FIELD', // 模板管理-用例模板-用例模板字段管理
                                        locale: 'system.orgTemplate.field',
                                        route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE_FILED_SETTING,
                                        permission: [],
                                        routeQuery: {
                                            type: 'FUNCTIONAL',
                                        },
                                        level: MENU_LEVEL[1],
                                    },
                                    {
                                        key: 'SETTING_ORGANIZATION_TEMPLATE_FUNCTIONAL_TEMPLATE', // 模板管理-用例模板-用例模板管理
                                        locale: 'system.orgTemplate.caseTemplateManagement',
                                        route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT,
                                        permission: [],
                                        routeQuery: {
                                            type: 'FUNCTIONAL',
                                        },
                                        level: MENU_LEVEL[1],
                                    },
                                ],
                            },
                            {
                                key: 'SETTING_ORGANIZATION_TEMPLATE_API', // 模板管理-接口模板
                                locale: 'system.orgTemplate.APITemplates',
                                route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT,
                                permission: [],
                                routeQuery: {
                                    type: 'API',
                                },
                                level: MENU_LEVEL[1],
                                children: [
                                    {
                                        key: 'SETTING_ORGANIZATION_TEMPLATE_API_FIELD', // 模板管理-接口模板-接口模板字段管理
                                        locale: 'system.orgTemplate.field',
                                        route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE_FILED_SETTING,
                                        permission: [],
                                        routeQuery: {
                                            type: 'API',
                                        },
                                        level: MENU_LEVEL[1],
                                    },
                                    {
                                        key: 'SETTING_ORGANIZATION_TEMPLATE_API_TEMPLATE', // 模板管理-接口模板-接口模板管理
                                        locale: 'system.orgTemplate.apiTemplateManagement',
                                        route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT,
                                        permission: [],
                                        routeQuery: {
                                            type: 'API',
                                        },
                                        level: MENU_LEVEL[1],
                                    },
                                ],
                            },
                            {
                                key: 'SETTING_ORGANIZATION_TEMPLATE_BUG', // 模板管理-缺陷模板
                                locale: 'system.orgTemplate.defectTemplates',
                                route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT,
                                permission: [],
                                routeQuery: {
                                    type: 'BUG',
                                },
                                level: MENU_LEVEL[1],
                                children: [
                                    {
                                        key: 'SETTING_ORGANIZATION_TEMPLATE_BUG_FIELD', // 模板管理-缺陷模板管理-字段管理
                                        locale: 'system.orgTemplate.field',
                                        route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE_FILED_SETTING,
                                        permission: [],
                                        routeQuery: {
                                            type: 'BUG',
                                        },
                                        level: MENU_LEVEL[1],
                                    },
                                    {
                                        key: 'SETTING_ORGANIZATION_TEMPLATE_BUG_TEMPLATE', // 模板管理-缺陷模板-缺陷模板管理
                                        locale: 'system.orgTemplate.bugTemplateManagement',
                                        route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT,
                                        permission: [],
                                        routeQuery: {
                                            type: 'BUG',
                                        },
                                        level: MENU_LEVEL[1],
                                    },
                                    {
                                        key: 'SETTING_ORGANIZATION_TEMPLATE_BUG_WORKFLOW', // 模板管理-缺陷模板-缺陷工作流
                                        locale: 'menu.settings.organization.templateManagementWorkFlow',
                                        route: RouteEnum.SETTING_ORGANIZATION_TEMPLATE_MANAGEMENT_WORKFLOW,
                                        permission: [],
                                        level: MENU_LEVEL[1],
                                    },
                                ],
                            },
                        ],
                    },
                    {
                        key: 'SETTING_ORGANIZATION_LOG', // 系统设置-组织-日志
                        locale: 'menu.settings.organization.log',
                        route: RouteEnum.SETTING_ORGANIZATION_LOG,
                        permission: [],
                        level: MENU_LEVEL[1],
                        hideInModule: true,
                    },
                ],
            },
        ],
    },
    {
        key: 'PROJECT_MANAGEMENT', // 项目管理
        locale: 'menu.projectManagement',
        route: RouteEnum.PROJECT_MANAGEMENT,
        permission: [],
        level: MENU_LEVEL[2],
        children: [
            {
                key: 'PROJECT_MANAGEMENT_PERMISSION', // 项目管理-项目与权限
                locale: 'menu.projectManagement.projectPermission',
                route: RouteEnum.PROJECT_MANAGEMENT_PERMISSION,
                permission: [],
                level: MENU_LEVEL[2],
                children: [
                    {
                        key: 'PROJECT_MANAGEMENT_PERMISSION_BASIC_INFO', // 项目管理-项目与权限-基本信息
                        locale: 'project.permission.basicInfo',
                        route: RouteEnum.PROJECT_MANAGEMENT_PERMISSION_BASIC_INFO,
                        permission: [],
                        level: MENU_LEVEL[2],
                    },
                    {
                        key: 'PROJECT_MANAGEMENT_PERMISSION_MENU_MANAGEMENT', // 项目管理-项目与权限-菜单管理
                        locale: 'project.permission.menuManagement',
                        route: RouteEnum.PROJECT_MANAGEMENT_PERMISSION_MENU_MANAGEMENT,
                        permission: [],
                        level: MENU_LEVEL[2],
                        children: [
                            {
                                key: 'PROJECT_MANAGEMENT_PERMISSION_MENU_MANAGEMENT_FAKE', // 项目管理-项目与权限-菜单管理-误报
                                locale: 'menu.projectManagement.fakeError',
                                route: RouteEnum.PROJECT_MANAGEMENT_MENU_MANAGEMENT_ERROR_REPORT_RULE,
                                permission: [],
                                level: MENU_LEVEL[2],
                            },
                        ],
                    },
                    {
                        key: 'PROJECT_MANAGEMENT_PERMISSION_VERSION', // 项目管理-项目与权限-项目版本
                        locale: 'project.permission.projectVersion',
                        route: RouteEnum.PROJECT_MANAGEMENT_PERMISSION_VERSION,
                        permission: [],
                        level: MENU_LEVEL[2],
                    },
                    {
                        key: 'PROJECT_MANAGEMENT_PERMISSION_MEMBER', // 项目管理-项目与权限-成员
                        locale: 'project.permission.member',
                        route: RouteEnum.PROJECT_MANAGEMENT_PERMISSION_MEMBER,
                        permission: [],
                        level: MENU_LEVEL[2],
                    },
                    {
                        key: 'PROJECT_MANAGEMENT_PERMISSION_USER_GROUP', // 项目管理-项目与权限-用户组
                        locale: 'project.permission.userGroup',
                        route: RouteEnum.PROJECT_MANAGEMENT_PERMISSION_USER_GROUP,
                        permission: [],
                        level: MENU_LEVEL[2],
                    },
                ],
            },
            {
                key: 'PROJECT_MANAGEMENT_TEMPLATE', // 系统设置-组织-模板管理
                locale: 'menu.projectManagement.templateManager',
                route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE,
                permission: [],
                level: MENU_LEVEL[2],
                children: [
                    {
                        key: 'PROJECT_MANAGEMENT_TEMPLATE_FUNCTIONAL', // 模板管理-用例模板
                        locale: 'system.orgTemplate.caseTemplates',
                        route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT,
                        permission: [],
                        routeQuery: {
                            type: 'FUNCTIONAL',
                        },
                        level: MENU_LEVEL[2],
                        children: [
                            {
                                key: 'PROJECT_MANAGEMENT_TEMPLATE_FUNCTIONAL_FIELD', // 模板管理-用例模板-用例模板字段管理
                                locale: 'system.orgTemplate.field',
                                route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE_FIELD_SETTING,
                                permission: [],
                                routeQuery: {
                                    type: 'FUNCTIONAL',
                                },
                                level: MENU_LEVEL[2],
                            },
                            {
                                key: 'PROJECT_MANAGEMENT_TEMPLATE_FUNCTIONAL_TEMPLATE', // 模板管理-用例模板-用例模板管理
                                locale: 'system.orgTemplate.caseTemplateManagement',
                                route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT,
                                permission: [],
                                routeQuery: {
                                    type: 'FUNCTIONAL',
                                },
                                level: MENU_LEVEL[2],
                            },
                        ],
                    },
                    {
                        key: 'PROJECT_MANAGEMENT_TEMPLATE_API', // 模板管理-接口模板
                        locale: 'system.orgTemplate.APITemplates',
                        route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT,
                        permission: [],
                        routeQuery: {
                            type: 'API',
                        },
                        level: MENU_LEVEL[2],
                        children: [
                            {
                                key: 'PROJECT_MANAGEMENT_TEMPLATE_API_FIELD', // 模板管理-接口模板-接口模板字段管理
                                locale: 'system.orgTemplate.field',
                                route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE_FIELD_SETTING,
                                permission: [],
                                routeQuery: {
                                    type: 'API',
                                },
                                level: MENU_LEVEL[2],
                            },
                            {
                                key: 'PROJECT_MANAGEMENT_TEMPLATE_API_TEMPLATE', // 模板管理-接口模板-接口模板管理
                                locale: 'system.orgTemplate.apiTemplateManagement',
                                route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT,
                                permission: [],
                                routeQuery: {
                                    type: 'API',
                                },
                                level: MENU_LEVEL[2],
                            },
                        ],
                    },
                    {
                        key: 'PROJECT_MANAGEMENT_TEMPLATE_BUG', // 模板管理-缺陷模板
                        locale: 'system.orgTemplate.defectTemplates',
                        route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT,
                        permission: [],
                        routeQuery: {
                            type: 'BUG',
                        },
                        level: MENU_LEVEL[2],
                        children: [
                            {
                                key: 'PROJECT_MANAGEMENT_TEMPLATE_BUG_FIELD', // 模板管理-缺陷模板管理-字段管理
                                locale: 'system.orgTemplate.field',
                                route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE_FIELD_SETTING,
                                permission: [],
                                routeQuery: {
                                    type: 'BUG',
                                },
                                level: MENU_LEVEL[2],
                            },
                            {
                                key: 'PROJECT_MANAGEMENT_TEMPLATE_BUG_TEMPLATE', // 模板管理-缺陷模板-缺陷模板管理
                                locale: 'system.orgTemplate.bugTemplateManagement',
                                route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT,
                                permission: [],
                                routeQuery: {
                                    type: 'BUG',
                                },
                                level: MENU_LEVEL[2],
                            },
                            {
                                key: 'PROJECT_MANAGEMENT_TEMPLATE_BUG_WORKFLOW', // 模板管理-缺陷模板-缺陷工作流
                                locale: 'menu.settings.organization.templateManagementWorkFlow',
                                route: RouteEnum.PROJECT_MANAGEMENT_TEMPLATE_MANAGEMENT_WORKFLOW,
                                permission: [],
                                level: MENU_LEVEL[2],
                            },
                        ],
                    },
                ],
            },

            {
                key: 'PROJECT_MANAGEMENT_FILE_MANAGEMENT', // 项目管理-文件管理
                locale: 'menu.projectManagement.fileManagement',
                route: RouteEnum.PROJECT_MANAGEMENT_FILE_MANAGEMENT,
                permission: [],
                level: MENU_LEVEL[2],
            },
            {
                key: 'PROJECT_MANAGEMENT_MESSAGE_MANAGEMENT', // 项目管理-消息管理
                locale: 'menu.projectManagement.messageManagement',
                route: RouteEnum.PROJECT_MANAGEMENT_MESSAGE_MANAGEMENT,
                permission: [],
                level: MENU_LEVEL[2],
                children: [
                    {
                        key: 'PROJECT_MANAGEMENT_MESSAGE_MANAGEMENT_CONFIG', // 项目管理-消息管理-消息设置
                        locale: 'project.messageManagement.config',
                        route: RouteEnum.PROJECT_MANAGEMENT_MESSAGE_MANAGEMENT,
                        permission: [],
                        level: MENU_LEVEL[2],
                    },
                    {
                        key: 'PROJECT_MANAGEMENT_MESSAGE_MANAGEMENT_ROBOT', // 项目管理-消息管理-机器人列表
                        locale: 'project.messageManagement.botList',
                        route: RouteEnum.PROJECT_MANAGEMENT_MESSAGE_MANAGEMENT,
                        permission: [],
                        routeQuery: {
                            tab: 'botList',
                        },
                        level: MENU_LEVEL[2],
                    },
                ],
            },
            {
                key: 'PROJECT_MANAGEMENT_MESSAGE_MANAGEMENT_EDIT', // 项目管理-消息管理-编辑
                locale: 'menu.projectManagement.messageManagementEdit',
                route: RouteEnum.PROJECT_MANAGEMENT_MESSAGE_MANAGEMENT_EDIT,
                permission: [],
                level: MENU_LEVEL[2],
            },
            {
                key: 'PROJECT_MANAGEMENT_COMMON_SCRIPT', // 项目管理-公共脚本
                locale: 'menu.projectManagement.commonScript',
                route: RouteEnum.PROJECT_MANAGEMENT_COMMON_SCRIPT,
                permission: [],
                level: MENU_LEVEL[2],
            },
            {
                key: 'PROJECT_MANAGEMENT_LOG', // 项目管理-日志
                locale: 'menu.projectManagement.log',
                route: RouteEnum.PROJECT_MANAGEMENT_LOG,
                permission: [],
                level: MENU_LEVEL[2],
            },
            {
                key: 'PROJECT_MANAGEMENT_ENVIRONMENT', // 项目管理-环境管理
                locale: 'menu.projectManagement.environmentManagement',
                route: RouteEnum.PROJECT_MANAGEMENT_ENVIRONMENT_MANAGEMENT,
                permission: [],
                level: MENU_LEVEL[2],
            },
            {
                key: 'PROJECT_MANAGEMENT_TASK_CENTER', // 项目管理-任务中心
                locale: 'menu.projectManagement.taskCenter',
                route: '',
                permission: [],
                level: MENU_LEVEL[2],
            },
        ],
    },
]