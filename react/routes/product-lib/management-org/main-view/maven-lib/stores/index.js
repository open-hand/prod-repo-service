import React, { createContext, useContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import LibListDS from './LibListDS';
import PackageListDS from './PackageListDS';
import PublishAuthDS from './PublishAuthDS';
import LogListDS from './LogListDS';
import { useProdStore } from '../../../stores';
import useStore from './useStore';
// import useConfigMapStore from './useConfigMapStore';
// import useSecretStore from './useSecretStore';
// import useDomainStore from './useDomainStore';
// import useNetworkStore from './useNetworkStore';

const Store = createContext();

export function useMavenStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(observer((props) => {
  const { children, intl: { formatMessage } } = props;
  const {
    AppState: { currentMenuType: { organizationId } },
    intlPrefix,
  } = useProdStore();

  const tabs = useMemo(() => ({
    LIB_TAB: 'libList',
    PACKAGE_TAB: 'packageList',
    AUTH_TAB: 'publishAuth',
    LOG_TAB: 'logList',
  }), []);
  const mavenStore = useStore(tabs);

  const libListDs = useMemo(() => new DataSet(LibListDS(intlPrefix, formatMessage, organizationId)), [organizationId]);
  const packageListDs = useMemo(() => new DataSet(PackageListDS(organizationId, formatMessage, intlPrefix)), [organizationId]);
  const publishAuthDs = useMemo(() => new DataSet(PublishAuthDS(intlPrefix, formatMessage, organizationId)), [organizationId]);
  const logListDs = useMemo(() => new DataSet(LogListDS(formatMessage, organizationId)), [organizationId]);

  // const mappingStore = useConfigMapStore();
  // const cipherStore = useSecretStore();
  // const domainStore = useDomainStore();
  // const networkStore = useNetworkStore();

  const value = {
    ...props,
    intlPrefix,
    formatMessage,
    tabs,
    libListDs,
    packageListDs,
    publishAuthDs,
    logListDs,
    mavenStore,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
