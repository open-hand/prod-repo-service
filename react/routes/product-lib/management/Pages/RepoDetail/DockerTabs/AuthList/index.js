/**
* harbor权限列表
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/30
* @copyright 2020 ® HAND
*/
import React, { useEffect, useCallback } from 'react';
import { message } from 'choerodon-ui';
import { Table, Modal } from 'choerodon-ui/pro';
import { axios, Action } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import { TabKeyEnum } from '../../DockerTabContainer';
import EditModal from './EditModal';
import { useUserAuth } from '../../../index';

const imgStyle = {
  width: '18px',
  height: '18px',
  borderRadius: '50%',
  flexShrink: 0,
};

const iconStyle = {
  width: '18px',
  height: '18px',
  fontSize: '13px',
  background: 'rgba(104, 135, 232, 0.2)',
  color: 'rgba(104,135,232,1)',
  borderRadius: '50%',
  lineHeight: '18px',
  textAlign: 'center',
  flexShrink: 0,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
};

const intlPrefix = 'infra.prod.lib';

const { Column } = Table;
const AuthList = ({ dockerAuthDs, formatMessage, activeTabKey }) => {
  const userAuth = useUserAuth();
  useEffect(() => {
    if (activeTabKey === TabKeyEnum.USER_AUTH) {
      dockerAuthDs.query();
    }
  }, [activeTabKey]);

  const rendererIcon = useCallback((imageUrl, text) => {
    let iconElement;
    if (imageUrl) {
      iconElement = <img src={imageUrl} alt="" style={imgStyle} />;
    } else {
      iconElement = <div style={iconStyle}>{text[0]}</div>;
    }
    return (
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {iconElement}
        <span
          style={{
            marginLeft: '7px',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
          }}
        >
          {text}
        </span>
      </div>
    );
  }, []);

  const handleDelete = async (data) => {
    const deleteKey = Modal.key();
    Modal.open({
      key: deleteKey,
      title: formatMessage({ id: 'confirm.delete' }),
      children: formatMessage({ id: `${intlPrefix}.view.confirm.deleteAuth` }),
      okText: formatMessage({ id: 'delete' }),
      onOk: async () => {
        try {
          await axios.delete('/rdupm/v1/harbor-auths', { data });
          message.success(formatMessage({ id: 'success.delete', defaultMessage: '删除成功' }));
          dockerAuthDs.query();
        } catch (error) {
          // message.error(error);
        }
      },
      footer: ((okBtn, cancelBtn) => (
        <>
          {cancelBtn}
          {okBtn}
        </>
      )),
      movable: false,
    });
  };

  const handleOpenEditModal = useCallback(async (data) => {
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.changeAuth`, defaultMessage: '修改权限' }),
      maskClosable: true,
      destroyOnClose: true,
      drawer: true,
      style: { width: '380px' },
      children: <EditModal formatMessage={formatMessage} dockerAuthDs={dockerAuthDs} data={data} />,
    });
  }, []);

  function renderAction({ record }) {
    const data = record.toData();
    const actionData = [
      {
        service: ['choerodon.code.project.infra.product-lib.ps.project-owner-harbor'],
        text: formatMessage({ id: `${intlPrefix}.view.changeAuth`, defaultMessage: '修改权限' }),
        action: () => handleOpenEditModal(data),
      }, {
        service: ['choerodon.code.project.infra.product-lib.ps.project-owner-harbor'],
        text: formatMessage({ id: 'delete', defaultMessage: '删除' }),
        action: () => handleDelete(data),
      },
    ];
    return data.locked !== 'Y' && <Action data={actionData} />;
  }

  return (
    <Table dataSet={dockerAuthDs} className="no-border-top-table">
      <Column name="loginName" />
      {userAuth?.includes('projectAdmin') && <Column renderer={renderAction} width={70} />}
      <Column name="realName" renderer={({ text, record }) => rendererIcon(record.toData().userImageUrl, text)} />
      <Column name="memberRole" />
      <Column name="harborRoleValue" />
      <Column name="endDate" />
    </Table>
  );
};

export default observer(AuthList);
