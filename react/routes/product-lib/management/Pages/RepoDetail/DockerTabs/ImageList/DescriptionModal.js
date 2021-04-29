/**
* 镜像描述
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/29
* @copyright 2020 ® HAND
*/
import React, { useEffect, useRef } from 'react';
import { TextArea } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

const DescriptionModal = ({ dockerImageListDs, modal, originData }) => {
  let desc = useRef(originData.description).current;
  useEffect(() => {
    modal.handleOk(async () => {
      try {
        originData.description = desc;
        await axios.post('/rdupm/v1/harbor-image/update/description', originData);
        dockerImageListDs.query();
        return true;
      } catch (error) {
        // message.error(error);
        return false;
      }
    });
  }, [originData, modal]);

  return (
    <TextArea cols={100} rows={10} value={desc} onChange={val => { desc = val; }} />
  );
};

export default observer(DescriptionModal);
