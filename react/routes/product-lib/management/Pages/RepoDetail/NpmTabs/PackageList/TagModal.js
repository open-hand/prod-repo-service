/**
* 镜像描述
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/29
* @copyright 2020 ® HAND
*/
import React, { useCallback } from 'react';
import { Tooltip, message } from 'choerodon-ui';
import { Table, Modal, Form, TextField, DataSet, Icon } from 'choerodon-ui/pro';
import { Action, axios, stores } from '@choerodon/boot';
import { observer, useLocalStore } from 'mobx-react-lite';
import UserAvatar from '@/components/user-avatar';
import Timeago from '@/components/date-time-ago/DateTimeAgo';
import moment from 'moment';
import NpmGuideModal from '../../../GuideButton/NpmGuideButton/NpmGuideModal';

const intlPrefix = 'infra.prod.lib';
const { Column } = Table;

const TagModal = ({ userAuth, npmOverViewDs, repositoryId, formatMessage, repositoryName, name, enableFlag }) => {
  const { organizationId, projectId } = stores.AppState.currentMenuType;
  const npmTagDs = React.useRef(new DataSet({
    autoQuery: false,
    selection: false,
    pageSize: 10,
    transport: {
      read: () => ({
        url: `/rdupm/v1/nexus-components/${organizationId}/project/${projectId}/npm/version?repositoryId=${repositoryId}&&repositoryName=${repositoryName}&name=${name}`,
        method: 'GET',
      }),
    },
    fields: [
      { name: 'version', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.tagName`, defaultMessage: '版本号' }) },
      { name: 'name', type: 'string', label: formatMessage({ id: 'name', defaultMessage: '名称' }) },
      { name: 'creatorRealName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.author`, defaultMessage: '创建人' }) },
      { name: 'creationDate', type: 'string', label: formatMessage({ id: 'createDate', defaultMessage: '创建时间' }) },
      { name: 'downloadUrl', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.downloadUrl`, defaultMessage: '下载地址' }) },
    ],
    queryFields: [
      { name: 'version', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.tagName`, defaultMessage: '版本号' }) },
    ],
  })).current;

  React.useEffect(() => {
    npmTagDs.query();
  }, []);


  const guideInfo = useLocalStore(() => ({
    packageVersion: '',
    get info() {
      return {
        hidePush: true,
        setRegistory: `npm config set registry=${npmOverViewDs.current.get('url')}`,
        login: 'npm login',
        pull: `npm install ${guideInfo.packageVersion} --registry=${npmOverViewDs.current.get('url')}`,
      };
    },
    setPackageVersion(sPackageVersion) {
      guideInfo.packageVersion = sPackageVersion;
    },
  }));

  const handleOpenGuideModal = useCallback(async (data) => {
    const { version } = data;
    guideInfo.setPackageVersion(`${name}@${version}`);
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.configGuideTitle`, defaultMessage: '配置指引' }),
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '740px' },
      children: <NpmGuideModal guideInfo={guideInfo} formatMessage={formatMessage} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  }, [guideInfo]);


  const handleDelete = async (data) => {
    const { repository, componentIds } = data;
    const button = await Modal.confirm({
      children: (
        <div>
          <p>{formatMessage({ id: 'confirm.delete', defaultMessage: '确认删除？' })}</p>
        </div>
      ),
    });
    if (button !== 'cancel') {
      try {
        await axios.delete(`/rdupm/v1/nexus-components/${organizationId}/project/${projectId}/npm?repositoryId=${repositoryId}&&repositoryName=${repository}`, { data: componentIds });
        message.success(formatMessage({ id: 'success.delete', defaultMessage: '删除成功' }));
        npmTagDs.query();
      } catch (error) {
        // message.error(error);
      }
    }
  };

  const renderAction = ({ record }) => {
    const data = record.toData();
    let actionData = [];
    if (enableFlag === 'Y' && (userAuth.includes('projectAdmin') || userAuth.includes('developer'))) {
      actionData = [
        {
          service: [],
          text: formatMessage({ id: `${intlPrefix}.view.pullImageByTag`, defaultMessage: '版本拉取' }),
          action: () => handleOpenGuideModal(data),
        }, {
          service: [],
          text: formatMessage({ id: 'delete', defaultMessage: '删除' }),
          action: () => handleDelete(data),
        },
      ];
    } else {
      actionData = [
        {
          service: [],
          text: formatMessage({ id: `${intlPrefix}.view.pullImageByTag`, defaultMessage: '版本拉取' }),
          action: () => handleOpenGuideModal(data),
        },
      ];
    }
    return <Action data={actionData} />;
  };


  function renderName({ record }) {
    const avatar = (
      <div style={{ display: 'inline-flex' }}>
        <UserAvatar
          user={{
            loginName: record.get('creatorLoginName'),
            realName: record.get('creatorRealName'),
            imageUrl: record.get('creatorImageUrl'),
          }}
        />
      </div>
    );
    return avatar;
  }

  return (
    <React.Fragment>
      <div
        className="product-lib-npm-taglist-search"
        onKeyDown={(event) => {
          if (event.keyCode === 13) {
            npmTagDs.query();
          }
        }}
      >
        <Form dataSet={npmTagDs.queryDataSet} >
          <TextField prefix={<Icon type="search" />} name="version" />
        </Form>
      </div>
      <Table dataSet={npmTagDs} queryBar="none">
        <Column name="version" />
        <Column renderer={renderAction} width={70} />
        <Column name="name" />
        <Column name="creatorRealName" renderer={renderName} width={200} />
        <Column name="creationDate" renderer={({ value }) => value && <Timeago date={moment(value).format('YYYY-MM-DD HH:mm:ss')} />} />
        <Column
          name="downloadUrl"
          renderer={({ text }) =>
            (
              <Tooltip title={text} placement="top" overlayClassName="product-lib-npm-image-tag-digest">
                <div className="product-lib-npm-image-tag-digest-text">{text}</div>
              </Tooltip>
            )}
        />
      </Table>
    </React.Fragment>
  );
};

export default observer(TagModal);
