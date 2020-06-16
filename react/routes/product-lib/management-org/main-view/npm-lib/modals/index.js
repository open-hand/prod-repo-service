import React, { useRef, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import HeaderButtons from '../../../components/header-buttons/index';
import { useNpmStore } from '../stores';
import ExportAuthority from './export-authority';
import './index.less';

const AppModals = observer(() => {
  const {
    intlPrefix,
    formatMessage,
    tabs: {
      LIB_TAB,
      LIST_TAB,
      AUTH_TAB,
      LOG_TAB,
    },
    libListDs,
    packageListDs,
    authListDs,
    logListDs,
    npmStore,
  } = useNpmStore();
  const currentTab = npmStore.getTabKey;

  const tableRef = useRef();

  // 操作日志刷新
  const loadData = useCallback(async (page = 1) => {
    npmStore.setOpeLoading(true);
    const res = await logListDs.query(page);
    const records = npmStore.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        logListDs.unshift(...records);
      }
      npmStore.setOldOptsRecord(logListDs.records);
      npmStore.setLoadMoreBtn(res.hasNextPage);
      npmStore.setOpeLoading(false);
      return res;
    } else {
      npmStore.setOpeLoading(false);
      return false;
    }
  }, [logListDs]);

  function refresh() {
    switch (currentTab) {
      case LIB_TAB:
        libListDs.query();
        break;
      case LIST_TAB:
        packageListDs.query();
        break;
      case AUTH_TAB:
        authListDs.query();
        break;
      case LOG_TAB:
        loadData();
        break;
      default:
    }
  }

  function getButtons() {
    return [
      {
        name: formatMessage({ id: 'exportAuth' }),
        icon: 'get_app',
        handler: () => { npmStore.setExportModalVisible(true); },
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
    exportStore: npmStore,
    tableRef,
    formatMessage,
    dataSet: authListDs,
    title: formatMessage({ id: `${intlPrefix}.view.npmLib`, defaultMessage: 'npm制品库' }),
  };


  return (
    <React.Fragment>
      <HeaderButtons items={getButtons()} />
      <ExportAuthority {...exportProps} />
    </React.Fragment>
  );
});

export default AppModals;
