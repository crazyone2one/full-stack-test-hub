export const TaskCenterEnum = {
    CASE: 'CASE',
    DETAIL: 'DETAIL',
    BACKEND: 'BACKEND',
    API_IMPORT: 'API_IMPORT',
} as const
export const ExecuteStatusEnum = {
    PENDING: 'PENDING',
    RUNNING: 'RUNNING',
    COMPLETED: 'COMPLETED',
    RERUNNING: 'RERUNNING',
    STOPPED: 'STOPPED',
} as const
export const ExecuteResultEnum = {
    SUCCESS: 'SUCCESS',
    ERROR: 'ERROR',
    FAKE_ERROR: 'FAKE_ERROR',
} as const
export const ExecuteTriggerMode = {
    MANUAL: 'MANUAL',
    BATCH: 'BATCH',
    API: 'API',
    SCHEDULE: 'SCHEDULE',
} as const
export const ExecuteTaskType = {
    API_CASE: 'API_CASE',
    API_CASE_BATCH: 'API_CASE_BATCH',
    API_SCENARIO: 'API_SCENARIO',
    API_SCENARIO_BATCH: 'API_SCENARIO_BATCH',
    TEST_PLAN_API_CASE: 'TEST_PLAN_API_CASE',
    TEST_PLAN_API_CASE_BATCH: 'TEST_PLAN_API_CASE_BATCH',
    TEST_PLAN_API_SCENARIO: 'TEST_PLAN_API_SCENARIO',
    TEST_PLAN_API_SCENARIO_BATCH: 'TEST_PLAN_API_SCENARIO_BATCH',
    TEST_PLAN: 'TEST_PLAN',
    TEST_PLAN_GROUP: 'TEST_PLAN_GROUP',
} as const
export type TaskCenterEnumType = typeof TaskCenterEnum[keyof typeof TaskCenterEnum];
export type ExecuteStatusEnumType = typeof ExecuteStatusEnum[keyof typeof ExecuteStatusEnum];
export type ExecuteResultEnumType = typeof ExecuteResultEnum[keyof typeof ExecuteResultEnum];
export type ExecuteTriggerModeType = typeof ExecuteTriggerMode[keyof typeof ExecuteTriggerMode];
export type ExecuteTaskTypeType = typeof ExecuteTaskType[keyof typeof ExecuteTaskType];