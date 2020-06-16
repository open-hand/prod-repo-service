import React, { useEffect, useState, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Button, Select, Spin } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import { axios, stores } from '@choerodon/boot';
import uuidv4 from 'uuid/v4';

const { Option } = Select;
const AssociateModal = ({ repositoryId, modal, refresh }) => {
  const [svcList, setSVCList] = useState([]);
  const [createdSVCList, setCreatedSVCList] = useState([{ _id: uuidv4() }]);
  const [loading, setLoading] = useState(false);
  const [detail, setDetail] = useState({});

  const init = useCallback(async () => {
    const { currentMenuType: { organizationId } } = stores.AppState;
    try {
      setLoading(true);
      const res = await axios.get(`/rdupm/v1/${organizationId}/harbor-custom-repos/detail/project/${repositoryId}`);
      setDetail(res);
    } finally {
      setLoading(false);
    }
  }, [repositoryId]);

  const fetchProject = useCallback(async () => {
    const { currentMenuType: { organizationId } } = stores.AppState;
    try {
      const res = await axios.get(`/rdupm/v1/${organizationId}/harbor-custom-repos/no-relate-service/${repositoryId}`);
      setSVCList([...res]);
    } catch (error) {
      // message.error(error);
    }
  }, [repositoryId]);

  useEffect(() => {
    init();
    fetchProject();
  }, [fetchProject]);

  useEffect(() => {
    modal.handleOk(async () => {
      const { currentMenuType: { projectId, organizationId } } = stores.AppState;
      const body = createdSVCList.map(o => o.id && o.id).filter(Boolean);
      if (body.length === 0) {
        message.error('请选择应用服务');
        return false;
      }

      await axios.post(`/rdupm/v1/${organizationId}/harbor-custom-repos/relate-service/${projectId}`, {
        ...detail,
        appServiceIds: [...new Set([...(detail.appServiceIds || []), ...body])],
      });
      refresh();
      message.success('关联成功');
    });
  }, [createdSVCList, detail]);

  const handleSelectProject = useCallback((val, uuid) => {
    // eslint-disable-next-line
    const index = createdSVCList.findIndex(o => o._id === uuid)
    createdSVCList[index].id = val;
  }, [createdSVCList]);

  const handleAddSVC = useCallback(() => {
    setCreatedSVCList(prevList => prevList.concat([{ _id: uuidv4() }]));
  }, []);

  const handleDelete = useCallback((uuid) => {
    // eslint-disable-next-line
    setCreatedSVCList(prevList => prevList.filter(o => o._id !== uuid));
  }, [createdSVCList]);

  const renderSelectList = useCallback(() => (
    createdSVCList.map(list => (
      // eslint-disable-next-line
      <div key={list._id} style={{ display: 'flex' }}>
        <Select
          label="应用服务"
          searchable
          style={{ width: '100%' }}
          allowClear={false}
          // eslint-disable-next-line
          onChange={val => handleSelectProject(val, list._id)}
          dropdownMenuStyle={{ maxHeight: '200px', overflowY: 'scroll' }}
        >
          {
            svcList.map(o => (
              <Option key={o.id} value={o.id}>{o.name}</Option>
            ))
          }
        </Select>
        <Button
          style={{ marginLeft: '10px' }}
          funcType="flat"
          icon="delete"
          // eslint-disable-next-line
          onClick={() => handleDelete(list._id)}
        />
      </div>
    ))
  ), [createdSVCList, svcList]);

  return (
    <Spin spinning={loading}>
      <Form>
        {renderSelectList()}
      </Form>
      <Button
        style={{ textAlign: 'left' }}
        funcType="flat"
        color="primary"
        icon="add"
        onClick={handleAddSVC}
      >
        添加应用服务
      </Button>
    </Spin>
  );
};

export default observer(AssociateModal);
