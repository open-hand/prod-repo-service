import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { inject } from 'mobx-react';
import { asyncRouter, asyncLocaleProvider, nomatch } from '@choerodon/boot';
import { ModalContainer } from 'choerodon-ui/pro';

import './index.less';

// 制品库
const productManagement = asyncRouter(() => import('./routes/product-lib/management'));
const productManagementOrg = asyncRouter(() => import('./routes/product-lib/management-org'));
const productManagementSite = asyncRouter(() => import('./routes/product-lib/management-site'));

function RDUPMIndex({ match, AppState: { currentLanguage: language } }) {
  const IntlProviderAsync = asyncLocaleProvider(language, () => import(`./locale/${language}`));
  return (
    <IntlProviderAsync>
      <div className="hrds-prod-repo">
        <Switch>
          <Route path={`${match.url}/product-lib`} component={productManagement} />
          <Route path={`${match.url}/product-lib-org`} component={productManagementOrg} />
          <Route path={`${match.url}/product-lib-site`} component={productManagementSite} />
          <Route path="*" component={nomatch} />
        </Switch>
        <ModalContainer />
      </div>
    </IntlProviderAsync>
  );
}

export default inject('AppState')(RDUPMIndex);
