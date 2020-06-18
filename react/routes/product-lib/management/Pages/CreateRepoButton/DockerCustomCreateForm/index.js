/**
* 制品库创建仓库
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect, useCallback } from 'react';
import { Form, TextField, Select, Button, Password, SelectBox } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
import uuidv4 from 'uuid/v4';
import useRepoList from './useRepoList';
import './index.less';

const intlPrefix = 'infra.prod.lib';

const { Option } = Select;


const DockerCustomCreateForm = ({ validateStore, dockerCustomCreateDs, formatMessage, modal, init }) => {
  const { repoList, createdRepoList, setCreatedRepoList } = useRepoList();

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
          if (submitData.projectShare === 'false') {
            const appServiceIds = createdRepoList.map(o => o.id).filter(Boolean);
            if (appServiceIds.length === 0) {
              // eslint-disable-next-line
              message.error(formatMessage({ id: `${intlPrefix}.view.chooseAppService`, defaultMessage: '请选择应用服务' }));
              return false;
            }
            submitData.appServiceIds = appServiceIds;
          }

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
  }, [dockerCustomCreateDs, modal, createdRepoList]);

  useEffect(() => {
    modal.handleCancel(() => {
      validateStore.setIsValidate(undefined);
    });
  }, [modal, validateStore]);

  const handleSelectProject = useCallback((val, id) => {
    // eslint-disable-next-line
    const index = createdRepoList.findIndex(o => o._id === id)
    createdRepoList[index].id = val;
  }, [createdRepoList]);

  const handleAddCreatedRepo = useCallback(() => {
    setCreatedRepoList(prevList => prevList.concat([{ _id: uuidv4() }]));
  }, []);

  const handleDelete = useCallback((id) => {
    if (createdRepoList.length !== 1) {
      // eslint-disable-next-line
      setCreatedRepoList(prevList => prevList.filter(o => o._id !== id));
    }
  }, [createdRepoList]);

  const renderSelectList = useCallback(() => (
    createdRepoList.map((list, index) => (
      // eslint-disable-next-line
      <div key={list._id} style={{ display: 'flex', marginBottom: index !== createdRepoList.length - 1 ? '23px' : '10px' }}>
        <Select
          label={formatMessage({ id: 'infra.service', defaultMessage: '应用服务' })}
          searchable
          style={{ width: '100%' }}
          allowClear={false}
          // eslint-disable-next-line
          onChange={val => handleSelectProject(val, list._id)}
          dropdownMenuStyle={{ maxHeight: '200px', overflowY: 'scroll' }}
        >
          {
            repoList.map(o => (
              <Option key={o.id} value={o.id}>{o.name}</Option>
            ))
          }
        </Select>
        <Button
          funcType="flat"
          icon="delete"
          // eslint-disable-next-line
          onClick={() => handleDelete(list._id)}
        />
      </div>
    ))
  ), [createdRepoList, repoList]);

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
          <Option value="true">{formatMessage({ id: 'yes' })}</Option>
          <Option value="false">{formatMessage({ id: 'no' })}</Option>
        </SelectBox>

        {dockerCustomCreateDs.current && dockerCustomCreateDs.current.get('projectShare') === 'false' &&
          <div className="product-lib-pages-createtrpo-select-list">
            {renderSelectList()}
            <Button
              style={{ textAlign: 'left', marginBottom: '10px' }}
              funcType="flat"
              color="primary"
              icon="add"
              onClick={handleAddCreatedRepo}
            >
              {formatMessage({ id: `${intlPrefix}.view.addAppService`, defaultMessage: '添加应用服务' })}
            </Button>
          </div>
        }
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
