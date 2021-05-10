// stores/index.js
import React, { createContext, useContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import Pages from './Pages';
import {
  repoListDataSet,
  overViewDataSet,
  mavenCreateDataSet,
  nexusComponentDataSet,
  mavenUploadPackageDataSet,
  publishAuthDataSet,
  dockerCreateBasicDataSet,
  dockerImageListDataSet,
  dockerImageTagDataSet,
  dockerAuthDataSet,
  optLogDataSet,
  npmCreateDataSet,
  npmComponentDataSet,
  npmOptLogDataSet,
  dockerCustomCreateDataSet,
  mavenAssociateDataSet,
  npmAssociateDataSet,
  dockerImageScanningDetailsDataSet,
} from './stores';

const Store = createContext();
export function useStore() {
  return useContext(Store);
}

export const intlPrefix = 'infra.prod.lib';

export default injectIntl(inject('AppState')((props) => {
  const {
    AppState,
    intl,
  } = props;
  const { organizationId, projectId } = AppState.currentMenuType;
  const { formatMessage } = intl;

  const repoListDs = useMemo(() => new DataSet(repoListDataSet(intlPrefix, formatMessage, projectId)), [organizationId, projectId]);
  const overViewDs = useMemo(() => new DataSet(overViewDataSet(intlPrefix, formatMessage, organizationId, projectId)), [organizationId, projectId]);
  const mavenCreateDs = useMemo(() => new DataSet(mavenCreateDataSet(intlPrefix, formatMessage, organizationId, projectId)), [organizationId, projectId]);
  const nexusComponentDs = useMemo(() => new DataSet(nexusComponentDataSet(formatMessage, organizationId, projectId)), [formatMessage, organizationId, projectId]);
  const mavenUploadPackageDs = useMemo(() => new DataSet(mavenUploadPackageDataSet(organizationId, projectId)), [organizationId, projectId]);
  const publishAuthDs = useMemo(() => new DataSet(publishAuthDataSet(intlPrefix, formatMessage, projectId)), [projectId]);

  const dockerCustomCreateDs = useMemo(() => new DataSet(dockerCustomCreateDataSet(intlPrefix, formatMessage, projectId)), [projectId]);
  const dockerCreateBasicDs = useMemo(() => new DataSet(dockerCreateBasicDataSet(intlPrefix, formatMessage)), []);
  const dockerImageListDs = useMemo(() => new DataSet(dockerImageListDataSet(intlPrefix, formatMessage)), []);
  const dockerImageTagDs = useMemo(() => new DataSet(dockerImageTagDataSet(intlPrefix, formatMessage, projectId)), [projectId]);
  const dockerImageScanDetailsDs = useMemo(() => new DataSet(dockerImageScanningDetailsDataSet({ projectId })), [projectId]);
  const dockerAuthDs = useMemo(() => new DataSet(dockerAuthDataSet(intlPrefix, formatMessage, projectId)), [projectId]);
  const optLogDs = useMemo(() => new DataSet(optLogDataSet(projectId)), [projectId]);

  const npmCreateDs = useMemo(() => new DataSet(npmCreateDataSet(intlPrefix, formatMessage, organizationId, projectId)), [organizationId, projectId]);
  const npmOverViewDs = useMemo(() => new DataSet(overViewDataSet(intlPrefix, formatMessage, organizationId, projectId)), [organizationId, projectId]);
  const npmComponentDs = useMemo(() => new DataSet(npmComponentDataSet(formatMessage, organizationId, projectId)), [organizationId, projectId]);
  const npmOptLogDs = useMemo(() => new DataSet(npmOptLogDataSet(intlPrefix, formatMessage, organizationId)), [organizationId]);
  
  const mavenAssociateDs = useMemo(() => new DataSet(mavenAssociateDataSet(formatMessage)), []);
  const npmAssociateDs = useMemo(() => new DataSet(npmAssociateDataSet(formatMessage)), []);

  const value = {
    ...props,
    formatMessage,
    organizationId,
    intlPrefix,
    repoListDs,
    overViewDs,
    mavenCreateDs,
    nexusComponentDs,
    mavenUploadPackageDs,
    publishAuthDs,
    dockerCreateBasicDs,
    dockerImageListDs,
    dockerImageTagDs,
    dockerAuthDs,
    optLogDs,
    npmCreateDs,
    npmOverViewDs,
    npmComponentDs,
    npmOptLogDs,
    dockerCustomCreateDs,
    dockerImageScanDetailsDs,
    mavenAssociateDs,
    npmAssociateDs,
  };
  return (
    <Store.Provider value={value}>
      <Pages />
    </Store.Provider>
  );
}));
