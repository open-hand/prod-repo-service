import React, { createContext } from 'react';
import { Page, axios, stores } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { useStore } from '../index';
import RepoList from './RepoList';
import { MavenTabContainer, DockerTabContainer, NpmTabContainer, CustomDockerTabContainer } from './RepoDetail';
import './index.less';

export const RepositoryIdContext = createContext();
export const CurrentRoleContext = createContext();

export const useUserAuth = () => {
  const currentRole = React.useContext(CurrentRoleContext);
  const { productType, repositoryId, projectId } = React.useContext(RepositoryIdContext);
  return currentRole[productType][repositoryId || projectId];
};

const Pages = () => {
  const [loading, setLoading] = React.useState(true);
  const [activeRepository, setActiveRepository] = React.useState(false);
  const [currentRole, setCurrentRole] = React.useState({
    MAVEN: [],
    NPM: [],
    DOCKER: [],
  });
  const { repoListDs } = useStore();

  const init = React.useCallback(async () => {
    setLoading(true);
    const { projectId } = stores.AppState.currentMenuType;
    const res = await repoListDs.query();
    const ids = res.map(o => o.repositoryId || o.projectId);
    const userAuth = await axios.post(`/rdupm/v1/prod-users/getRoleList?projectId=${projectId}&ids=${ids}`);
    setCurrentRole(userAuth);
    setLoading(false);
  }, []);

  React.useEffect(() => {
    init();
  }, [init]);

  return (
    <CurrentRoleContext.Provider value={currentRole}>
      <Page>
        {!loading && !activeRepository && <RepoList setActiveRepository={setActiveRepository} init={init} />}
        {!loading && activeRepository &&
          <RepositoryIdContext.Provider value={activeRepository}>
            {activeRepository.productType === 'DOCKER_CUSTOM' && <CustomDockerTabContainer />}
            {activeRepository.productType === 'DOCKER' && <DockerTabContainer />}
            {activeRepository.productType === 'MAVEN' && <MavenTabContainer />}
            {activeRepository.productType === 'NPM' && <NpmTabContainer />}
          </RepositoryIdContext.Provider>
        }
      </Page>
    </CurrentRoleContext.Provider>
  );
};

export default observer(Pages);
