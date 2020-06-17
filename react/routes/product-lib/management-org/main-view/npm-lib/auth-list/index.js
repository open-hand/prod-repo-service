/**
* 用户权限
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/5/6
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import { Table } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import UserAvatar from '@/components/user-avatar';
import { useNpmStore } from '../stores';
import { useProdStore } from '../../../stores';

const { Column } = Table;

const AuthList = () => {
  const { prodStore: { getSelectedMenu } } = useProdStore();
  const {
    tabs: {
      AUTH_TAB,
    },
    npmStore,
    authListDs,
  } = useNpmStore();
  const { getTabKey } = npmStore;

  function refresh() {
    authListDs.query();
  }
  useEffect(() => {
    if (getTabKey === AUTH_TAB) {
      refresh();
    }
  }, [getTabKey, getSelectedMenu]);

  return (
    <div style={{ overflowX: 'hidden', height: 'calc(100% - 75px)' }}>
      <Table dataSet={authListDs} className="no-border-top-table">
        <Column name="neRepositoryName" />
        <Column
          name="projectName"
          renderer={({ record }) => (
            <div style={{ display: 'inline-flex' }}>
              <UserAvatar
                user={{
                  loginName: record.get('projectId'),
                  realName: record.get('projectName'),
                  imageUrl: record.get('projectImgUrl'), // TODO
                }}
              />
            </div>
          )}
        />
        <Column name="loginName" />
        <Column
          name="realName"
          sortable
          renderer={({ record }) => (
            <div style={{ display: 'inline-flex' }}>
              <UserAvatar
                user={{
                  loginName: record.get('loginName'),
                  realName: record.get('realName'),
                  imageUrl: record.get('userImageUrl'),
                }}
              // hiddenText
              />
            </div>
          )}
        />
        <Column name="memberRole" />
        <Column name="roleCode" />
        <Column name="endDate" />
      </Table>
    </div>
  );
};

export default observer(AuthList);
