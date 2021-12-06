import React, { createContext, useContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { injectIntl } from 'react-intl';
import LibListDS from './LibListDS';
import { useProdStore } from '../../../stores';

const Store = createContext();

export function useNpmStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(observer((props) => {
  const { children, intl: { formatMessage } } = props;
  const {
    AppState: { currentMenuType: { organizationId } },
    intlPrefix,
    format,
    formatCommon,
  } = useProdStore();

  const libListDs = useMemo(() => new DataSet(LibListDS(intlPrefix, formatMessage)), []);

  const value = {
    ...props,
    organizationId,
    intlPrefix,
    formatMessage,
    libListDs,
    format,
    formatCommon,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
