/**
* 制品库创建仓库
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import { Form, TextField, Password, Spin, SelectBox, Select } from 'choerodon-ui/pro';
// import { message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
// import useRepoList from './useRepoList';
import './index.less';

const { Option } = Select;
// const intlPrefix = 'infra.prod.lib';

const DockerCustomEditForm = ({ validateStore, dockerCustomCreateDs, modal, repoListDs, repositoryId, formatMessage }) => {
  const [loading, setLoading] = React.useState(false);

  const [originProjectShare, setOriginProjectShare] = React.useState('false');
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
    async function init() {
      const { currentMenuType: { organizationId } } = stores.AppState;
      try {
        setLoading(true);
        const res = await axios.get(`/rdupm/v1/${organizationId}/harbor-custom-repos/detail/project/${repositoryId}`);
        setOriginProjectShare(res.projectShare);
        dockerCustomCreateDs.create({
          ...res,
        });
      } finally {
        setLoading(false);
      }
    }
    init();
  }, []);

  useEffect(() => {
    modal.handleOk(async () => {
      const validate = await dockerCustomCreateDs.current.validate(true);
      if (validate) {
        const { currentMenuType: { projectId, organizationId } } = stores.AppState;
        try {
          const submitData = dockerCustomCreateDs.current.toData();
          await axios.post(`/rdupm/v1/${organizationId}/harbor-custom-repos/update/${projectId}`, submitData);
          repoListDs.query();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [dockerCustomCreateDs, modal]);

  return (
    <Spin spinning={loading}>
      <Form dataSet={dockerCustomCreateDs} columns={1}>
        <TextField name="repoName" />
        <TextField name="repoUrl" />
        <TextField name="loginName" />
        <Password name="password" />
        <TextField name="email" />
        <TextField name="description" />
        <SelectBox
          name="projectShare"
          className="prod-lib-edit-custom-docker-selectbox"
          disabled={isExistShareProject && originProjectShare !== 'true'}
        >
          <Option value="true">{formatMessage({ id: 'yes' })}</Option>
          <Option value="false">{formatMessage({ id: 'no' })}</Option>
        </SelectBox>
      </Form>
      <div className="prod-lib-test-connect-edit">
        测试连接：{validateStore.isValidate ? <span style={{ color: '#00BFA5' }}>成功</span> : <span style={{ color: 'red' }}>失败</span>}
      </div>
    </Spin>
  );
};

export default observer(DockerCustomEditForm);
