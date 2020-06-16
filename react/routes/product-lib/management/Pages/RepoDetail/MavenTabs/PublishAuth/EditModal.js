/**
* 修改权限
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/30
* @copyright 2020 ® HAND
*/
import React, { useEffect, useRef } from 'react';
import { axios } from '@choerodon/boot';
import { Form, TextField, DataSet, DateTimePicker, Select } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import moment from 'moment';

const intlPrefix = 'infra.prod.lib';

const EditModal = ({ formatMessage, publishAuthDs, data, modal }) => {
  const ds = useRef(new DataSet({
    data: [data],
    fields: [
      { name: 'realName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.realName`, defaultMessage: '用户姓名' }) },
      {
        name: 'roleCode',
        type: 'string',
        label: formatMessage({ id: `${intlPrefix}.model.harborRoleName`, defaultMessage: '权限角色' }),
        lookupCode: 'RDUPM.NEXUS_ROLE',
        required: true,
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
      const validate = await ds.current.validate(true);
      if (validate) {
        try {
          const submitData = ds.current.toData();
          await axios.put('/rdupm/v1/nexus-auths', submitData);
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
    <Form dataSet={ds} >
      <TextField name="realName" disabled />
      <Select name="roleCode" />
      <DateTimePicker name="endDate" min={moment()} />
    </Form>
  );
};

export default observer(EditModal);
