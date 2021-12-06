import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { withRouter } from 'react-router-dom';
import { injectIntl } from 'react-intl';
import { useFormatMessage, useFormatCommon } from '@choerodon/master';
import { itemTypeMappings } from './mappings';
import useStore from './useStore';

const Store = createContext();

export function useProdStore() {
  return useContext(Store);
}

export const StoreProvider = withRouter(injectIntl(inject('AppState')(observer((props) => {
  const {
    AppState,
    intl,
    children,
  } = props;
  const { organizationId } = AppState.currentMenuType;
  const { formatMessage } = intl;
  const intlPrefix = 'infra.prod.lib';
  const format = useFormatMessage(intlPrefix);
  const formatCommon = useFormatCommon();
  const itemTypes = useMemo(() => itemTypeMappings, []);
  const prodStore = useStore(itemTypes.MAVEN);

  const value = {
    ...props,
    intlPrefix,
    prefixCls: 'c7ncd-deployment',
    itemTypes,
    prodStore,
    formatMessage,
    organizationId,
    format,
    formatCommon,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}))));
