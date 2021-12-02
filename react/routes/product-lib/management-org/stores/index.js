import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { observer } from 'mobx-react-lite';
import { withRouter } from 'react-router-dom';
import { injectIntl } from 'react-intl';
import { useFormatCommon, useFormatMessage } from '@choerodon/master';
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

  const intlPrefixNew = 'c7ncd.product-lib-org';
  const formatCommon = useFormatCommon();
  const formatClient = useFormatMessage(intlPrefixNew);

  const itemTypes = useMemo(() => itemTypeMappings, []);
  const prodStore = useStore(itemTypes.DOCKER);

  const value = {
    ...props,
    intlPrefix,
    prefixCls: 'c7ncd-deployment',
    permissions: [
      'choerodon.code.project.infra.product-lib.ps.project-owner-harbor',
      'choerodon.code.organization.infra.product-lib.ps.organization-admin-maven',
      'choerodon.code.organization.infra.product-lib.ps.organization-admin-npm',
    ],
    itemTypes,
    prodStore,
    formatMessage,
    organizationId,
    formatCommon,
    formatClient,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}))));
