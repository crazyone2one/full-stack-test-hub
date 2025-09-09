export const WHITE_LIST = [
    {name: 'notFound', path: '/notFound', children: []},
]
export const WHITE_LIST_NAME = WHITE_LIST.map((el) => el.name);
export const featureRouteMap: Record<string, any> = {
    ["apiTest"]: 'apiTest',
    ["CASE_MANAGEMENT"]: 'caseManagement',
    ["TEST_PLAN"]: 'testPlan',
    ["BUG_MANAGEMENT"]: 'bugManagement',
};
export const NO_RESOURCE_ROUTE_NAME = 'no-resource';
export const NO_PROJECT_ROUTE_NAME = 'no-project';