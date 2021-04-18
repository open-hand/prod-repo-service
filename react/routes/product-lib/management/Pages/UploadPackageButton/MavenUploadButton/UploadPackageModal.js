/**
* 制品库包上传
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/7
* @copyright 2020 ® HAND
*/
import React, { useEffect, useRef, useState } from 'react';
import { Icon, message } from 'choerodon-ui';
import { ChunkUploader } from '@choerodon/components';
import { Form, TextField, Select } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios, stores } from '@choerodon/boot';
import UploadFile from './UploadFile';
import { intlPrefix } from '../../../index';

const UploadPackageModal = ({ repositoryId, repositoryName, formatMessage, mavenUploadPackageDs, modal, nexusComponentDs }) => {
  const formData = useRef(new FormData()).current;
  const [isUploadPOM, setISUploadPOM] = useState(false);

  const [uploading, setUploading] = useState(false);

  const [jarstr, setJarstr] = useState('');

  useEffect(() => {
    mavenUploadPackageDs.create({
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
      if (await mavenUploadPackageDs.current.validate(true)) {
        try {
          const submitData = mavenUploadPackageDs.current.toData();
          Object.entries(submitData).forEach(o => {
            formData.set(o[0], o[1]);
          });
          formData.set('repositoryId', repositoryId);
          if (jarstr) {
            formData.set('filePath', jarstr);
          }
          if (!formData.get('filePath') && !formData.get('assetPom')) {
            message.error(formatMessage({ id: `${intlPrefix}.view.upload-one-file-atlast`, defaultMessage: '至少上传一个文件' }));
            return false;
          }
          await axios.post(`/rdupm/v1/nexus-components/${organizationId}/project/${projectId}/upload`, formData);
          await new Promise(resolve => setTimeout(() => resolve(), 1000));
          nexusComponentDs.query();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [mavenUploadPackageDs, modal, jarstr]);

  return (
    <Form dataSet={mavenUploadPackageDs} columns={1}>
      <Select name="repositoryName" disabled />
      <TextField name="version" disabled={isUploadPOM} />
      <TextField name="groupId" disabled={isUploadPOM} />
      <TextField name="artifactId" disabled={isUploadPOM} />
      <div style={{ display: 'flex' }}>
        <div style={{ width: '50%' }}>
          <p style={{
            fontSize: '12px',
            fontWeight: 500,
            color: 'rgba(0, 0, 0, 1)',
            lineHeight: '15px',
            marginBottom: '10px',
            display: 'block',
          }}
          >
            上传jar文件
          </p>
          <ChunkUploader
            callbackWhenLoadingChange={(loading) => {
              setUploading(loading);
            }}
            suffix=".jar"
            accept=".jar"
            prefixPatch="/rdupm"
            showUploadList
            callback={(str) => {
              setISUploadPOM(false);
              setJarstr(str);
              mavenUploadPackageDs.getField('version').set('required', true);
              mavenUploadPackageDs.getField('version').set('pattern', /^[.A-Za-z0-9_-]+$/);
              mavenUploadPackageDs.getField('groupId').set('required', true);
              mavenUploadPackageDs.getField('groupId').set('pattern', /^[.A-Za-z0-9_-]+$/);
              mavenUploadPackageDs.getField('artifactId').set('required', true);
              mavenUploadPackageDs.getField('artifactId').set('pattern', /^[.A-Za-z0-9_-]+$/);
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
        </div>

        {/* <UploadFile */}
        {/* formData={formData} */}
        {/* formKey="assetJar" */}
        {/* title={formatMessage({ id: `${intlPrefix}.view.upload-jar`, defaultMessage: '上传jar文件' })} */}
        {/* accept=".jar" */}
        {/* onUpload={() => { */}
        {/*   setISUploadPOM(false); */}
        {/*   mavenUploadPackageDs.getField('version').set('required', true); */}
        {/*   mavenUploadPackageDs.getField('version').set('pattern', /^[.A-Za-z0-9_-]+$/); */}
        {/*   mavenUploadPackageDs.getField('groupId').set('required', true); */}
        {/*   mavenUploadPackageDs.getField('groupId').set('pattern', /^[.A-Za-z0-9_-]+$/); */}
        {/*   mavenUploadPackageDs.getField('artifactId').set('required', true); */}
        {/*   mavenUploadPackageDs.getField('artifactId').set('pattern', /^[.A-Za-z0-9_-]+$/); */}
        {/* }} */}
        {/* /> */}
        <UploadFile
          formData={formData}
          formKey="assetPom"
          title={formatMessage({ id: `${intlPrefix}.view.upload-pom`, defaultMessage: '上传pom文件' })}
          accept=".xml"
          onUpload={() => {
            setISUploadPOM(true);
            mavenUploadPackageDs.getField('version').set('required', false);
            mavenUploadPackageDs.getField('version').set('pattern', /.*/);
            mavenUploadPackageDs.current.set('version', '');
            mavenUploadPackageDs.getField('groupId').set('required', false);
            mavenUploadPackageDs.getField('groupId').set('pattern', /.*/);
            mavenUploadPackageDs.current.set('groupId', '');
            mavenUploadPackageDs.getField('artifactId').set('required', false);
            mavenUploadPackageDs.getField('artifactId').set('pattern', /.*/);
            mavenUploadPackageDs.current.set('artifactId', '');
          }}
          onRemove={() => {
            setISUploadPOM(false);
            mavenUploadPackageDs.getField('version').set('required', true);
            mavenUploadPackageDs.getField('version').set('pattern', /^[.A-Za-z0-9_-]+$/);
            mavenUploadPackageDs.getField('groupId').set('required', true);
            mavenUploadPackageDs.getField('groupId').set('pattern', /^[.A-Za-z0-9_-]+$/);
            mavenUploadPackageDs.getField('artifactId').set('required', true);
            mavenUploadPackageDs.getField('artifactId').set('pattern', /^[.A-Za-z0-9_-]+$/);
          }}
        />
      </div>
    </Form>
  );
};

export default observer(UploadPackageModal);
