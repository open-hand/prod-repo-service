import React, { Component } from 'react';
import { stores, axios, Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react';
import FileSaver from 'file-saver';
import { omit, forEach } from 'lodash';

const { AppState } = stores;
@observer
class ExportAuthority extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }

  componentDidMount() {
    const { loading } = this.state;
    const { modal } = this.props;
    modal.update({
      onOk: this.exportExcel,
      confirmLoading: loading,
    });
  }

  /**
   * 输出 excel
   */
  exportExcel = () => {
    const { organizationId } = AppState.currentMenuType;
    const { dataSet, formatMessage, modal } = this.props;
    this.setState({
      loading: true,
    });
    const params = omit(dataSet.queryDataSet.current.toJSONData(), '__id', '__status', '__dirty');
    let urlParam = '';
    forEach(params, (value, key) => {
      urlParam = `${urlParam}&${key}=${value}`;
    });
    axios.get(`/rdupm/v1/harbor-auths/export/organization/${organizationId}?exportType=DATA${urlParam}`, { responseType: 'blob' })
      .then((blob) => {
        const fileName = '权限记录.xlsx';
        FileSaver.saveAs(blob, fileName);
        Choerodon.prompt(formatMessage({ id: 'infra.docManage.message.exportSuccess' }));
        modal.update({ closable: false });
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
    const { formatMessage, title } = this.props;
    return (
      <div style={{ margin: '10px 0' }}>
        {formatMessage({ id: 'infra.docManage.message.confirm.export' })}
        {' '}
        <span style={{ fontWeight: 500 }}>{title}</span>
        {' '}
        {formatMessage({ id: 'infra.permission' })}
        ？
      </div>
    );
  }
}

ExportAuthority.propTypes = {

};

export default ExportAuthority;
