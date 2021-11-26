import React, { lazy, Suspense } from 'react';
import { observer } from 'mobx-react-lite';
import { Spin } from 'choerodon-ui';
import { Content } from '@choerodon/boot';
import { useMavenStore } from './stores';
import { useProdStore } from '../../stores';

import '../index.less';

const LibList = lazy(() => import('./lib-list'));

const MavenContent = observer(() => {
  const {
    intl: { formatMessage },
  } = useMavenStore();
  const {
    intlPrefix,
  } = useProdStore();

  return (
    <>
      <Content
        title={formatMessage({ id: `${intlPrefix}.view.mavenLib`, defaultMessage: 'Maven制品库' })}
        className="product-lib-org-management-tab-page-content"
      >
        <Suspense fallback={<Spin />}>
          <LibList />
        </Suspense>
      </Content>
    </>
  );
});

export default MavenContent;
