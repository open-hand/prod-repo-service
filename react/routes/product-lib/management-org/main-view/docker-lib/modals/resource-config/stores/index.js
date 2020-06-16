import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import ConfigFormDS from './ConfigFormDS';

const Store = createContext();

export function useConfigStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    intl: { formatMessage },
    children,
    intlPrefix,
    repoName,
    projectId,
  } = props;
  const formDs = useMemo(() => new DataSet(ConfigFormDS({ formatMessage, intlPrefix, projectId, repoName })), [projectId, repoName]);

  const value = {
    ...props,
    formDs,
    intlPrefix,
    formatMessage,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
