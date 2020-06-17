import { useLocalStore } from 'mobx-react-lite';

export default function useStore(MAVEN) {
  return useLocalStore(() => ({
    showHeader: true,
    setShowHeader(flag) {
      this.showHeader = flag;
    },
    get getShowHeader() {
      return this.showHeader;
    },

    selectedMenu: MAVEN, // 选中的仓库类型
    setSelectedMenu(data) {
      this.selectedMenu = data;
    },
    get getSelectedMenu() {
      return this.selectedMenu;
    },

    repositoryId: null,
    setRepositoryId(value) {
      this.repositoryId = value;
    },
    get getRepositoryId() {
      return this.repositoryId;
    },
    dockerRepoInfo: {},
    setDockerRepoInfo(data) {
      this.dockerRepoInfo = data;
    },
    get getDockerRepoInfo() {
      return this.dockerRepoInfo;
    },
    npmPackageName: undefined,
    setNpmPackageId(data) {
      this.npmPackageName = data;
    },
    get getNpmPackageId() {
      return this.npmPackageName;
    },
  }));
}
