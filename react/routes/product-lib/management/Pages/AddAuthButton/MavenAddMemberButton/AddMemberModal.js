/**
* 权限添加角色
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/30
* @copyright 2020 ® HAND
*/
import React, { useEffect, useRef } from 'react';
import { Form, DateTimePicker, DataSet, Button, Select } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
import moment from 'moment';
import { useNoCacheSelectData } from '@/utils';

const intlPrefix = 'infra.prod.lib';

const AddMemberModal = ({ repositoryId, formatMessage, modal, publishAuthDs }) => {
  const { currentMenuType: { projectId } } = stores.AppState;
  const memberDataSource = useNoCacheSelectData({ lookupUrl: `/rdupm/v1/harbor-auths/list-project-member/${projectId}` });

  const authDataSource = useNoCacheSelectData({ lookupCode: 'RDUPM.NEXUS_ROLE' });

  const ds = useRef(new DataSet({
    autoCreate: true,
    fields: [
      {
        name: 'userId',
        type: 'string',
        required: true,
        label: formatMessage({ id: `${intlPrefix}.model.userId`, defaultMessage: '成员' }),
        // lookupUrl: `/rdupm/v1/harbor-auths/list-project-member/${projectId}`,
        // textField: 'realName',
        // valueField: 'id',
      },
      {
        name: 'roleCode',
        type: 'string',
        label: formatMessage({ id: `${intlPrefix}.model.roleCode`, defaultMessage: '权限' }),
        required: true,
        // lookupCode: 'RDUPM.NEXUS_ROLE',
      },
      {
        name: 'endDate',
        type: 'dateTime',
        label: formatMessage({ id: `${intlPrefix}.model.endDate01`, defaultMessage: '过期时间' }),
        required: true,
      },
    ],
  })).current;

  useEffect(() => {
    modal.handleOk(async () => {
      const validate = await ds.validate();
      if (validate) {
        try {
          const submitData = ds.toData();
          await axios.post(`/rdupm/v1/nexus-auths/${projectId}/create`, submitData.map(o => ({
            ...o,
            repositoryId,
          })));
          publishAuthDs.query();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [modal]);

  return (
    <React.Fragment>
      {ds.data.map((record) =>
        (
          <Form key={record.id} columns={4} record={record}>
            <Select name="userId" dropdownMenuStyle={{ maxHeight: '200px', overflowY: 'scroll' }} searchable>
              {memberDataSource.map(o => (
                <Select.Option key={o.id} value={o.id}>{o.realName}</Select.Option>
              ))}
            </Select>
            <Select name="roleCode">
              {authDataSource.map(o => (
                <Select.Option key={o.value} value={o.value}>{o.meaning}</Select.Option>
              ))}
            </Select>
            <DateTimePicker name="endDate" min={moment()} />
            <Button
              funcType="flat"
              icon="delete"
              onClick={() => {
                if (ds.records.length > 1) {
                  ds.remove([record]);
                }
              }}
            />
          </Form>
        ))
      }
      <Button
        style={{ textAlign: 'left', marginBottom: '10px' }}
        funcType="flat"
        color="primary"
        icon="add"
        onClick={() => ds.create()}
      >
        {formatMessage({ id: `${intlPrefix}.view.addMember`, defaultMessage: '添加成员' })}
      </Button>
      <div className="c7n-pro-field-help">
        权限角色可拥有的权限如下<br />
        1.仓库管理员：push包、pull包、删除包、修改仓库配置(且用户同时是项目所有者)、查看日志<br />
        2.开发人员：push包、pull包、删除包<br />
        3.访客：pull包
      </div>
    </React.Fragment>
  );
};

export default observer(AddMemberModal);
