import React from 'react';
import { observer } from 'mobx-react-lite';
import { Page } from '@choerodon/boot';
import MainView from './main-view';
import { useProdStore } from './stores';

function Resource() {
  const {
    permissions,
  } = useProdStore();

  return (
    <Page service={permissions}>
      <MainView />
    </Page>
  );
}

export default observer(Resource);
