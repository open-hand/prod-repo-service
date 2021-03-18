/**
* 镜像描述
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/29
* @copyright 2020 ® HAND
*/
import React, { useEffect, useCallback } from 'react';
import { Tooltip, message } from 'choerodon-ui';
import { Table, Modal, Form, TextField } from 'choerodon-ui/pro';
import { Action, axios } from '@choerodon/boot';
import { observer, useLocalStore } from 'mobx-react-lite';
import Timeago from '@/components/date-time-ago/DateTimeAgo';
import moment from 'moment';
import PullGuideModal from './PullGuideModal';
import BuildLogModal from './BuildLogModal';

const intlPrefix = 'infra.prod.lib';
const { Column } = Table;

const imgStyle = {
  width: '18px',
  height: '18px',
  borderRadius: '50%',
  flexShrink: 0,
};

const iconStyle = {
  width: '18px',
  height: '18px',
  fontSize: '13px',
  background: 'rgba(104, 135, 232, 0.2)',
  color: 'rgba(104,135,232,1)',
  borderRadius: '50%',
  lineHeight: '18px',
  textAlign: 'center',
  flexShrink: 0,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
};

const TagModal = ({ dockerImageTagDs, formatMessage, repoName, imageName, userAuth }) => {
  useEffect(() => {
    dockerImageTagDs.setQueryParameter('repoName', repoName);
    dockerImageTagDs.query();
  }, []);

  const guideInfo = useLocalStore(() => ({
    info: {},
    setGuideInfo(info) {
      this.info = info;
    },
  }));

  const buildInfo = useLocalStore(() => ({
    info: '',
    setBuildInfo(info) {
      this.info = info;
    },
  }));

  const fetchGuide = useCallback(async (data) => {
    const { tagName } = data;
    try {
      const res = await axios.get(`/rdupm/v1/harbor-guide/tag?digest=${tagName}&repoName=${repoName}`);
      guideInfo.setGuideInfo(res);
    } catch (error) {
      // message.error(error);
    }
  }, []);

  const fetchBuildLog = useCallback(async (data) => {
    const { tagName, digest } = data;
    try {
      const res = await axios.get(`/rdupm/v1/harbor-image-tag/build/log?tagName=${tagName}&repoName=${repoName}&digest=${digest}`);
      buildInfo.setBuildInfo(res);
    } catch (error) {
      // message.error(error);
    }
  }, []);

  const handleOpenGuideModal = useCallback(async (data) => {
    fetchGuide(data);
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.pullImageByTag`, defaultMessage: '版本拉取' }),
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '740px' },
      children: <PullGuideModal guideInfo={guideInfo} formatMessage={formatMessage} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  }, [fetchGuide, guideInfo]);

  const handleOpenLogModal = useCallback(async (data) => {
    await fetchBuildLog(data);
    const key = Modal.key();
    Modal.open({
      key,
      title: formatMessage({ id: `${intlPrefix}.view.buildLog`, defaultMessage: '构建日志' }),
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '740px' },
      className: 'product-lib-docker-img-tag-buildlog-modal',
      children: <BuildLogModal formatMessage={formatMessage} buildInfo={buildInfo} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  }, [fetchBuildLog, buildInfo]);


  const handleDelete = async (data) => {
    const { tagName } = data;
    const button = await Modal.confirm({
      children: (
        <p>
          {`确认删除镜像 ${imageName} ${tagName}? 如果您删除此 Tag，则这个 Tag 引用的同一个 digest 的所有其他 Tag 也将被删除`}
        </p>
      ),
    });
    if (button !== 'cancel') {
      try {
        await axios.delete(`/rdupm/v1/harbor-image-tag/delete?tagName=${tagName}&repoName=${repoName}`);
        message.success(formatMessage({ id: 'success.delete', defaultMessage: '删除成功' }));
        dockerImageTagDs.query();
      } catch (error) {
        // message.error(error);
      }
    }
  };

  const renderAction = ({ record }) => {
    const data = record.toData();
    let actionData = [];
    if (userAuth?.includes('projectAdmin')) {
      actionData = [
        {
          service: [],
          text: formatMessage({ id: `${intlPrefix}.view.pullImageByTag`, defaultMessage: '版本拉取' }),
          action: () => handleOpenGuideModal(data),
        }, {
          service: [],
          text: formatMessage({ id: `${intlPrefix}.view.buildLog`, defaultMessage: '构建日志' }),
          action: () => handleOpenLogModal(data),
        }, {
          service: ['choerodon.code.project.infra.product-lib.ps.project-owner-harbor'],
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
        }, {
          service: [],
          text: formatMessage({ id: `${intlPrefix}.view.buildLog`, defaultMessage: '构建日志' }),
          action: () => handleOpenLogModal(data),
        },
      ];
    }
    return <Action data={actionData} />;
  };

  const rendererIcon = (imageUrl, text, loginName) => {
    let iconElement;
    if (imageUrl) {
      iconElement = <img style={imgStyle} src={imageUrl} alt="" />;
    } else {
      iconElement = <div style={iconStyle}>{text[0]}</div>;
    }
    return (
      <Tooltip title={`${text}（${loginName}）`}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          {iconElement}
          <span
            style={{
              marginLeft: '7px',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
            }}
          >
            {`${text}（${loginName}）`}
          </span>
        </div>
      </Tooltip>
    );
  };

  return (
    <React.Fragment>
      <div
        className="product-lib-docker-taglist-search"
        onKeyDown={(event) => {
          if (event.keyCode === 13) {
            dockerImageTagDs.query();
          }
        }}
      >
        <Form dataSet={dockerImageTagDs.queryDataSet} >
          <TextField name="tagName" />
        </Form>
      </div>
      <Table dataSet={dockerImageTagDs} queryBar="none">
        <Column 
          name="tagName"
          renderer={({ text }) => (
            <Tooltip title={text} placement="top" >
              {text}
            </Tooltip>
          )}
        />
        <Column renderer={renderAction} width={70} />
        <Column name="sizeDesc" />
        <Column name="dockerVersion" />
        <Column name="os" renderer={({ record }) => `${record.get('os')}/${record.get('architecture')}`} />
        <Column
          name="digest"
          renderer={({ text }) =>
            (
              <Tooltip title={text} placement="top" overlayClassName="product-lib-docker-image-tag-digest">
                <div className="product-lib-docker-image-tag-digest-text">{text}</div>
              </Tooltip>
            )}
        />
        <Column
          name="realName"
          renderer={({ text, record }) => {
            const { userImageUrl, loginName } = record.toData();
            return rendererIcon(userImageUrl, text, loginName);
          }}
        />
        {/* <Column name="createTime" renderer={({ value }) => value && <Timeago date={moment(value).format('YYYY-MM-DD HH:mm:ss')} />} /> */}
        <Column name="pushTime" renderer={({ value }) => value && <Timeago date={moment(value).format('YYYY-MM-DD HH:mm:ss')} />} />
        <Column name="pullTime" renderer={({ value }) => value && <Timeago date={moment(value).format('YYYY-MM-DD HH:mm:ss')} />} />
      </Table>
    </React.Fragment>
  );
};

export default observer(TagModal);
