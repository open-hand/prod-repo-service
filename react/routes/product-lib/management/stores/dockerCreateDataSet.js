export default ((intlPrefix, formatMessage) => ({
  autoQuery: false,
  selection: false,
  pageSize: 10,
  transport: {},
  fields: [
    {
      name: 'code',
      type: 'string',
      required: true,
      label: formatMessage({ id: `${intlPrefix}.model.harborProjectCode`, defaultMessage: '镜像仓库编码' }),
    },
    {
      name: 'name',
      type: 'string',
      label: formatMessage({ id: `${intlPrefix}.model.harborProjectName`, defaultMessage: '镜像仓库名称' }),
      required: true,
    },
    {
      name: 'publicFlag',
      type: 'string',
      required: true,
      defaultValue: 'true',
    },
    {
      name: 'countLimit',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.countLimit`, defaultMessage: '制品数' }),
      required: true,
      validator: (value) => {
        if (value < -1) {
          return '最小为-1';
        }
        return true;
      },
    },
    {
      name: 'storageNum',
      type: 'number',
      label: formatMessage({ id: `${intlPrefix}.model.storageNum`, defaultMessage: '存储容量' }),
      required: true,
      validator: (value) => {
        if (value < -1 || value > 1024) {
          return '值在-1 ~ 1024之间';
        }
        return true;
      },
    },
    {
      name: 'storageUnit',
      type: 'string',
      lookupCode: 'RDUPM.STORAGE_UNIT',
      label: formatMessage({ id: `${intlPrefix}.model.storageUnit`, defaultMessage: '存储单位' }),
      required: true,
    },
    {
      name: 'autoScanFlag',
      type: 'string',
    },
    {
      name: 'preventVulnerableFlag',
      type: 'string',
    },
    {
      name: 'severity',
      type: 'string',
      lookupCode: 'RDUPM.SEVERITY_LEVEL',
      label: formatMessage({ id: `${intlPrefix}.model.severity`, defaultMessage: '危害级别' }),
      defaultValue: 'low',
    },
    {
      name: 'cve',
      type: 'string',
      defaultValue: 'useSysCveFlag',
    },
    {
      name: 'useSysCveFlag',
      type: 'string',
      defaultValue: 'true',
    },
    {
      name: 'useProjectCveFlag',
      type: 'string',
      defaultValue: 'false',
    },
    {
      name: 'endDate',
      type: 'dateTime',
      label: formatMessage({ id: `${intlPrefix}.model.endDate`, defaultMessage: '有效期至' }),
    },
  ],
}));
