/**
* 制品库创建仓库
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useEffect, useCallback } from 'react';
import {
  Form, TextField, Select, SelectBox, Button,
} from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import { observer, useComputed } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
import classnames from 'classnames';
import {  v4 as uuidv4 }from 'uuid';
import useRepoList from './useRepoList';
import './index.less';

const intlPrefix = 'infra.prod.lib';

const { Option } = Select;

const NpmCreateForm = ({
  formatMessage, npmCreateDs, enableAnonymousFlag, modal, init,
}) => {
  const { repoList, createdRepoList, setCreatedRepoList } = useRepoList();
  useEffect(() => {
    npmCreateDs.create({
      type: 'hosted',
    });
  }, []);

  const mavenNameSuffix = useComputed(() => {
    if (!npmCreateDs.current) {
      return '';
    }
    const mavenType = npmCreateDs.current.get('type');
    if (mavenType === 'hosted') {
      return '-source';
    }
    return `-${mavenType.toLowerCase()}`;
  }, [npmCreateDs.current]);

  useEffect(() => {
    modal.handleOk(async () => {
      const validate = await npmCreateDs.current.validate(true);
      if (validate) {
        const { currentMenuType: { projectId, organizationId } } = stores.AppState;
        try {
          const submitData = npmCreateDs.current.toData();
          submitData.name = `${submitData.name}${mavenNameSuffix}`;
          if (submitData.type === 'group') {
            const repoMemberList = createdRepoList.map((o) => o.name).filter(Boolean);
            if (repoMemberList.length === 0) {
              message.error(formatMessage({ id: `${intlPrefix}.view.chooseGroupPlease`, defaultMessage: '请选择仓库' }));
              return false;
            }
            submitData.repoMemberList = repoMemberList;
          }
          await axios.post(`/rdupm/v1/nexus-repositorys/${organizationId}/project/${projectId}/npm/repo`, submitData);
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
  }, [npmCreateDs, modal, createdRepoList, mavenNameSuffix]);

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
          label={formatMessage({ id: `${intlPrefix}.view.groupMember`, defaultMessage: '组仓库成员' })}
          searchable
          style={{ width: '100%' }}
          allowClear={false}
          // eslint-disable-next-line
          onChange={val => handleSelectProject(val, list._id)}
          dropdownMenuStyle={{ maxHeight: '200px', overflowY: 'scroll' }}
        >
          {
            repoList.map((o) => {
              const hasData = createdRepoList.some((value) => o.name === value.name);
              return (
                <Option disabled={hasData} key={o.name} value={o.name}>{o.name}</Option>
              );
            })
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

  const type = useComputed(() => npmCreateDs.current && npmCreateDs.current.data.type, [npmCreateDs.current]);

  return (
    <Form dataSet={npmCreateDs} columns={1}>
      <SelectBox name="type" className={classnames('product-lib-createrepo-selectbox', 'product-lib-createrepo-selectbox-type')} />
      <TextField name="name" addonAfter={mavenNameSuffix} />
      {type === 'hosted'
        && [
          <Select
            key="writePolicy"
            name="writePolicy"
          />,
        ]}
      {type === 'proxy'
        && [
          <TextField key="remoteUrl" name="remoteUrl" />,
          <TextField key="remoteUsername" name="remoteUsername" />,
          <TextField
            key="remotePassword"
            name="remotePassword"
            renderer={({ text }) => text.replace(/./g, '•')}
          />,
        ]}
      {type === 'group'
        && (
        <div className="product-lib-pages-createtrpo-select-list">
          {renderSelectList()}
          <Button
            style={{ textAlign: 'left', marginBottom: '10px' }}
            funcType="flat"
            color="primary"
            icon="add"
            onClick={handleAddCreatedRepo}
          >
            {formatMessage({ id: `${intlPrefix}.view.addGroup`, defaultMessage: '添加组仓库成员' })}
          </Button>
        </div>
        )}
      {enableAnonymousFlag === 1
      && (
      <SelectBox name="allowAnonymous" className={classnames('product-lib-createrepo-selectbox', 'product-lib-createrepo-selectbox-type')}>
        <Option value={1}>{formatMessage({ id: 'yes', defaultMessage: '是' })}</Option>
        <Option value={0}>{formatMessage({ id: 'no', defaultMessage: '否' })}</Option>
      </SelectBox>
      )}
    </Form>
  );
};

export default observer(NpmCreateForm);
