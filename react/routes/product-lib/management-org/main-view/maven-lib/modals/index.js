import React, { useRef, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { HeaderButtons, Header } from '@choerodon/master';
import { useMavenStore } from '../stores';
import ExportAuthority from './export-authority';

const AppModals = observer(() => {
  const {
    formatMessage,
    tabs: {
      LIB_TAB,
      PACKAGE_TAB,
      AUTH_TAB,
      LOG_TAB,
    },
    intlPrefix,
    libListDs,
    packageListDs,
    publishAuthDs,
    logListDs,
    mavenStore,
    repoListDs,
  } = useMavenStore();

  const tableRef = useRef();
  const currentTab = mavenStore.getTabKey;

  // 操作日志刷新
  const loadData = useCallback(async (page = 1) => {
    mavenStore.setOpeLoading(true);
    const res = await logListDs.query(page);
    const records = mavenStore.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        logListDs.unshift(...records);
      }
      mavenStore.setOldOptsRecord(logListDs.records);
      mavenStore.setLoadMoreBtn(res.hasNextPage);
      mavenStore.setOpeLoading(false);
      return res;
    }
    mavenStore.setOpeLoading(false);
    return false;
  }, [logListDs]);

  function refresh() {
    repoListDs.query();
    switch (currentTab) {
      case LIB_TAB:
        libListDs.query();
        break;
      case PACKAGE_TAB:
        packageListDs.query();
        break;
      case AUTH_TAB:
        publishAuthDs.query();
        break;
      case LOG_TAB:
        loadData();
        break;
      default:
    }
  }

  function getButtons() {
    const result = [{
      name: formatMessage({ id: 'exportAuth' }),
      icon: 'get_app',
      handler: () => { mavenStore.setExportModalVisible(true); },
      display: currentTab === AUTH_TAB,
    },
    {
      icon: 'refresh',
      handler: refresh,
      iconOnly: true,
    },
    ];
    return result;
  }

  const exportProps = {
    exportStore: mavenStore,
    tableRef,
    formatMessage,
    dataSet: publishAuthDs,
    title: formatMessage({ id: `${intlPrefix}.view.mavenLib`, defaultMessage: 'Maven制品库' }),
  };

  return (
    <>
      <Header>
        <HeaderButtons items={getButtons()} />
      </Header>
      <ExportAuthority {...exportProps} />
    </>
  );
});

export default AppModals;
