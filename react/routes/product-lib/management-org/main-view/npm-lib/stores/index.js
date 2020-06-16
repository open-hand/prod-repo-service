import React, { createContext, useContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import LibListDS from './LibListDS';
import PackageListDS from './PackageListDS';
import AuthListDS from './AuthListDS';
import BaseInfoDS from './BaseInfoDS';
import LogListDS from './LogListDS';
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

  const libListDs = useMemo(() => new DataSet(LibListDS(intlPrefix, formatMessage, organizationId, userId)), [organizationId]);
  const packageListDs = useMemo(() => new DataSet(PackageListDS(intlPrefix, formatMessage, organizationId)), [organizationId]);
  const authListDs = useMemo(() => new DataSet(AuthListDS(intlPrefix, formatMessage, organizationId)), [organizationId]);
  const logListDs = useMemo(() => new DataSet(LogListDS(formatMessage, organizationId)), [organizationId]);


  const baseInfoDs = useMemo(() => new DataSet(BaseInfoDS()), []);


  // const mappingStore = useConfigMapStore();
  // const cipherStore = useSecretStore();
  // const domainStore = useDomainStore();
  // const networkStore = useNetworkStore();

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
    // mappingStore,
    // cipherStore,
    // domainStore,
    // networkStore,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
