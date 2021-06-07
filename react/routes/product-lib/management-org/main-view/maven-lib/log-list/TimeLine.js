import React, { useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import uuidv4 from 'uuid/v4';
import moment from 'moment';
import { Icon, Button } from 'choerodon-ui';
import { Spin } from 'choerodon-ui/pro';
import UserAvatar from '@/components/user-avatar';

const TimeLine = ({ formatMessage, isMore, opEventTypeLookupData, loadData, logListDs }) => {
  const record = logListDs.current && logListDs.toData();

  const getOpEventTypeMeaning = useCallback((code) => {
    let icon;
    let style;
    switch (code) {
      case 'create':
        icon = 'authority';
        break;
      case 'update':
        icon = 'rate_review1';
        style = { background: 'rgba(81,79,160,1)' };
        break;
      case 'revoke':
      case 'delete':
        icon = 'delete';
        style = { background: 'rgba(244,133,144,1)' };
        break;
      case 'pull': // 拉取
        icon = 'get_app';
        style = { background: 'rgba(81, 79, 160, 1)' };
        break;
      case 'push': // 推送
        icon = 'file_upload';
        style = { background: 'rgba(104, 135, 232, 1)' };
        break;
      case 'assign':
        icon = 'authority';
        break;
      default:
        icon = 'account_circle';
    }
    return { ...opEventTypeLookupData.find(o => o.value === code), icon, style };
  }, [opEventTypeLookupData]);

  // 更多操作
  function loadMoreOptsRecord() {
    loadData(logListDs.currentPage + 1);
  }

  const getUserIcon = (imageUrl, name = '') => {
    if (imageUrl) {
      return <img src={imageUrl} alt="" />;
    } else {
      return <div className="product-lib-org-management-log-timeLine-card-content-text-div-icon">{name[0]}</div>;
    }
  };

  function renderData() {
    return record ? (
      <ul>
        {
          record.map((item, index) => {
            const { projectImageUrl, projectCode, projectName, repoName, operateType, userImageUrl, content, operateTime } = item;
            const [date, time] = moment(operateTime).format('YYYY-MM-DD HH:mm:ss').split(' ');
            return (
              <li key={uuidv4()}>
                <div className="product-lib-org-management-log-timeLine-card">
                  <div className="product-lib-org-management-log-timeLine-card-header">
                    <div className="product-lib-org-management-log-timeLine-card-header-icon">
                      <Icon type={getOpEventTypeMeaning(operateType).icon} style={getOpEventTypeMeaning(operateType).style} />
                      <span className="product-lib-org-management-log-timeLine-card-header-title">{getOpEventTypeMeaning(operateType).meaning}</span>
                      <div style={{ display: 'inline-flex', marginLeft: '0.32rem', color: 'var(--text-color3) !important' }}>
                        <UserAvatar
                          user={{
                            loginName: projectCode || repoName,
                            realName: projectName || repoName,
                            imageUrl: projectImageUrl,
                          }}
                          size="0.18rem"
                          hiddenText
                          showToolTip={false}
                        />
                        {projectName || repoName}
                      </div>
                    </div>
                    <div className="product-lib-org-management-log-timeLine-card-header-date">
                      <Icon type="date_range" />
                      <span style={{ marginLeft: '0.12rem' }}>{date}</span>
                    </div>
                  </div>
                  <div className="product-lib-org-management-log-timeLine-split-line" />
                  <div className="product-lib-org-management-log-timeLine-card-content">
                    <div className="product-lib-org-management-log-timeLine-card-content-text">
                      {getUserIcon(userImageUrl, content)}
                      <p>{content}</p>
                    </div>
                    <div className="product-lib-org-management-log-timeLine-card-content-time"><Icon type="av_timer" /><span style={{ marginLeft: '0.15rem' }}>{time}</span></div>
                  </div>
                  {index !== record.length - 1 && <div className="product-lib-org-management-log-timeLine-card-line" />}
                </div>
              </li>
            );
          })
        }
      </ul>
    ) : null;
  }

  return (

    <Spin dataSet={logListDs}>
      <div className="product-lib-org-management-log-timeLine">
        {
          record && record.length > 0 ? (
            <div className="product-lib-org-management-log-timeLine-body">
              {renderData()}
            </div>
          ) : (
              // eslint-disable-next-line react/jsx-indent
              <div className="product-lib-org-management-log-timeLine-no-content">
                <div className="product-lib-org-management-log-timeLine-card-no-content">
                  {formatMessage({ id: 'infra.docManage.message.noOperationLog' })}
                </div>
              </div>
            )
        }
        {isMore && <Button type="primary" onClick={loadMoreOptsRecord}>{formatMessage({ id: 'infra.codelib.audit.view.loadMore' })}</Button>}
      </div>
    </Spin>

  );
};

export default observer(TimeLine);
