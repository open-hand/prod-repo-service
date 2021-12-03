/**
* 制品库关联仓库
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/2
* @copyright 2020 ® HAND
*/
import React, { useEffect, useCallback } from 'react';
import {
  Form, TextField, Select, Button, Password,
} from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
import uuidv4 from 'uuid';
import useRepoList from './useRepoList';
import './index.less';

const { Option } = Select;
const intlPrefix = 'infra.prod.lib';
const NpmAssociateForm = ({
  formatMessage, npmAssociateDs, modal, init,
}) => {
  const { repoList, createdRepoList, setCreatedRepoList } = useRepoList();

  useEffect(() => {
    npmAssociateDs.create({});
  }, []);

  useEffect(() => {
    modal.handleOk(async () => {
      const validate = await npmAssociateDs.current.validate(true);
      if (validate) {
        const { currentMenuType: { projectId, organizationId } } = stores.AppState;
        try {
          const submitData = npmAssociateDs.current.toData();
          const repositoryList = createdRepoList.map((o) => o.name).filter(Boolean);
          if (repositoryList.length === 0) {
            message.error(formatMessage({ id: `${intlPrefix}.view.chooseGroupPlease`, defaultMessage: '请选择仓库' }));
            return false;
          }
          submitData.repositoryList = repositoryList;
          await axios.post(`/rdupm/v1/nexus-repositorys/${organizationId}/project/${projectId}/npm/repo/related`, submitData);
          await new Promise((resolve) => setTimeout(() => resolve(), 1000));
          init();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [npmAssociateDs, modal, createdRepoList]);

  const handleSelectProject = useCallback((val, id) => {
    // eslint-disable-next-line
    const index = createdRepoList.findIndex(o => o._id === id)
    createdRepoList[index].name = val;
  }, [createdRepoList]);

  const handleAddCreatedRepo = useCallback(() => {
    setCreatedRepoList((prevList) => prevList.concat([{ _id: uuidv4() }]));
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
          label={formatMessage({ id: `${intlPrefix}.view.associateRepo`, defaultMessage: '关联仓库' })}
          searchable
          style={{ width: '100%' }}
          allowClear={false}
          // eslint-disable-next-line
          onChange={val => handleSelectProject(val, list._id)}
          dropdownMenuStyle={{ maxHeight: '200px', overflowY: 'scroll' }}
        >
          {
            repoList.map((o) => (
              <Option key={o.name} value={o.name}>{o.name}</Option>
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
    <>
      <input name="username" type="text" style={{ display: 'none' }} />
      <input name="password" type="password" style={{ display: 'none' }} />
      <Form dataSet={npmAssociateDs} columns={1}>
        <div className="product-lib-pages-associate-select-list">
          {renderSelectList()}
          <Button
            style={{ textAlign: 'left', marginBottom: '10px' }}
            funcType="flat"
            color="primary"
            icon="add"
            onClick={handleAddCreatedRepo}
          >
            {formatMessage({ id: `${intlPrefix}.view.addAssociateRepo`, defaultMessage: '添加关联仓库' })}
          </Button>
        </div>
      </Form>
      <div className="product-lib-pages-associate-divider" />
      <span className="product-lib-pages-associate-second-title">{formatMessage({ id: `${intlPrefix}.view.publishAccount`, defaultMessage: '管理员用户' })}</span>
      <Form dataSet={npmAssociateDs} columns={1}>
        <TextField name="userName" autoComplete="off" />
        <Password name="password" autoComplete="off" />
      </Form>
    </>
  );
};

export default observer(NpmAssociateForm);
