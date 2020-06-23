import React from 'react';
import { Button, Modal, DataSet } from 'choerodon-ui/pro';
import { Action, axios, stores } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import CreateModal from './CreateModal';
import EditModal from './EditModal';
import nexusCustomDataSet from './nexusCustomDataSet';
import './index.less';

const intlPrefix = 'infra.prod.lib';

const NexusAssociateModal = ({ init, formatMessage }) => {
  const [svcList, setSvcList] = React.useState([]);
  const createDs = React.useRef(new DataSet(nexusCustomDataSet(intlPrefix, formatMessage))).current;

  const fetchList = React.useCallback(async () => {
    const { currentMenuType: { projectId, organizationId } } = stores.AppState;
    const res = await axios.get(`/rdupm/v1/${organizationId}/nexus-server-configs/project/${projectId}/list`);
    setSvcList(res);
  }, [stores.AppState]);

  React.useEffect(() => {
    fetchList();
  }, [fetchList]);

  const openSubCreateModal = React.useCallback(() => {
    const key = Modal.key();
    Modal.open({
      key,
      title: '新增自定义nexus服务信息',
      maskClosable: false,
      destroyOnClose: true,
      drawer: true,
      className: 'product-lib-create-model',
      children: <CreateModal createDs={createDs} formatMessage={formatMessage} init={fetchList} />,
    });
  }, []);

  const openSubEditModal = React.useCallback((data) => {
    if (data.defaultFlag !== 1) {
      const key = Modal.key();
      Modal.open({
        key,
        title: '更新自定义nexus服务信息',
        maskClosable: false,
        destroyOnClose: true,
        drawer: true,
        className: 'product-lib-create-model',
        children: <EditModal createDs={createDs} formatMessage={formatMessage} init={fetchList} data={data} />,
      });
    }
  }, []);

  const enableNexusSvc = React.useCallback(async (data) => {
    const { currentMenuType: { projectId, organizationId } } = stores.AppState;
    await axios.post(`/rdupm/v1/${organizationId}/nexus-server-configs/project/${projectId}/enable`, data);
    fetchList();
    init();
  }, []);

  return (
    <React.Fragment>
      {
        svcList.map(o => (
          <section key={o.configId} className="prod-lib-nexus-associate-list-card">
            <div className="prod-lib-nexus-associate-list-card-info">
              <div className="prod-lib-nexus-associate-list-card-info-title">
                <span className={o.defaultFlag !== 1 && 'edit-link-cell'} onClick={() => openSubEditModal(o)}>{o.serverName}</span>
                {o.enableFlag === 1 && <span className="prod-lib-nexus-associate-list-card-info-title-enable-tag">启用</span>}
              </div>
              <div className="prod-lib-nexus-associate-list-card-info-addr">{o.serverUrl}</div>
            </div>
            {o.defaultFlag === 1 ?
              <Action
                data={[{
                  service: [],
                  text: formatMessage({ id: 'active', defaultMessage: '启用' }),
                  action: () => enableNexusSvc(o),
                }]}
              />
              :
              <Action
                data={[{
                  service: [],
                  text: formatMessage({ id: 'write', defaultMessage: '编辑' }),
                  action: () => openSubEditModal(o),
                }, {
                  service: [],
                  text: formatMessage({ id: 'active', defaultMessage: '启用' }),
                  action: () => enableNexusSvc(o),
                }]}
              />
            }
          </section>
        ))
      }

      <Button
        style={{ textAlign: 'left', marginBottom: '10px' }}
        funcType="flat"
        color="primary"
        icon="add"
        onClick={openSubCreateModal}
      >
        {formatMessage({ id: `${intlPrefix}.view.addCustomNexus`, defaultMessage: '添加自定义nexus服务' })}
      </Button>
    </React.Fragment>
  );
};

export default observer(NexusAssociateModal);
