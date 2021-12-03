/**
* 创建docker仓库
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/27
* @copyright 2020 ® HAND
*/
import React, { useEffect, useState } from 'react';
import { Icon, Tooltip } from 'choerodon-ui';
import {
  Form, TextField, SelectBox, Select, NumberField, CheckBox, DateTimePicker, Button,
} from 'choerodon-ui/pro';
import { observer, useComputed } from 'mobx-react-lite';
import classnames from 'classnames';
import uuidv4 from 'uuid';
import { axios, stores } from '@choerodon/boot';
import moment from 'moment';
import './index.less';

const { Option } = Select;

const DockerCreateForm = ({
  formatMessage, dockerCreateBasicDs, modal, init,
}) => {
  const [CVEIDList, setCVEIDList] = useState([{ _id: uuidv4() }]);
  const [showAdvanced, setShowAdvanced] = useState(false);

  useEffect(() => {
    async function initDocker() {
      const { currentMenuType: { id, name } } = stores.AppState;
      const res = await axios.get(`/iam/choerodon/v1/projects/${id}`);
      const defaultValue = await axios.get('/rdupm/v1/harbor-quota/global');
      dockerCreateBasicDs.create({
        code: res.code,
        name,
        ...defaultValue,
      });
    }
    initDocker();
  }, []);

  useEffect(() => {
    modal.handleOk(async () => {
      const validate = await dockerCreateBasicDs.current.validate(true);
      if (validate) {
        const { currentMenuType: { projectId } } = stores.AppState;
        try {
          const submitData = dockerCreateBasicDs.current.toData();
          const cveNoList = CVEIDList.map((o) => o.name).filter(Boolean);
          if (cveNoList.length > 0) {
            submitData.cveNoList = cveNoList;
          }
          await axios.post(`/rdupm/v1/harbor-project/create/${projectId}`, submitData);
          // console.time('testForEach');
          await new Promise((resolve) => setTimeout(() => resolve(), 3500));
          // console.timeEnd('testForEach');
          init();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [dockerCreateBasicDs, modal, CVEIDList]);

  const handleAdd = () => {
    setCVEIDList((prevList) => prevList.concat([{ _id: uuidv4() }]));
  };

  const handleDelete = (id) => {
    if (CVEIDList.length !== 1) {
      // eslint-disable-next-line
      setCVEIDList(prevList => prevList.filter(o => o._id !== id));
    }
  };

  const handleCVEIDChange = (val, id) => {
    // eslint-disable-next-line
    const index = CVEIDList.findIndex(o => o._id === id)
    CVEIDList[index].name = val;
    setCVEIDList([...CVEIDList]);
  };

  const renderCVEIDList = () => (
    CVEIDList.map((list, index) => (
      // eslint-disable-next-line
      <div key={list._id} style={{ display: 'flex', marginBottom: index !== CVEIDList.length - 1 ? '23px' : '10px' }}>
        <TextField
          label={formatMessage({ id: 'infra.prod.lib.model.CVE-ID', defaultMessage: 'CVE-ID' })}
          style={{ width: '100%' }}
          // eslint-disable-next-line
          onChange={val => handleCVEIDChange(val, list._id)}
        />

        <Button
          funcType="flat"
          icon="delete"
          // eslint-disable-next-line
          onClick={() => handleDelete(list._id)}
        />
      </div>
    ))
  );

  const cve = useComputed(() => dockerCreateBasicDs.current
   && dockerCreateBasicDs.current.data.cve, [dockerCreateBasicDs.current]);

  return (
    <>
      <Form dataSet={dockerCreateBasicDs} columns={1}>
        <TextField name="code" disabled />
        <TextField name="name" disabled />
      </Form>
      <div
        className="product-lib-create-docker-form-label"
        style={{ margin: '-20px 0 -16px -5px' }}
      >
        {formatMessage({ id: 'infra.prod.lib.model.publicFlag', defaultMessage: '访问级别' })}
        {dockerCreateBasicDs.getField('publicFlag').get('required') && <span className="required">*</span>}
        <Tooltip title="当镜像仓库设为公开后，任何人都有此仓库下镜像的读权限。命令行用户无需“docker login”就可以拉取镜像">
          <Icon type="help" style={{ marginLeft: '5px', marginTop: '-2px' }} />
        </Tooltip>
      </div>
      <Form dataSet={dockerCreateBasicDs} columns={1}>
        <SelectBox
          name="publicFlag"
          className={classnames('product-lib-createrepo-selectbox', 'product-lib-createrepo-selectbox-50')}
        >
          <Option value="true">
            {formatMessage({ id: 'infra.prod.lib.view.public', defaultMessage: '公开' })}
          </Option>
          <Option value="false">
            {formatMessage({ id: 'infra.prod.lib.view.private', defaultMessage: '不公开' })}
          </Option>
        </SelectBox>
      </Form>
      <Form dataSet={dockerCreateBasicDs} columns={3}>
        <NumberField name="countLimit" min={-1} step={1} />
        <NumberField name="storageNum" step={1} min={-1} />
        <Select name="storageUnit" />
      </Form>
      <div className="c7n-pro-field-help" style={{ position: 'relative', top: '-15px' }}>如果对数量无限制，则输入-1</div>
      <div className="product-lib-pages-docker-create-divider" />
      <div className="product-lib-pages-docker-create-second-title" onClick={() => setShowAdvanced((pre) => !pre)} role="none">
        {formatMessage({ id: 'infra.prod.lib.view.advancedConf', defaultMessage: '高级配置' })}
        {showAdvanced ? <Icon type="expand_less" /> : <Icon type="expand_more" />}
      </div>
      {showAdvanced
        && (
        <>
          <div className="product-lib-create-docker-form-label">
            {formatMessage({ id: 'infra.prod.lib.view.DockerRepoConf', defaultMessage: 'Docker仓库配置' })}
          </div>
          <div style={{ width: '50%', display: 'inline-block' }}>
            <CheckBox name="autoScanFlag" onChange={(val) => dockerCreateBasicDs.current.set('autoScanFlag', val)}>
              {formatMessage({ id: 'infra.prod.lib.model.autoScanFlag', defaultMessage: '自动扫描镜像' })}
            </CheckBox>
            <Tooltip title="当镜像上传后，自动进行扫描">
              <Icon type="help" />
            </Tooltip>
          </div>
          <div style={{ width: '50%', display: 'inline-block' }}>
            <CheckBox name="preventVulnerableFlag" onChange={(val) => dockerCreateBasicDs.current.set('preventVulnerableFlag', val)}>
              {formatMessage({ id: 'infra.prod.lib.model.preventVulnerableFlag', defaultMessage: '阻止潜在漏洞镜像' })}
            </CheckBox>
            <Tooltip title="阻止危害级别以上的镜像运行">
              <Icon type="help" />
            </Tooltip>
          </div>
          <Form dataSet={dockerCreateBasicDs} columns={1} style={{ marginTop: '20px' }}>
            <Select name="severity" />
          </Form>
          <div
            className="product-lib-create-docker-form-label"
            style={{ margin: '-20px 0 -16px -5px' }}
          >
            {formatMessage({ id: 'infra.prod.lib.model.model.cve', defaultMessage: 'CVE白名单' })}
            <Tooltip title="在推送和拉取镜像时，在项目的CVE白名单中的漏洞将会被忽略。
            您可以选择使用系统的CVE白名单作为该项目的白名单，也可勾选“启用项目白名单”项来建立该项目自己的CVE白名单"
            >
              <Icon type="help" style={{ marginLeft: '5px', marginTop: '-2px' }} />
            </Tooltip>
          </div>
          <Form dataSet={dockerCreateBasicDs} columns={1}>
            <SelectBox
              name="cve"
              className={classnames('product-lib-createrepo-selectbox', 'product-lib-createrepo-selectbox-50')}
              onChange={(val) => {
                if (val === 'useSysCveFlag') {
                  dockerCreateBasicDs.current.set('useSysCveFlag', 'true');
                  dockerCreateBasicDs.current.set('useProjectCveFlag', 'false');
                  dockerCreateBasicDs.current.set('endDate', undefined);
                  setCVEIDList([{ _id: uuidv4() }]);
                } else {
                  dockerCreateBasicDs.current.set('useSysCveFlag', 'false');
                  dockerCreateBasicDs.current.set('useProjectCveFlag', 'true');
                }
              }}
            >
              <Option value="useSysCveFlag">
                {formatMessage({ id: 'infra.prod.lib.model.useSysCveFlag', defaultMessage: '启用系统白名单' })}
              </Option>
              <Option value="useProjectCveFlag">
                {formatMessage({ id: 'infra.prod.lib.model.useProjectCveFlag', defaultMessage: '启用项目白名单' })}
              </Option>
            </SelectBox>
            {cve === 'useProjectCveFlag'
              && [
                <DateTimePicker key="endDate" name="endDate" min={moment()} />,

                <div key="cveList" className="product-lib-pages-createtrpo-select-list">
                  {renderCVEIDList()}
                  <Button
                    style={{ textAlign: 'left', marginBottom: '10px' }}
                    funcType="flat"
                    color="primary"
                    icon="add"
                    onClick={handleAdd}
                  >
                    {formatMessage({ id: 'infra.prod.lib.view.addCVE-ID', defaultMessage: '添加CVE-ID' })}
                  </Button>
                </div>,
              ]}
          </Form>
        </>
        )}
    </>
  );
};

export default observer(DockerCreateForm);
