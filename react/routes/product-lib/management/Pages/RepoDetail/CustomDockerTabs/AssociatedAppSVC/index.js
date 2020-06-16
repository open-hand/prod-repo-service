/**
* 自定义harbor仓库关联应用服务
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/6/09
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import { message } from 'choerodon-ui';
import { Table, Modal, DataSet } from 'choerodon-ui/pro';
import { axios, Action, stores } from '@choerodon/boot';
import { useObserver } from 'mobx-react-lite';
import { TabKeyEnum } from '../../CustomDockerTabContainer';
import AssociatedAppSVCDataSet from './AssociatedAppSVCDataSet';

const intlPrefix = 'infra.prod.lib';

const { Column } = Table;
const AssociatedAppSVC = ({ detail, repositoryId, projectShare, formatMessage, activeTabKey }, ref) => {
  const { currentMenuType: { projectId, organizationId } } = stores.AppState;
  const associatedAppSVCDs = React.useRef(new DataSet(AssociatedAppSVCDataSet(intlPrefix, formatMessage, organizationId, projectId))).current;

  useEffect(() => {
    if (activeTabKey === TabKeyEnum.ASSOCIATE) {
      associatedAppSVCDs.setQueryParameter('customRepoId', repositoryId);
      associatedAppSVCDs.query();
    }
  }, [activeTabKey]);

  React.useImperativeHandle(ref, () => ({
    init: () => associatedAppSVCDs.query(),
  }));

  const handleDelete = async (data) => {
    const deleteKey = Modal.key();
    Modal.open({
      key: deleteKey,
      title: formatMessage({ id: 'confirm.delete' }),
      children: '确认删除关联应用服务？',
      okText: formatMessage({ id: 'delete' }),
      okProps: { color: 'red' },
      cancelProps: { color: 'dark' },
      onOk: async () => {
        try {
          await axios.delete(`/rdupm/v1/${organizationId}/harbor-custom-repos/delete-relation/${data.id}`, { data: detail });
          message.success(formatMessage({ id: 'success.delete', defaultMessage: '删除成功' }));
          associatedAppSVCDs.query();
        } catch (error) {
          // message.error(error);
        }
      },
      footer: ((okBtn, cancelBtn) => (
        <React.Fragment>
          {cancelBtn}{okBtn}
        </React.Fragment>
      )),
      movable: false,
    });
  };

  function renderAction({ record }) {
    const data = record.toData();
    const actionData = [
      {
        service: ['choerodon.code.project.infra.product-lib.ps.project-owner-harbor'],
        text: formatMessage({ id: 'delete', defaultMessage: '删除' }),
        action: () => handleDelete(data),
      },
    ];
    return <Action data={actionData} />;
  }

  return useObserver(() => (
    <Table dataSet={associatedAppSVCDs} className="no-border-top-table" >
      <Column name="name" />
      {projectShare === 'false' && <Column renderer={renderAction} width={70} />}
      <Column name="code" />
      <Column name="type" />
      <Column name="creationDate" />
    </Table>
  ));
};

export default React.forwardRef(AssociatedAppSVC);
