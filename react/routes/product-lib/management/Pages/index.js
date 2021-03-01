/*eslint-disable*/
import React, { createContext } from 'react';
import { Page, axios, stores } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { Spin } from 'choerodon-ui';
import { useStore } from '../index';
import RepoList from './RepoList';
import { MavenTabContainer, DockerTabContainer, NpmTabContainer, CustomDockerTabContainer } from './RepoDetail';
import './index.less';

export const RepositoryIdContext = createContext();
export const CurrentRoleContext = createContext();

export const useUserAuth = () => {
  const currentRole = React.useContext(CurrentRoleContext).currentRole;
  const { productType, projectId, sourceRepositoryId } = React.useContext(RepositoryIdContext);
  return currentRole[productType][sourceRepositoryId || projectId];
};

export const useAuthPermisson = () => React.useContext(CurrentRoleContext).useAuthPermission;

const Pages = () => {
  const [loading, setLoading] = React.useState(true);
  const [activeRepository, setActiveRepository] = React.useState(false);
  const [currentRole, setCurrentRole] = React.useState({
    MAVEN: [],
    NPM: [],
    DOCKER: [],
  });
  const [useAuthPermission, setUserAuthPermission] = React.useState(undefined);
  const { repoListDs } = useStore();

  const init = React.useCallback(async () => {
    setLoading(true);
    const { projectId } = stores.AppState.currentMenuType;
    const res = await repoListDs.query();
    const ids = res.filter(o => ['MAVEN', 'NPM']?.includes(o.productType)).map(o => ({ repositoryId: o.repositoryId || o.projectId }));
    const userAuth = await axios.post(`/rdupm/v1/prod-users/getRoleList?projectId=${projectId}`, ids);
    const authPermission = await axios.post(`/rdupm/v1/prod-users/role/getRoleList?projectId=${projectId}`, ids);
    setCurrentRole(userAuth);
    setUserAuthPermission(authPermission);
    setLoading(false);
  }, []);

  React.useEffect(() => {
    init();
  }, [init]);

  return (
    <CurrentRoleContext.Provider value={{
      currentRole,
      useAuthPermission,
    }}
    >
      <Spin spinning={loading} style={{ marginLeft: '50%', marginTop: '50vh', position: 'absolute' }} />
      <Page>
        {!loading && !activeRepository && <RepoList setActiveRepository={setActiveRepository} init={init} />}
        {!loading && activeRepository &&
          <RepositoryIdContext.Provider value={activeRepository}>
            {activeRepository.productType === 'DOCKER_CUSTOM' && <CustomDockerTabContainer />}
            {activeRepository.productType === 'DOCKER' && <DockerTabContainer />}
            {activeRepository.productType === 'MAVEN' && <MavenTabContainer activeRepository={activeRepository} />}
            {activeRepository.productType === 'NPM' && <NpmTabContainer activeRepository={activeRepository} />}
          </RepositoryIdContext.Provider>
        }
      </Page>
    </CurrentRoleContext.Provider>
  );
};

export default observer(Pages);
