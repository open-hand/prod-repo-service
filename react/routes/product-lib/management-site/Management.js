import React from 'react';
import { observer } from 'mobx-react-lite';
import { Page } from '@choerodon/boot';
import MainView from './main-view';

function Resource() {
  return (
    <Page>
      <MainView />
    </Page>
  );
}

export default observer(Resource);
