import React, { useEffect } from 'react';
import { Form, TextField, Select, Password, SelectBox } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
import './index.less';

// const intlPrefix = 'infra.prod.lib';

const { Option } = Select;


const CreateModal = ({ createDs, formatMessage, modal, init }) => {
  useEffect(() => {
    createDs.create();
  }, []);

  useEffect(() => {
    modal.handleOk(async () => {
      const validate = await createDs.current.validate(true);
      if (validate) {
        const { currentMenuType: { projectId, organizationId } } = stores.AppState;
        try {
          const submitData = createDs.current.toData();
          await axios.post(`/rdupm/v1/${organizationId}/nexus-server-configs/project/${projectId}`, submitData);
          init();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [createDs, modal]);

  return (
    <React.Fragment>
      <Form dataSet={createDs} columns={1}>
        <TextField name="serverName" />
        <TextField name="serverUrl" />
        <TextField name="userName" />
        <Password name="password" />
        <SelectBox name="enableAnonymousFlag" className="prod-lib-create-custom-nexus-selectbox">
          <Option value={1}>{formatMessage({ id: 'yes' })}</Option>
          <Option value={0}>{formatMessage({ id: 'no' })}</Option>
        </SelectBox>
        {createDs.current && createDs.current.get('enableAnonymousFlag') === 1 &&
          [
            <TextField key="anonymous" name="anonymous" />,
            <TextField key="anonymousRole" name="anonymousRole" />,
          ]
        }
      </Form>
    </React.Fragment >
  );
};

export default observer(CreateModal);
