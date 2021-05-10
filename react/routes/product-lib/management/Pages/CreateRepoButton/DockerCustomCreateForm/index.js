/**
* 制品库创建仓库
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import { Form, TextField, Select, Password, SelectBox } from 'choerodon-ui/pro';
import { Tooltip, Icon } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
import './index.less';

// const intlPrefix = 'infra.prod.lib';

const { Option } = Select;


const DockerCustomCreateForm = ({ validateStore, dockerCustomCreateDs, formatMessage, modal, init }) => {
  const [isExistShareProject, setIsExistShareProject] = React.useState(true);
  useEffect(() => {
    async function fetchIsExist() {
      const { currentMenuType: { projectId, organizationId } } = stores.AppState;
      const res = await axios.get(`/rdupm/v1/${organizationId}/harbor-custom-repos/exist-share/${projectId}`);
      setIsExistShareProject(res);
    }
    fetchIsExist();
  }, []);

  useEffect(() => {
    dockerCustomCreateDs.create();
  }, []);

  useEffect(() => {
    modal.handleOk(async () => {
      const validate = await dockerCustomCreateDs.current.validate(true);
      if (validate) {
        const { currentMenuType: { projectId, organizationId } } = stores.AppState;
        try {
          const submitData = dockerCustomCreateDs.current.toData();
          await axios.post(`/rdupm/v1/${organizationId}/harbor-custom-repos/create/${projectId}`, submitData);
          init();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [dockerCustomCreateDs, modal]);

  useEffect(() => {
    modal.handleCancel(() => {
      validateStore.setIsValidate(undefined);
    });
  }, [modal, validateStore]);

  return (
    <React.Fragment>
      <Form dataSet={dockerCustomCreateDs} columns={1}>
        <TextField name="repoName" />
        <TextField name="repoUrl" />
        <TextField name="loginName" />
        <Password name="password" />
        <TextField name="email" />
        <TextField name="description" />
        <SelectBox name="projectShare" className="prod-lib-create-custom-docker-selectbox" disabled={isExistShareProject}>
          <Option value="true">
            {formatMessage({ id: 'yes' })}
            <Tooltip title="关联默认仓库的应用服务将会自动关联到此共享仓库">
              <Icon
                type="help"
                style={{
                  marginLeft: '2px',
                  fontSize: '16px',
                  marginBottom: '3px',
                  color: 'rgba(0, 0, 0, 0.6)',
                }}
              />
            </Tooltip>
          </Option>
          <Option value="false">{formatMessage({ id: 'no' })}</Option>
        </SelectBox>
      </Form>
      <div className="prod-lib-test-connect">
        测试连接：
        {validateStore.isValidate === true && <span style={{ color: '#00BFA5' }}>成功</span>}
        {validateStore.isValidate === false && <span style={{ color: 'red' }}>失败</span>}
        {validateStore.isValidate === undefined && <span style={{ color: 'red' }}>未测试</span>}
      </div>
    </React.Fragment >
  );
};

export default observer(DockerCustomCreateForm);
