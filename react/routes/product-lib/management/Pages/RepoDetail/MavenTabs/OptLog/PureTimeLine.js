import React, { useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { Icon, Button } from 'choerodon-ui';
import { Spin } from 'choerodon-ui/pro';

const TimeLine = ({ isMore, operateTypeLookupData, loadData, mavenOptLogDs }) => {
  const record = mavenOptLogDs.current && mavenOptLogDs.toData();


  const getOperateTypeMeaning = useCallback((code) => {
    let icon;
    let style;
    switch (code) {
      case 'assign': // 分配
        icon = 'authority';
        break;
      case 'update': // 更新
        icon = 'rate_review1';
        style = { background: 'rgba(81,79,160,1)' };
        break;
      case 'delete':
      case 'revoke': // 回收
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
      default:
        icon = 'account_circle';
    }
    return { ...operateTypeLookupData.find(o => o.value === code), icon, style };
  }, [operateTypeLookupData]);


  // 更多操作
  function loadMoreOptsRecord() {
    loadData(mavenOptLogDs.currentPage + 1);
  }

  const getUserIcon = (imageUrl, name) => {
    if (imageUrl) {
      return <img src={imageUrl} alt="" />;
    } else {
      return <div className="prod-lib-npm-optlog-timeLine-card-content-text-div-icon">{name[0]}</div>;
    }
  };

  function renderData() {
    return record ? (
      <ul>
        {
          record.map((item, index) => {
            const { logId, operateTime, operateType, content, imgUrl } = item;
            const date = operateTime.split(' ')[0];
            const time = operateTime.split(' ')[1];
            return (
              <li key={logId}>
                <div className="prod-lib-npm-optlog-timeLine-card">
                  <div className="prod-lib-npm-optlog-timeLine-card-header">
                    <div className="prod-lib-npm-optlog-timeLine-card-header-icon">
                      <div style={{ display: 'flex' }}>
                        <Icon type={getOperateTypeMeaning(operateType).icon} style={getOperateTypeMeaning(operateType).style} />
                        <span className="prod-lib-npm-optlog-timeLine-card-header-title">{getOperateTypeMeaning(operateType).meaning}</span>
                      </div>
                    </div>
                    <div className="prod-lib-npm-optlog-timeLine-card-header-date">
                      <Icon type="date_range" />
                      <span style={{ marginLeft: '0.15rem' }}>{date}</span>
                    </div>
                  </div>
                  <div className="prod-lib-npm-optlog-timeLine-card-content">
                    <div className="prod-lib-npm-optlog-timeLine-card-content-text">
                      {getUserIcon(imgUrl, content)}
                      <p>{content}</p>
                    </div>
                    <div className="prod-lib-npm-optlog-timeLine-card-content-time"><Icon type="av_timer" /><span style={{ marginLeft: '0.15rem' }}>{time}</span></div>
                  </div>
                  {index !== record.length - 1 && <div className="prod-lib-npm-optlog-timeLine-card-line" />}
                </div>
              </li>
            );
          })
        }
      </ul>
    ) : null;
  }

  return (
    <Spin dataSet={mavenOptLogDs}>
      <div className="prod-lib-npm-optlog-timeLine">
        {
          record && record.length > 0 ? (
            <div className="prod-lib-npm-optlog-timeLine-body">
              {renderData()}
            </div>
          ) :
            (
              <div className="prod-lib-npm-optlog-timeLine-no-content">
                <span>暂无操作记录</span>
              </div>)
        }
        {isMore && <Button type="primary" onClick={loadMoreOptsRecord}>加载更多</Button>}
      </div>
    </Spin>

  );
};

export default observer(TimeLine);
