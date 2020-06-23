import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import LibListDS from './LibListDS';
import PackageListDS from './PackageListDS';
import AuthListDS from './AuthListDS';
import BaseInfoDS from './BaseInfoDS';
import LogListDS from './LogListDS';
import RepoListDS from './RepoListDS';
import { useProdStore } from '../../../stores';
import useStore from './useStore';
// import useConfigMapStore from './useConfigMapStore';
// import useSecretStore from './useSecretStore';
// import useDomainStore from './useDomainStore';
// import useNetworkStore from './useNetworkStore';

const Store = createContext();

export function useNpmStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(observer((props) => {
  const { children, intl: { formatMessage } } = props;
  const {
    AppState: { currentMenuType: { organizationId }, userInfo: { id: userId } },
    intlPrefix,
  } = useProdStore();

  const tabs = useMemo(() => ({
    LIB_TAB: 'libList',
    LIST_TAB: 'packageList',
    AUTH_TAB: 'authList',
    LOG_TAB: 'logList',
  }), []);
  const npmStore = useStore(tabs.LIB_TAB, organizationId);

  const repoListDs = useMemo(() => new DataSet(RepoListDS({ organizationId })), [organizationId]);


  const libListDs = useMemo(() => new DataSet(LibListDS(intlPrefix, formatMessage, organizationId, userId)), [organizationId]);
  const packageListDs = useMemo(() => new DataSet(PackageListDS(intlPrefix, formatMessage, organizationId, repoListDs)), [organizationId]);
  const authListDs = useMemo(() => new DataSet(AuthListDS(intlPrefix, formatMessage, organizationId, repoListDs)), [organizationId]);
  const logListDs = useMemo(() => new DataSet(LogListDS(formatMessage, organizationId)), [organizationId]);


  const baseInfoDs = useMemo(() => new DataSet(BaseInfoDS()), []);


  useEffect(() => {
    repoListDs.transport.read = () => ({
      url: `/rdupm/v1/nexus-repositorys/organizations/${organizationId}/npm/repo/name`,
      method: 'get',
    });
    repoListDs.query();
  }, [organizationId]);

  const value = {
    ...props,
    organizationId,
    intlPrefix,
    formatMessage,
    tabs,
    libListDs,
    packageListDs,
    authListDs,
    logListDs,
    baseInfoDs,
    npmStore,
    repoListDs,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
