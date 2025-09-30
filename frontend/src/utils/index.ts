import {cloneDeep} from "es-toolkit";

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
let lastTimestamp = 0;
let sequence = 0;
export const getGenerateId = () => {
    let timestamp = new Date().getTime();
    if (timestamp === lastTimestamp) {
        sequence++;
        if (sequence >= 100000) {
            // 如果超过999，则重置为0，等待下一秒
            sequence = 0;
            while (timestamp <= lastTimestamp) {
                timestamp = new Date().getTime();
            }
        }
    } else {
        sequence = 0;
    }

    lastTimestamp = timestamp;

    return timestamp.toString() + sequence.toString().padStart(5, '0');
};

export const addCommasToNumber = (number: number) => {
    if (number === 0 || number === undefined) {
        return '0';
    }
    // 将数字转换为字符串
    const numberStr = number.toString();

    // 分割整数部分和小数部分
    const parts = numberStr.split('.');
    const integerPart = parts[0];
    const decimalPart = parts[1] || ''; // 如果没有小数部分，则设为空字符串

    // 对整数部分添加逗号分隔
    const integerWithCommas = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',');

    // 拼接整数部分和小数部分（如果有）
    return decimalPart ? `${integerWithCommas}.${decimalPart}` : integerWithCommas;
}

export function mapTree<T>(
    tree: TreeNode<T> | TreeNode<T>[] | T | T[],
    customNodeFn: (node: TreeNode<T>, path: string, _level: number) => TreeNode<T> | null = (node) => node,
    customChildrenKey = 'children',
    parentPath = '',
    level = 0,
    parent: TreeNode<T> | null = null
): T[] {
    let cloneTree = cloneDeep(tree);
    if (!Array.isArray(cloneTree)) {
        cloneTree = [cloneTree];
    }

    function mapFunc(
        _tree: TreeNode<T> | TreeNode<T>[] | T | T[],
        _parentPath = '',
        _level = 0,
        _parent: TreeNode<T> | null = null
    ): T[] {
        if (!Array.isArray(_tree)) {
            _tree = [_tree];
        }
        return _tree
            .map((node: TreeNode<T>, i: number) => {
                const fullPath = node.path ? `${_parentPath}/${node.path}`.replace(/\/+/g, '/') : '';
                node.sort = i + 1; // sort 从 1 开始
                node.parent = _parent || undefined; // 没有父节点说明是树的第一层
                const newNode = typeof customNodeFn === 'function' ? customNodeFn(node, fullPath, _level) : node;
                if (newNode) {
                    newNode.level = _level;
                    if (newNode[customChildrenKey] && newNode[customChildrenKey].length > 0) {
                        newNode[customChildrenKey] = mapFunc(newNode[customChildrenKey], fullPath, _level + 1, newNode);
                    }
                }
                return newNode;
            })
            .filter((node: TreeNode<T> | null) => node !== null);
    }
    return mapFunc(cloneTree, parentPath, level, parent);
}