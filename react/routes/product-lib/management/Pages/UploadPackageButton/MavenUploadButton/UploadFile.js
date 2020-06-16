/**
* 制品库 包上传 上传文件
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/7
* @copyright 2020 ® HAND
*/
import React, { useState } from 'react';
import { Upload, Icon, message } from 'choerodon-ui';
import { axios, stores } from '@choerodon/boot';

const UploadFile = ({ formData, formKey, title, accept, onUpload, onRemove }) => {
  const titleStyle = {
    fontSize: '12px',
    fontWeight: 500,
    color: 'rgba(0, 0, 0, 1)',
    lineHeight: '15px',
    marginBottom: '10px',
    display: 'block',
  };

  const [fileList, setFileList] = useState([]);

  const validateFileType = (name) => accept.split(',').includes(`.${name.split('.').pop()}`);

  const beforeUpload = async (file) => {
    const { currentMenuType: { organizationId } } = stores.AppState;
    if (!validateFileType(file.name)) {
      message.error('文件格式错误');
    } else if (formKey === 'assetPom') {
      const validateFormData = new FormData();
      validateFormData.set('pomXml', file);
      const res = await axios.post(`rdupm/v1/nexus-components/${organizationId}/pom-validate`, validateFormData);
      if (res.failed) {
        return Promise.reject();
      }
      setFileList([file]);
      formData.set(formKey, file);
      // eslint-disable-next-line
      onUpload && onUpload();
    } else {
      setFileList([file]);
      formData.set(formKey, file);
      // eslint-disable-next-line
      onUpload && onUpload();
    }
    return Promise.reject();
  };

  const handleRemoveFile = () => {
    formData.delete(formKey, null);
    setFileList([]);
    // eslint-disable-next-line
    onRemove && onRemove();
  };

  return (
    <div style={{ width: '50%' }}>
      <span style={titleStyle}>{title}</span>
      <Upload
        accept={accept}
        name="file"
        fileList={fileList}
        multiple={false}
        listType="text"
        onRemove={handleRemoveFile}
        beforeUpload={beforeUpload}
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
      </Upload>
    </div>
  );
};

export default UploadFile;
