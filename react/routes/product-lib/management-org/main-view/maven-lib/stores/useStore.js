import { useLocalStore } from 'mobx-react-lite';

export default function useStore({ LIB_TAB }) {
  return useLocalStore(() => ({
    tabKey: LIB_TAB,

    setTabKey(data) {
      this.tabKey = data;
    },
    get getTabKey() {
      return this.tabKey;
    },

    info: {},
    setGuideInfo(data) {
      this.info = data;
    },

    networkIds: [],
    setNetworkIds(data) {
      this.networkIds = data;
    },
    get getNetworkIds() {
      return this.networkIds;
    },
    // 权限导出弹窗
    exportModalVisible: false,
    setExportModalVisible(data) {
      this.exportModalVisible = data;
    },
    get getExportModalVisible() {
      return this.exportModalVisible;
    },
    opeLoading: false,
    setOpeLoading(loading) {
      this.opeLoading = loading;
    },
    // 操作日志数据
    oldOptsRecord: [],
    setOldOptsRecord(data) {
      this.oldOptsRecord = data || [];
    },
    get getOldOptsRecord() {
      return this.oldOptsRecord;
    },
    // 加载更多按钮
    isMore: false,
    setLoadMoreBtn(data) {
      this.isMore = data;
    },
    get getLoadMoreBtn() {
      return this.isMore;
    },
  }));
}
