export interface TreeNode<T> {
    children?: TreeNode<T>[];

    [key: string]: any;
}

export function findNodeByKey<T>(
    trees: TreeNode<T>[],
    targetKey: string | number,
    customKey = 'key',
    dataKey: string | undefined = undefined
): TreeNode<T> | T | null {
    for (let i = 0; i < trees.length; i++) {
        const node = trees[i];
        if (dataKey ? node[dataKey]?.[customKey] === targetKey : node[customKey] === targetKey) {
            return node; // 如果当前节点的 key 与目标 key 匹配，则返回当前节点
        }

        if (Array.isArray(node.children) && node.children.length > 0) {
            const _node = findNodeByKey(node.children, targetKey, customKey, dataKey); // 递归在子节点中查找
            if (_node) {
                return _node; // 如果在子节点中找到了匹配的节点，则返回该节点
            }
        }
    }

    return null; // 如果在整个树形数组中都没有找到匹配的节点，则返回 null
}

export function characterLimit(str?: string, length?: number): string {
    if (!str) return '';
    if (str.length <= (length || 20)) {
        return str;
    }
    return `${str.slice(0, length || 20 - 3)}...`;
}

export const formatPhoneNumber = (phoneNumber = '') => {
    if (phoneNumber && phoneNumber.trim().length === 11) {
        const cleanedNumber = phoneNumber.replace(/\D/g, '');
        return cleanedNumber.replace(/(\d{3})(\d{4})(\d{4})/, '$1 $2 $3');
    }
    return phoneNumber;
}