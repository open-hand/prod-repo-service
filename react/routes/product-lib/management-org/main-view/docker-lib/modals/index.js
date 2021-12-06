import React, { useRef, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { Modal } from 'choerodon-ui/pro';
import { HeaderButtons, Header } from '@choerodon/master';
import { useDockerStore } from '../stores';
import ResourceConfig from './resource-config';
import ExportAuthority from './export-authority';
import './index.less';
import { useProdStore } from '@/routes/product-lib/management-org/stores/index';

const modalKey = Modal.key();
const modalStyle = {
  width: '3.8rem',
};

const AppModals = observer(() => {
  const {
    formatCommon,
    formatClient,
  } = useProdStore();

  const {
    intlPrefix,
    formatMessage,
    tabs: {
      MIRROR_TAB,
      LIST_TAB,
      AUTH_TAB,
      LOG_TAB,
    },
    mirrorLibDs,
    mirrorListDS,
    authListDs,
    logListDs,
    dockerStore,
    repoListDs,
  } = useDockerStore();
  const currentTab = dockerStore.getTabKey;
  const tableRef = useRef();

  // 操作日志刷新
  const loadData = useCallback(async (page = 1) => {
    dockerStore.setOpeLoading(true);
    const res = await logListDs.query(page);
    const records = dockerStore.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        logListDs.unshift(...records);
      }
      dockerStore.setOldOptsRecord(logListDs.records);
      dockerStore.setLoadMoreBtn(res.hasNextPage);
      dockerStore.setOpeLoading(false);
      return res;
    }
    dockerStore.setOpeLoading(false);
    return false;
  }, [logListDs]);

  function refresh() {
    repoListDs.query();
    switch (currentTab) {
      case MIRROR_TAB:
        mirrorLibDs.query();
        break;
      case LIST_TAB:
        mirrorListDS.query();
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

  function openConfigModal() {
    Modal.open({
      key: modalKey,
      style: modalStyle,
      drawer: true,
      title: formatMessage({ id: `${intlPrefix}.view.globalResource.title` }),
      className: 'infra-prod-lib-org-modals',
      children: <ResourceConfig
        refresh
        intlPrefix={intlPrefix}
        repoName="全部仓库"
      />,
      okText: formatMessage({ id: 'save' }),
    });
  }
  const openExportModal = useCallback(() => {
    console.log('a');
    Modal.open({
      title: formatMessage({ id: 'exportModal.confirm.title', defaultMessage: '权限导出确认' }),
      children: <ExportAuthority {...exportProps} />,
    });
  }, []);
  function getButtons() {
    const result = [
      {
        name: formatClient({ id: 'docker.mirrorList.globalResourceAllocation' }),
        icon: 'settings_applications',
        handler: openConfigModal,
        display: currentTab === MIRROR_TAB || currentTab === LIST_TAB,
      },
      {
        name: formatClient({ id: 'docker.permission.permissiontoexport' }),
        icon: 'get_app',
        handler: () => openExportModal(),
        display: currentTab === AUTH_TAB,
      },
      {
        icon: 'refresh',
        handler: refresh,
        iconOnly: true,
        color: 'default',
      },
    ];
    return result;
  }
  const exportProps = {
    exportStore: dockerStore,
    tableRef,
    formatMessage,
    dataSet: authListDs,
    title: formatMessage({ id: `${intlPrefix}.view.dockerLib`, defaultMessage: 'Docker制品库' }),
  };

  return (
    <>
      <Header>
        <HeaderButtons items={getButtons()} />
      </Header>
    </>
  );
});

export default AppModals;
