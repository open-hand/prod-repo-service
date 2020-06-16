/**
* 制品库配置指引
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React, { useCallback } from 'react';
import { Modal } from 'choerodon-ui/pro';
import { observer, useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';
import GuideModal from './GuideModal';
import { intlPrefix } from '../../../../../index';

const GuideButton = ({ repositoryId, formatMessage, text, record }) => {
  const guideInfo = useLocalStore(() => ({
    info: {},
    setGuideInfo(oGuideInfo) {
      this.info = oGuideInfo;
    },
  }));

  const fetchGuide = useCallback(async () => {
    const { repository, group, name, version, extension } = record;
    try {
      const res = await axios.get('/rdupm/v1/nexus-components/guide', {
        params: { repositoryId, repository, group, name, version, extension },
      });
      guideInfo.setGuideInfo(res);
    } catch (error) {
      // message.error(error);
    }
  }, [record]);

  const handleOpenGrantAuthModal = useCallback(async () => {
    fetchGuide();
    const { name } = record;
    const key = Modal.key();
    Modal.open({
      key,
      title:
        formatMessage(
          {
            id: `${intlPrefix}.view.configGuide`,
            defaultMessage: `${name}配置指引`,
          },
          { name },
        ),
      maskClosable: true,
      destroyOnClose: true,
      okCancel: false,
      drawer: true,
      style: { width: '740px' },
      children: <GuideModal guideInfo={guideInfo} formatMessage={formatMessage} />,
      okText: formatMessage({ id: 'close', defaultMessage: '关闭' }),
    });
  }, [fetchGuide, guideInfo]);

  return (
    <span onClick={handleOpenGrantAuthModal} className="link-cell">{text}</span>
  );
};

export default observer(GuideButton);
