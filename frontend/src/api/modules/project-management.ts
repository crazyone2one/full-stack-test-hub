import {get} from "/@/api";
import type {ProjectListItem} from "/@/api/types/project.ts";

export const projectManagementApis = {
    fetchProjectList: (organizationId: string) => get<Array<ProjectListItem>>(`/project/list/options/${organizationId}`)
}