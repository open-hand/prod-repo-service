/**
* 制品库配置指引
* @author LZY <zhuyan.luo@hand-china.com>
* @creationDate 2020/4/3
* @copyright 2020 ® HAND
*/
import React, { useCallback } from 'react';
import { Modal } from 'choerodon-ui/pro';
import { observer, useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';
import GuideModal from './GuideModal';

const intlPrefix = 'infra.prod.lib';

const GuideButton = ({ formatMessage, text, record }) => {
  const guideInfo = useLocalStore(() => ({
    info: {},
    setGuideInfo(data) {
      this.info = data;
    },
  }));

  const fetchGuide = useCallback(async () => {
    const { repository, group, name, version } = record;
    try {
      const res = await axios.get('/rdupm/v1/nexus-components/guide', {
        params: { repository, group, name, version },
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
    <span onClick={handleOpenGrantAuthModal} className="product-lib-org-management-lib-list-render-dropdown-column-text">{text}</span>
  );
};

export default observer(GuideButton);
