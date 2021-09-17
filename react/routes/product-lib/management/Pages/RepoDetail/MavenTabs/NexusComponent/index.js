/**
* 制品库包列表
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React, { useEffect, useMemo } from 'react';
import { Table, Button, Modal } from 'choerodon-ui/pro';
import { Menu, Dropdown, message } from 'choerodon-ui';
import { axios, stores, Action } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import UserAvatar from '@/components/user-avatar';
import Timeago from '@/components/date-time-ago/DateTimeAgo';
import { useUserAuth } from '../../../index';
import GuideButton from './GuideButton';
import { TabKeyEnum } from '../../MavenTabContainer';
import './index.less';

const { Column } = Table;
const NexusComponent = ({
  formatMessage, nexusComponentDs, activeTabKey, repositoryId, repositoryName, enableFlag,
}) => {
  const userAuth = useUserAuth();

  const hasPermission = useMemo(() => (
    (userAuth.includes('projectAdmin') || userAuth.includes('developer')) && enableFlag === 'Y'
  ), [userAuth, enableFlag]);

  useEffect(() => {
    if (activeTabKey === TabKeyEnum.NEXUS_COMPONENT) {
      nexusComponentDs.setQueryParameter('repositoryName', repositoryName);
      nexusComponentDs.setQueryParameter('repositoryId', repositoryId);
      nexusComponentDs.query();
    }
  }, [activeTabKey, repositoryName, repositoryId]);

  async function handleDeleteVerison(record) {
    const { repository, componentIds } = record.toData();
    const { currentMenuType: { projectId, organizationId } } = stores.AppState;
    try {
      await axios.delete(`/rdupm/v1/nexus-components/${organizationId}/project/${projectId}?repositoryId=${repositoryId}&repositoryName=${repository}`, { data: componentIds });
      message.success(formatMessage({ id: 'success.delete', defaultMessage: '删除成功' }));
      nexusComponentDs.query();
    } catch (error) {
      // message.error(error);
    }
  }

  const handleDelete = async (record) => {
    Modal.open({
      title: '删除包版本',
      children: (
        <div>
          <p>确认要删除包版本吗？</p>
        </div>
      ),
      onOk: () => handleDeleteVerison(record),
    });
  };

  const rendererDropDown = ({ text, record }) => (
    <GuideButton
      text={text}
      record={record}
      formatMessage={formatMessage}
      repositoryId={repositoryId}
    />
  );

  const expandedRowRenderer = ({ record }) => {
    const { components } = record.toData();
    return (
      <table style={{ width: '100%' }}>
        <tbody>
          {
            components.map((o) => (
              <tr key={o.id}>
                <td style={{ paddingLeft: '10px' }}>
                  {rendererDropDown({
                    text: o.version,
                    record: o,
                  })}
                </td>
                <td style={{ paddingLeft: '10px' }}>{o.group}</td>
                <td style={{ paddingLeft: '20px' }}>{o.name}</td>
                <td style={{ paddingLeft: '20px' }}>
                  <div style={{ display: 'inline-flex' }}>
                    <UserAvatar
                      user={{
                        loginName: o.creatorLoginName,
                        realName: o.creatorRealName,
                        imageUrl: o.creatorImageUrl,
                      }}
                    />
                  </div>
                </td>
                <td style={{ paddingLeft: '20px' }}>
                  {o.creationDate && <Timeago date={moment(o.creationDate).format('YYYY-MM-DD HH:mm:ss')} />}
                </td>
              </tr>
            ))
          }
        </tbody>
      </table>
    );
  };

  function renderName({ record }) {
    const avatar = (
      <div style={{ display: 'inline-flex' }}>
        <UserAvatar
          user={{
            loginName: record.get('creatorLoginName'),
            realName: record.get('creatorRealName'),
            imageUrl: record.get('creatorImageUrl'),
          }}
        />
      </div>
    );
    return avatar;
  }

  const renderAction = ({ record }) => {
    const actionData = [{
      text: formatMessage({ id: 'delete', defaultMessage: '删除' }),
      action: () => handleDelete(record),
    }];
    return <Action data={actionData} />;
  };

  return (
    <Table
      dataSet={nexusComponentDs}
      mode="tree"
      expandIconColumnIndex={hasPermission ? 1 : 0}
      // expandedRowRenderer={expandedRowRenderer}
      className="product-lib-nexusComponent-table"
    >
      {hasPermission ? <Column name="isChecked" editor width={50} /> : null}
      <Column
        name="version"
        renderer={({ text, record }) => rendererDropDown({ text, record: record.toData() })}
        tooltip="overflow"
        className={hasPermission ? '' : 'product-lib-nexusComponent-table-version'}
      />
      {/* {hasPermission ? ( */}
      <Column renderer={renderAction} width={60} />
      {/* ) : null} */}
      <Column name="group" />
      <Column name="name" />
      <Column name="creatorRealName" renderer={renderName} width={200} />
      <Column name="creationDate" renderer={({ value }) => value && <Timeago date={moment(value).format('YYYY-MM-DD HH:mm:ss')} />} />
    </Table>
  );
};

export default observer(NexusComponent);
