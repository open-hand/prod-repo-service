import React, { lazy, Suspense, useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import { Icon } from 'choerodon-ui';
import { Page, Breadcrumb, Content } from '@choerodon/boot';
import Loading from '@/components/loading';
import { useProdStore } from '../stores';

import './index.less';

const MavenLib = lazy(() => import('./maven-lib'));
const NpmLib = lazy(() => import('./npm-lib'));

const MainView = observer(() => {
  const {
    prodStore,
    itemTypes: {
      MAVEN,
      NPM,
    },
  } = useProdStore();

  const { getSelectedMenu, setSelectedMenu } = prodStore;

  const content = useMemo(() => {
    if (!setSelectedMenu) return <Loading display />;

    const cmMaps = {
      [MAVEN]: <MavenLib />,
      [NPM]: <NpmLib />,
    };
    return <Suspense fallback={<Loading display />}>{cmMaps[getSelectedMenu]}</Suspense>;
  }, [getSelectedMenu]);

  return (
    <Page>
      <Breadcrumb />
      <Content className="product-lib-org-management">
        <div className="product-lib-org-management-lib-type-list">
          <div
            className={getSelectedMenu !== MAVEN ? 'product-lib-org-management-lib-type-list-item' : 'product-lib-org-management-lib-type-list-item-selected'}
            onClick={() => setSelectedMenu(MAVEN)}
            role="none"
          >
            <div className="product-lib-org-management-lib-type-list-item-maven" />
            {getSelectedMenu === MAVEN
              && <Icon type="check_circle" className="product-lib-org-management-lib-type-list-item-selector-icon" />}
          </div>
          <div
            className={getSelectedMenu !== NPM ? 'product-lib-org-management-lib-type-list-item' : 'product-lib-org-management-lib-type-list-item-selected'}
            onClick={() => setSelectedMenu(NPM)}
            role="none"
          >
            <div className="product-lib-org-management-lib-type-list-item-npm" />
            {getSelectedMenu === NPM
              && <Icon type="check_circle" className="product-lib-org-management-lib-type-list-item-selector-icon" />}
          </div>
        </div>
        {content}
      </Content>
    </Page>
  );
});

export default MainView;
