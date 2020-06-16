/**
* 制品库包上传
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/7
* @copyright 2020 ® HAND
*/
import React, { useEffect, useRef } from 'react';
import { message } from 'choerodon-ui';
import { Form, Select } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
import UploadFile from './UploadFile';
import { intlPrefix } from '../../../index';

const UploadPackageModal = ({ repositoryId, repositoryName, formatMessage, npmUploadPackageDs, modal, npmComponentDs }) => {
  const formData = useRef(new FormData()).current;

  useEffect(() => {
    npmUploadPackageDs.create({
      repositoryName,
    });
  }, []);

  useEffect(() => {
    modal.handleOk(async () => {
      const { currentMenuType: { projectId, organizationId } } = stores.AppState;
      if (await npmUploadPackageDs.current.validate(true)) {
        try {
          const submitData = npmUploadPackageDs.current.toData();
          Object.entries(submitData).forEach(o => {
            formData.set(o[0], o[1]);
          });
          formData.set('repositoryId', repositoryId);
          if (!formData.get('assetTgz')) {
            message.error('请上传文件');
            return false;
          }
          await axios.post(`/rdupm/v1/nexus-components/${organizationId}/project/${projectId}/npm/upload`, formData);
          await new Promise(resolve => setTimeout(() => resolve(), 1000));
          npmComponentDs.query();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [npmUploadPackageDs, modal]);

  return (
    <Form dataSet={npmUploadPackageDs} columns={1}>
      <Select name="repositoryName" disabled />
      <div style={{ display: 'flex' }}>
        <UploadFile
          formData={formData}
          formKey="assetTgz"
          title={formatMessage({ id: `${intlPrefix}.view.upload-tgz`, defaultMessage: '上传tgz文件' })}
          accept=".tgz,.tar.gz"
        />
      </div>
    </Form>
  );
};

export default observer(UploadPackageModal);
