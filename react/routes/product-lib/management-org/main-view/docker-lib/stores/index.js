import React, { createContext, useContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import MirrorLibDS from './MirrorLibDS';
import MirrorListDS from './MirrorListDS';
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

export function useDockerStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(observer((props) => {
  const { children, intl: { formatMessage } } = props;
  const {
    AppState: { currentMenuType: { organizationId } },
    intlPrefix,
  } = useProdStore();

  const tabs = useMemo(() => ({
    MIRROR_TAB: 'mirrorLib',
    LIST_TAB: 'mirrorList',
    AUTH_TAB: 'authList',
    LOG_TAB: 'logList',
  }), []);
  const dockerStore = useStore(tabs.MIRROR_TAB, organizationId);
  const logTabKey = dockerStore.getLogTabKey;

  const mirrorLibDs = useMemo(() => new DataSet(MirrorLibDS(intlPrefix, formatMessage, organizationId)), [organizationId]);
  const mirrorListDS = useMemo(() => new DataSet(MirrorListDS(intlPrefix, formatMessage, organizationId)), [organizationId]);
  const authListDs = useMemo(() => new DataSet(AuthListDS(intlPrefix, formatMessage, organizationId)), [organizationId]);
  const logListDs = useMemo(() => new DataSet(LogListDS(formatMessage, organizationId, logTabKey)), [organizationId, logTabKey]);


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
    mirrorLibDs,
    mirrorListDS,
    authListDs,
    logListDs,
    baseInfoDs,
    dockerStore,
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
