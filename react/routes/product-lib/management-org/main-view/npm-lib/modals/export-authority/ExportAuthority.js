import React, { Component } from 'react';
import { stores, axios, Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react';
import { Modal } from 'choerodon-ui';
import FileSaver from 'file-saver';
import { omit, forEach } from 'lodash';

const { AppState } = stores;
@observer
class ExportAuthority extends Component {
  constructor(props) {
    super(props);
    this.state = {
      // mode: 'all',
      loading: false,
    };
  }

  /**
   * 输出 excel
   */
  exportExcel = () => {
    const { organizationId } = AppState.currentMenuType;
    const { exportStore, dataSet, formatMessage } = this.props;
    this.setState({
      loading: true,
    });
    const params = omit(dataSet.queryDataSet.current.toJSONData(), '__id', '__status', '__dirty');
    let urlParam = '';
    forEach(params, (value, key) => {
      urlParam = `${urlParam}&${key}=${value}`;
    });
    axios.get(`/rdupm/v1/nexus-auths/${organizationId}/export/organization?exportType=DATA${urlParam}&repoType=NPM`, { responseType: 'blob' })
      .then((blob) => {
        const fileName = '权限记录.xlsx';
        FileSaver.saveAs(blob, fileName);
        Choerodon.prompt(formatMessage({ id: 'infra.docManage.message.exportSuccess' }));
        exportStore.setExportModalVisible(false);
      }).finally(() => {
        this.setState({
          loading: false,
        });
      });
  };

  handleCancel = () => {
    const { exportStore } = this.props;
    exportStore.setExportModalVisible(false);
  }

  render() {
    const { loading } = this.state;
    const { exportStore, formatMessage, title } = this.props;
    const visible = exportStore.exportModalVisible;
    return (
      <Modal
        title={formatMessage({ id: 'infra.docManage.message.exportConfirm' })}
        visible={visible}
        onOk={this.exportExcel}
        onCancel={this.handleCancel}
        confirmLoading={loading}
      >
        <div style={{ margin: '10px 0' }}>
          {formatMessage({ id: 'infra.docManage.message.confirm.export' })}
          {' '}
          <span style={{ fontWeight: 500 }}>{title}</span>
          {' '}
          {formatMessage({ id: 'infra.permission' })}
        </div>
      </Modal>
    );
  }
}

ExportAuthority.propTypes = {

};

export default ExportAuthority;
