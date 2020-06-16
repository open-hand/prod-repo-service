import React, { useRef, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import HeaderButtons from '../../../components/header-buttons';
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
    } else {
      mavenStore.setOpeLoading(false);
      return false;
    }
  }, [logListDs]);

  function refresh() {
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
    return [{
      name: formatMessage({ id: 'exportAuth' }),
      icon: 'get_app',
      handler: () => { mavenStore.setExportModalVisible(true); },
      display: currentTab === AUTH_TAB,
      group: 1,
    },
    {
      name: formatMessage({ id: 'refresh' }),
      icon: 'refresh',
      handler: refresh,
      display: true,
      group: 1,
    }];
  }

  const exportProps = {
    exportStore: mavenStore,
    tableRef,
    formatMessage,
    dataSet: publishAuthDs,
    title: formatMessage({ id: `${intlPrefix}.view.mavenLib`, defaultMessage: 'Maven制品库' }),
  };

  return (
    <React.Fragment>
      <HeaderButtons items={getButtons()} />
      <ExportAuthority {...exportProps} />
    </React.Fragment>
  );
});

export default AppModals;
