import React, { lazy, Suspense } from 'react';
import { observer } from 'mobx-react-lite';
import { Tabs, Spin } from 'choerodon-ui';
import { Content } from '@choerodon/boot';
import { useNpmStore } from './stores';
import { useProdStore } from '../../stores';
import Modals from './modals';

import '../index.less';

const { TabPane } = Tabs;

const LibList = lazy(() => import('./lib-list'));
const PackageList = lazy(() => import('./package-list'));
const AuthList = lazy(() => import('./auth-list'));
const LogList = lazy(() => import('./log-list'));

const NpmContent = observer(() => {
  const {
    intl: { formatMessage },
    tabs: {
      LIB_TAB,
      LIST_TAB,
      AUTH_TAB,
      LOG_TAB,
    },
    npmStore,
  } = useNpmStore();
  const {
    intlPrefix,
  } = useProdStore();

  function handleChange(key) {
    npmStore.setTabKey(key);
  }

  return (
    <>
      <Content
        title={formatMessage({ id: `${intlPrefix}.view.npmLib`, defaultMessage: 'npm制品库' })}
        className="product-lib-org-management-tab-page-content"
      >
        <Tabs
          className="product-lib-org-management-tabs"
          animated={false}
          activeKey={npmStore.getTabKey}
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
            key={LIST_TAB}
            tab={formatMessage({ id: `${intlPrefix}.view.nexusComponent`, defaultMessage: '包列表' })}
          >
            <Suspense fallback={<Spin />}>
              <PackageList />
            </Suspense>
          </TabPane>
          <TabPane
            key={AUTH_TAB}
            tab={formatMessage({ id: `${intlPrefix}.view.userAuth`, defaultMessage: '用户权限' })}
          >
            <Suspense fallback={<Spin />}>
              <AuthList />
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
    </>
  );
});

export default NpmContent;
