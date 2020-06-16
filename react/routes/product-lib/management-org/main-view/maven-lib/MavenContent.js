import React, { lazy, Suspense } from 'react';
import { observer } from 'mobx-react-lite';
import { Tabs, Spin } from 'choerodon-ui';
import { Content } from '@choerodon/boot';
import { useMavenStore } from './stores';
import { useProdStore } from '../../stores';
import Modals from './modals';

import '../index.less';

const { TabPane } = Tabs;

const LibList = lazy(() => import('./lib-list'));
const PackageList = lazy(() => import('./package-list'));
const PublishAuth = lazy(() => import('./publish-auth'));
const LogList = lazy(() => import('./log-list'));

const MavenContent = observer(() => {
  const {
    intl: { formatMessage },
    tabs: {
      LIB_TAB,
      PACKAGE_TAB,
      AUTH_TAB,
      LOG_TAB,
    },
    mavenStore,
  } = useMavenStore();
  const {
    intlPrefix,
  } = useProdStore();

  function handleChange(key) {
    mavenStore.setTabKey(key);
  }

  return (
    <React.Fragment>
      <Content
        title={formatMessage({ id: `${intlPrefix}.view.mavenLib`, defaultMessage: 'Maven制品库' })}
        className="product-lib-org-management-tab-page-content"
      >
        <Tabs
          className="product-lib-org-management-tabs"
          animated={false}
          activeKey={mavenStore.getTabKey}
          onChange={handleChange}
        >
          <TabPane
            key={LIB_TAB}
            tab={formatMessage({ id: `${intlPrefix}.view.repo`, defaultMessage: '仓库列表' })}
          >
            <Suspense fallback={<Spin />}>
              <LibList />
            </Suspense>
          </TabPane>
          <TabPane
            key={PACKAGE_TAB}
            tab={formatMessage({ id: `${intlPrefix}.view.nexusComponent`, defaultMessage: '包列表' })}
          >
            <Suspense fallback={<Spin />}>
              <PackageList />
            </Suspense>
          </TabPane>
          <TabPane
            key={AUTH_TAB}
            tab={formatMessage({ id: `${intlPrefix}.view.publishAuth`, defaultMessage: '发布权限' })}
          >
            <Suspense fallback={<Spin />}>
              <PublishAuth />
            </Suspense>
          </TabPane>
          <TabPane
            key={LOG_TAB}
            tab={formatMessage({ id: `${intlPrefix}.view.operationLog`, defaultMessage: '操作日志' })}
          >
            <Suspense fallback={<Spin />}>
              <LogList />
            </Suspense>
          </TabPane>
        </Tabs>
      </Content>
      <Modals />
    </React.Fragment>
  );
});

export default MavenContent;
