import type {FormItemRule, SelectOption, SelectGroupOption} from "naive-ui";

export type FormMode = 'create' | 'edit';
export type FormItemType = 'input' | 'select' | 'inputNumber' | 'tagInput' | 'multiple' | 'switch' | 'textarea';

export interface CustomValidator {
    notRepeat?: boolean;
}

export interface FormItemModel {
    field: string;
    type: FormItemType;
    rules?: (FormItemRule & CustomValidator)[];
    label?: string;
    placeholder?: string;
    min?: number;
    max?: number;
    maxLength?: number;
    hideAsterisk?: boolean;
    hideLabel?: boolean;
    children?: FormItemModel[];
    options?: Array<SelectOption | SelectGroupOption>; // select option 选项
    className?: string; // 自定义样式
    defaultValue?: string | string[] | number | number[] | boolean; // 默认值
    hasRedStar?: boolean; // 是否有红星
    tooltip?: string;
    disabled?: boolean;
    maxKey?: string;
    minKey?: string;
    getPrecisionFun?: (model: FormItemModel, ele: FormItemModel) => number;

    [key: string]: any;
}