/**
* 制品库创建仓库
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/2
* @copyright 2020 ® HAND
*/
import React, { useState, useEffect, useCallback } from 'react';
import {
  Form, TextField, Select, Password, SelectBox, Stores, Button,
} from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import { observer, useComputed } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
import classnames from 'classnames';
import {  v4 as uuidv4 }from 'uuid';
import useRepoList from './useRepoList';
import { intlPrefix } from '../../../index';

const { Option } = Select;

const MavenEditModal = ({
  formatMessage, mavenCreateDs, modal, repoListDs, originData, enableAnonymousFlag,
}) => {
  const [typeLookupData, setTypeLookupData] = useState([]);

  const { repoList, createdRepoList, setCreatedRepoList } = useRepoList();

  useEffect(() => {
    mavenCreateDs.data = [originData];
    if (originData.type === 'group') {
      const originRepoList = originData.repoMemberList.map((o) => ({
        name: o,
        _id: uuidv4(),
      }));
      setCreatedRepoList(originRepoList);
    }
  }, []);

  useEffect(() => {
    async function getTypeLookup() {
      const lookupData = await Stores.LookupCodeStore.fetchLookupData('/hpfm/v1/lovs/value?lovCode=RDUPM.MAVEN_REPOSITORY_TYPE');
      setTypeLookupData(lookupData);
    }
    getTypeLookup();
  }, []);

  useEffect(() => {
    modal.handleOk(async () => {
      const validate = await mavenCreateDs.current.validate(true);
      if (validate) {
        const { currentMenuType: { projectId, organizationId } } = stores.AppState;
        try {
          const submitData = mavenCreateDs.current.toData();
          if (submitData.type === 'group') {
            const repoMemberList = createdRepoList.map((o) => o.name).filter(Boolean);
            if (repoMemberList.length === 0) {
              message.error(formatMessage({ id: `${intlPrefix}.view.chooseGroupPlease`, defaultMessage: '请选择仓库' }));
              return false;
            }
            submitData.repoMemberList = repoMemberList;
          }
          await axios.put(`/rdupm/v1/nexus-repositorys/${organizationId}/project/${projectId}/maven/repo/${submitData.repositoryId}`, submitData);
          repoListDs.query();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [mavenCreateDs, modal, createdRepoList]);

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
          value={list.name}
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

  const type = useComputed(() => mavenCreateDs.current && mavenCreateDs.current.data.type, [mavenCreateDs.current]);

  return (
    <Form dataSet={mavenCreateDs} columns={1}>
      <SelectBox disabled name="type" className={classnames('product-lib-createrepo-selectbox', 'product-lib-createrepo-selectbox-type')}>
        {typeLookupData.map((o) => (
          <Option key={o.value} value={o.value}>{o.meaning}</Option>
        ))}
      </SelectBox>
      <TextField name="name" disabled />
      {type === 'hosted'
        && [
          <Select key="versionPolicy" name="versionPolicy" disabled />,
          <Select
            key="writePolicy"
            name="writePolicy"
            optionsFilter={(record) => {
              if (mavenCreateDs.current.get('versionPolicy') === 'SNAPSHOT' && record.get('value') === 'ALLOW_ONCE') {
                return false;
              }
              return true;
            }}
          />,
        ]}
      {type === 'proxy'
        && [
          <Select key="versionPolicy" name="versionPolicy" disabled />,
          <TextField key="remoteUrl" name="remoteUrl" />,
          <TextField key="remoteUsername" name="remoteUsername" />,
          <Password key="remotePassword" name="remotePassword" />,
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

export default observer(MavenEditModal);
