import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore(MIRROR_TAB) {
  return useLocalStore(() => ({
    // 激活的Tab
    tabKey: MIRROR_TAB,
    setTabKey(data) {
      this.tabKey = data;
    },
    get getTabKey() {
      return this.tabKey;
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
    // 操作日志类型
    logTabKey: 'AuthLog',
    setLogTabKey(data) {
      this.logTabKey = data;
    },
    get getLogTabKey() {
      return this.logTabKey;
    },

    // 更新权限
    updateAuth(data, flag) {
      return axios.get(`/rdupm/v1/harbor-project/update/publicFlag/${data.projectId}?publicFlag=${flag}`);
    },
    // 删除镜像仓库
    deleteMirror(data) {
      return axios.delete(`/rdupm/v1/harbor-project/delete/${data.projectId}`);
    },
    // 删除镜像
    deleteImage(data) {
      return axios.delete('/rdupm/v1/harbor-image/delete', { data });
    },
  }));
}
