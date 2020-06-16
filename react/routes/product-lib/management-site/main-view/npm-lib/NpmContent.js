import React, { lazy, Suspense } from 'react';
import { observer } from 'mobx-react-lite';
import { Spin } from 'choerodon-ui';
import { Content } from '@choerodon/boot';
import { useNpmStore } from './stores';
import { useProdStore } from '../../stores';

import '../index.less';

const LibList = lazy(() => import('./lib-list'));

const NpmContent = observer(() => {
  const {
    intl: { formatMessage },
  } = useNpmStore();
  const {
    intlPrefix,
  } = useProdStore();

  return (
    <React.Fragment>
      <Content
        title={formatMessage({ id: `${intlPrefix}.view.npmLib`, defaultMessage: 'Npm制品库' })}
        className="product-lib-org-management-tab-page-content"
      >
        <Suspense fallback={<Spin />}>
          <LibList />
        </Suspense>
      </Content>
    </React.Fragment>
  );
});

export default NpmContent;
