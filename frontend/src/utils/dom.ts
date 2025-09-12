export const isServerRendering = (() => {
    try {
        return !(typeof window !== 'undefined' && document !== undefined);
    } catch (e) {
        return true;
    }
})();