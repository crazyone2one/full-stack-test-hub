import {pathMap, type PathMapItem, type PathMapRoute} from "/@/utils/path-map.ts";
import {findNodeByKey} from "/@/utils";

export default function usePathMap() {
    const getRouteLevelByKey = (name: PathMapRoute) => {
        const pathNode = findNodeByKey<PathMapItem>(pathMap, name, 'route');
        if (pathNode) {
            return pathNode.level;
        }
        return null;
    };
    return {
        getRouteLevelByKey
    };
}