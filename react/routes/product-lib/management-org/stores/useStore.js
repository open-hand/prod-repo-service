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

    repositoryName: '',
    setRepositoryName(value) {
      this.repositoryName = value;
    },
    get getRepositoryName() {
      return this.repositoryName;
    },
    dockerRepoInfo: {},
    setDockerRepoInfo(data) {
      this.dockerRepoInfo = data;
    },
    get getDockerRepoInfo() {
      return this.dockerRepoInfo;
    },
    npmPackageName: undefined,
    setNpmPackageName(data) {
      this.npmPackageName = data;
    },
    get getNpmPackageName() {
      return this.npmPackageName;
    },
  }));
}
