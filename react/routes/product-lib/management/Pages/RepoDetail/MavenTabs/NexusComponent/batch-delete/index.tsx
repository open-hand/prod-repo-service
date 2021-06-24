/**
* 制品库批量删除操作
*/
import React, { useCallback } from 'react';
import { inject } from 'mobx-react';
import { Modal, Button } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import map from 'lodash/map';
import { useUserAuth } from '@/routes/product-lib/management/Pages';
import ProductLibProjectServices from '@/routes/product-lib/management/services';
import { ButtonColor } from 'choerodon-ui/pro/lib/button/enum';
import DataSet from 'choerodon-ui/pro/lib/data-set';

const deleteModalKey = Modal.key();

interface BatchDeleteProps {
  formatMessage(arg0: object, arg1?: object): string,
  nexusComponentDs: DataSet,
  AppState: { currentMenuType: { projectId: number, organizationId: number } },
  refresh(): void,
  repositoryId: string,
}

const BatchDelete = ({
  formatMessage,
  nexusComponentDs,
  AppState: { currentMenuType: { projectId, organizationId } },
  refresh,
  repositoryId,
}: BatchDeleteProps) => {
  const userAuth = useUserAuth();

  const handleDelete = useCallback(async () => {
    const records = nexusComponentDs.selected;
    const postData = map(records, (record) => ({
      componentIds: record.get('componentIds'),
    }));
    try {
      const res = await ProductLibProjectServices.batchDelete({
        organizationId, projectId, postData, repositoryId,
      });
      if (res && res.failed) {
        return false;
      }
      refresh();
      return true;
    } catch (error) {
      return false;
    }
  }, [nexusComponentDs.selected]);

  const handleOpenDeleteModal = useCallback(() => {
    Modal.open({
      key: deleteModalKey,
      title: '批量删除',
      children: '确定删除所选包吗？',
      okText: formatMessage({ id: 'delete', defaultMessage: '删除' }),
      onOk: handleDelete,
    });
  }, [handleDelete]);

  if (userAuth.includes('projectAdmin') || userAuth.includes('developer')) {
    return (
      <Button
        icon="delete"
        disabled={!nexusComponentDs.selected?.length}
        onClick={handleOpenDeleteModal}
        color={'primary' as ButtonColor}
      >
        批量删除
      </Button>
    );
  }
  return null;
};

export default inject('AppState')(observer(BatchDelete));
