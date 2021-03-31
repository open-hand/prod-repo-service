import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import MirrorLibDS from './MirrorLibDS';
import MirrorListDS from './MirrorListDS';
import AuthListDS from './AuthListDS';
import BaseInfoDS from './BaseInfoDS';
import LogListDS from './LogListDS';
import RepoListDS from './RepoListDS';
import { useProdStore } from '../../../stores';
import useStore from './useStore';
import ScanDetailDS from './ScanDetailDS';
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

  const repoListDs = useMemo(() => new DataSet(RepoListDS({ organizationId })), [organizationId]);

  const mirrorLibDs = useMemo(() => new DataSet(MirrorLibDS(intlPrefix, formatMessage, organizationId, repoListDs)), [organizationId]);
  const mirrorListDS = useMemo(() => new DataSet(MirrorListDS(intlPrefix, formatMessage, organizationId, repoListDs)), [organizationId]);
  const authListDs = useMemo(() => new DataSet(AuthListDS(intlPrefix, formatMessage, organizationId, repoListDs)), [organizationId]);
  const logListDs = useMemo(() => new DataSet(LogListDS(formatMessage, organizationId, logTabKey, repoListDs)), [organizationId, logTabKey]);
  const scanDetailDs = useMemo(() => new DataSet(ScanDetailDS({ organizationId })), [organizationId]);

  const baseInfoDs = useMemo(() => new DataSet(BaseInfoDS()), []);

  useEffect(() => {
    repoListDs.transport.read = () => ({
      url: `/rdupm/v1/harbor-project/all/${organizationId}`,
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
    mirrorLibDs,
    mirrorListDS,
    authListDs,
    logListDs,
    baseInfoDs,
    dockerStore,
    repoListDs,
    scanDetailDs,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
