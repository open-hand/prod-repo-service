/**
* 制品库项目层空列表创建页
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/24
* @copyright 2020 ® HAND
*/
import React from 'react';
import { observer } from 'mobx-react-lite';
import EmptyPage from '@/components/empty-page';
import { Content } from '@choerodon/boot';
import { useCheckPermission } from '@/utils';
import { useOpenModal } from '../CreateRepoButton';
import { intlPrefix } from '../../index';

const NoRepoListPage = ({
  init,
  formatMessage,
  mavenCreateDs,
  dockerCreateBasicDs,
  npmCreateDs,
  dockerCustomCreateDs,
  mavenAssociateDs,
  npmAssociateDs,
}) => {
  const access = useCheckPermission([
    'choerodon.code.project.infra.product-lib.ps.project-owner-maven',
    'choerodon.code.project.infra.product-lib.ps.project-owner-harbor',
    'choerodon.code.project.infra.product-lib.ps.project-owner-npm',
  ]);

  const openModal = useOpenModal({
    init,
    formatMessage,
    mavenCreateDs,
    dockerCreateBasicDs,
    npmCreateDs,
    dockerCustomCreateDs,
    mavenAssociateDs,
    npmAssociateDs,
  });

  return (
    <Content>
      <EmptyPage
        access={access}
        title={formatMessage({ id: `${intlPrefix}.view.noprodlib`, defaultMessage: '暂无制品库' })}
        describe={formatMessage({ id: `${intlPrefix}.view.emptyPageDescription`, defaultMessage: '当前项目下无制品库，请创建' })}
        btnText={formatMessage({ id: `${intlPrefix}.view.createProdlib`, defaultMessage: '创建制品库' })}
        onClick={openModal}
      />
    </Content>
  );
};

export default observer(NoRepoListPage);
