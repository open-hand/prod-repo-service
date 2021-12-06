import React, { lazy, Suspense } from 'react';
import { observer } from 'mobx-react-lite';
import { Tabs, Spin } from 'choerodon-ui';
import { Content } from '@choerodon/boot';
import { useDockerStore } from './stores';
import { useProdStore } from '../../stores';
import Modals from './modals';

import './index.less';

const { TabPane } = Tabs;

const MirrorLib = lazy(() => import('./mirror-lib'));
const MirrorList = lazy(() => import('./mirror-list'));
const AuthList = lazy(() => import('./auth-list'));
const LogList = lazy(() => import('./log-list'));

const DockerContent = observer(() => {
  const {
    intl: { formatMessage },
    tabs: {
      MIRROR_TAB,
      LIST_TAB,
      AUTH_TAB,
      LOG_TAB,
    },
    dockerStore,
  } = useDockerStore();
  const {
    intlPrefix,
    // formatCommon,
    formatClient,
  } = useProdStore();

  function handleChange(key) {
    dockerStore.setTabKey(key);
  }

  return (
    <>
      <Content
        title={formatMessage({ id: `${intlPrefix}.view.dockerLib`, defaultMessage: 'Docker制品库' })}
        className="product-lib-org-management-tab-page-content"
      >
        <Tabs
          className="product-lib-org-management-tabs"
          animated={false}
          activeKey={dockerStore.getTabKey}
          // eslint-disable-next-line react/jsx-no-bind
          onChange={handleChange}
        >
          <TabPane
            key={MIRROR_TAB}
            tab={formatClient({ id: 'docker.mirrorWarehouse.mirrorWarehouse' })}
          >
            <Suspense fallback={<Spin />}>
              <MirrorLib />
            </Suspense>
          </TabPane>
          <TabPane
            key={LIST_TAB}
            tab={formatClient({ id: 'docker.mirrorList.mirrorList' })}
          >
            <Suspense fallback={<Spin />}>
              <MirrorList />
            </Suspense>
          </TabPane>
          <TabPane
            key={AUTH_TAB}
            tab={formatClient({ id: 'docker.permission.permission' })}
          >
            <Suspense fallback={<Spin />}>
              <AuthList />
            </Suspense>
          </TabPane>
          <TabPane
            key={LOG_TAB}
            tab={formatClient({ id: 'docker.log.log' })}
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

export default DockerContent;
