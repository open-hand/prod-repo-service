import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import CopyFormDS from './CopyFormDS';

const Store = createContext();

export function useVersionCopyStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    children,
    AppState: { currentMenuType: { organizationId } },
    intl: { formatMessage },
    intlPrefix,
    srcRepoName,
    digest,
  } = props;

  const copyFormDs = useMemo(() => new DataSet(CopyFormDS(intlPrefix, formatMessage, organizationId, srcRepoName, digest)), [formatMessage, organizationId, srcRepoName]);

  useEffect(() => {
    copyFormDs.create();
  }, []);

  const value = {
    ...props,
    copyFormDs,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));

