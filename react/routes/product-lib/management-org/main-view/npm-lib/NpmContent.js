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
    // prodStore,
    // itemTypes: {
    //   MAVEN,
    //   NPM,
    // },
  } = useProdStore();
  // const { getSelectedMenu } = prodStore;

  function handleChange(key) {
    npmStore.setTabKey(key);
  }

  // function getTitle() {
  //   let title = formatMessage({ id: `${intlPrefix}.view.dockerLib`, defaultMessage: 'Docker制品库' });
  //   switch (getSelectedMenu) {
  //     case MAVEN: {
  //       title = formatMessage({ id: `${intlPrefix}.view.mavenLib`, defaultMessage: 'Maven制品库' });
  //       break;
  //     }
  //     case NPM:
  //       title = formatMessage({ id: `${intlPrefix}.view.npmLib`, defaultMessage: 'Npm制品库' });
  //       break;
  //     default:
  //       title = formatMessage({ id: `${intlPrefix}.view.dockerLib`, defaultMessage: 'Docker制品库' });
  //       break;
  //   }
  //   return <span>{title}</span>;
  // }

  return (
    <React.Fragment>
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
    </React.Fragment>
  );
});

export default NpmContent;
