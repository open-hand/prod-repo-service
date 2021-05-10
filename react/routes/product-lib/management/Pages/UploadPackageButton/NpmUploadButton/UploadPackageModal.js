/**
* 制品库包上传
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/7
* @copyright 2020 ® HAND
*/
import React, { useEffect, useRef, useState } from 'react';
import { Icon, message } from 'choerodon-ui';
import { Form, Select } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
import { ChunkUploader } from '@choerodon/components';

const UploadPackageModal = observer(({ repositoryId, repositoryName, npmUploadPackageDs, modal, npmComponentDs }) => {
  const formData = useRef(new FormData()).current;

  const [uploading, setUploading] = useState(false);

  const [tgzstr, setTgzstr] = useState('');

  useEffect(() => {
    npmUploadPackageDs.create({
      repositoryName,
    });
  }, []);

  useEffect(() => {
    modal.update({
      okProps: { disabled: uploading },
      cancelProps: { disabled: uploading },
    });
  }, [uploading]);

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
          if (tgzstr) {
            formData.set('filePath', tgzstr);
          }
          if (!formData.get('filePath')) {
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
      <div>
        <p style={{
          fontSize: '12px',
          fontWeight: 500,
          color: 'rgba(0, 0, 0, 1)',
          lineHeight: '15px',
          marginBottom: '10px',
          display: 'block',
        }}
        >
          上传tgz文件
        </p>
        <ChunkUploader
          callbackWhenLoadingChange={(loading) => {
            setUploading(loading);
          }}
          suffix=".tgz"
          accept=".tgz"
          prefixPatch="/rdupm"
          showUploadList
          callback={(str) => {
            setTgzstr(str);
          }}
        >
          <div
            className="c7n-upload c7n-upload-select c7n-upload-select-picture-card"
            style={{
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'center',
            }}
          >
            <Icon type="add" />
            <div className="c7n-upload-text">Upload</div>
          </div>
        </ChunkUploader>
        {/* <UploadFile */}
        {/*  formData={formData} */}
        {/*  formKey="assetTgz" */}
        {/*  title={formatMessage({ id: `${intlPrefix}.view.upload-tgz`, defaultMessage: '上传tgz文件' })} */}
        {/*  accept=".tgz,.tar.gz" */}
        {/* /> */}
      </div>
    </Form>
  );
});

export default UploadPackageModal;
