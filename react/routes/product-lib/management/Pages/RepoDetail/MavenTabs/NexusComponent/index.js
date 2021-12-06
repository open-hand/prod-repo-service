/**
* 制品库包列表
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import { Table, Button, Modal } from 'choerodon-ui/pro';
import { Menu, Dropdown, message } from 'choerodon-ui';
import { axios, stores } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import UserAvatar from '@/components/user-avatar';
import Timeago from '@/components/date-time-ago/DateTimeAgo';
import moment from 'moment';
import { useUserAuth } from '../../../index';
import GuideButton from './GuideButton';
import { TabKeyEnum } from '../../MavenTabContainer';
import './index.less';


const { Column } = Table;
const NexusComponent = ({ formatMessage, nexusComponentDs, activeTabKey, repositoryId, repositoryName, enableFlag }) => {
  const userAuth = useUserAuth();

  useEffect(() => {
    if (activeTabKey === TabKeyEnum.NEXUS_COMPONENT) {
      nexusComponentDs.setQueryParameter('repositoryName', repositoryName);
      nexusComponentDs.setQueryParameter('repositoryId', repositoryId);
      nexusComponentDs.query();
    }
  }, [activeTabKey, repositoryName, repositoryId]);

  const handleDelete = async (record) => {
    const { repository, componentIds } = record;
    const { currentMenuType: { projectId, organizationId } } = stores.AppState;
    const button = await Modal.confirm({
      title: (
        <div>
          <p>{formatMessage({ id: 'confirm.delete', defaultMessage: '确认删除？' })}</p>
        </div>
      ),
    });
    if (button !== 'cancel') {
      try {
        await axios.delete(`/rdupm/v1/nexus-components/${organizationId}/project/${projectId}?repositoryId=${repositoryId}&&repositoryName=${repository}`, { data: componentIds });
        message.success(formatMessage({ id: 'success.delete', defaultMessage: '删除成功' }));
        nexusComponentDs.query();
      } catch (error) {
        // message.error(error);
      }
    }
  };

  const rendererDropDown = ({ text, record }) => {
    const menu = (
      <Menu>
        <Menu.Item key="0">
          <a onClick={() => handleDelete(record)}>{formatMessage({ id: 'delete', defaultMessage: '删除' })}</a>
        </Menu.Item>
      </Menu>
    );
    return (
      <div className="product-lib-management-selfrepo-render-dropdown-column">
        <GuideButton text={text} record={record} formatMessage={formatMessage} repositoryId={repositoryId} />
        {(userAuth.includes('projectAdmin') || userAuth.includes('developer')) && enableFlag === 'Y' &&
          <Dropdown overlay={menu} trigger={['click']}>
            <Button shape="circle" icon="more_vert" style={{ flexShrink: 0, float: 'right' }} />
          </Dropdown>
        }
      </div>
    );
  };


  const expandedRowRenderer = ({ record }) => {
    const { components } = record.toData();
    return (
      <table style={{ width: '100%' }}>
        <tbody>
          {
            components.map(o => (
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

  return (
    <Table
      dataSet={nexusComponentDs}
      expandedRowRenderer={expandedRowRenderer}
    >
      <Column name="version" renderer={({ text, record }) => rendererDropDown({ text, record: record.toData() })} />
      <Column name="group" />
      <Column name="name" />
      <Column name="creatorRealName" renderer={renderName} width={200} />
      <Column name="creationDate" renderer={({ value }) => value && <Timeago date={moment(value).format('YYYY-MM-DD HH:mm:ss')} />} />
    </Table>
  );
};

export default observer(NexusComponent);
