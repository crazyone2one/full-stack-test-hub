import type {App} from 'vue';

import outerClick from './outerClick';

export default {
    install(Vue: App) {
        // Vue.directive('permission', permission);
        // Vue.directive('xpack', validateLicense);
        // Vue.directive('expire', validateExpiration);
        Vue.directive('outer', outerClick);
    },
};