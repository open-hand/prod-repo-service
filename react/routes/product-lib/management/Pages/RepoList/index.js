/* eslint-disable */
import React, { createContext, useMemo } from 'react';
import { Page, Header, Breadcrumb } from '@choerodon/boot';
import { Button } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import CreateRepoButton from '../CreateRepoButton';
import { useStore } from '../../index';
import RepoListPage from './RepoListPage';
import NoRepoList from './NoRepoList';
import NexusAssociateBtn from '../NexusAssociateBtn';
import './index.less';

export const RepositoryIdContext = createContext();

const RepoList = ({ setActiveRepository, init }) => {
  const {
    intl: { formatMessage },
    repoListDs,
    mavenCreateDs,
    dockerCreateBasicDs,
    npmCreateDs,
    dockerCustomCreateDs,
    mavenAssociateDs,
    npmAssociateDs,
  } = useStore();

  const createButtonProps = useMemo(() => ({
    init,
    mavenCreateDs,
    dockerCreateBasicDs,
    npmCreateDs,
    dockerCustomCreateDs,
    formatMessage,
    mavenAssociateDs,
    npmAssociateDs,
  }), [
    init,
    formatMessage,
    mavenCreateDs,
    dockerCreateBasicDs,
    npmCreateDs,
    dockerCustomCreateDs,
    mavenAssociateDs,
    npmAssociateDs,
  ]);

  const nexusAssociateButtonProps = useMemo(() => ({ init, formatMessage }), [init, formatMessage]);

  const repoListProps = useMemo(() => ({ setActiveRepository }), []);
  const noRepoListPageProps = useMemo(() => ({
    init,
    formatMessage,
    mavenCreateDs,
    dockerCreateBasicDs,
    npmCreateDs,
    dockerCustomCreateDs,
    mavenAssociateDs,
    npmAssociateDs,
  }), [
    formatMessage,
    init,
    mavenCreateDs,
    dockerCreateBasicDs,
    npmCreateDs,
    dockerCustomCreateDs,
    mavenAssociateDs,
    npmAssociateDs,
  ]);


  return (
    <Page>
      <Header>
        <Button
          icon="refresh"
          onClick={() => init()}
         />
        <CreateRepoButton {...createButtonProps} />
        <NexusAssociateBtn {...nexusAssociateButtonProps} />
      </Header>
      <Breadcrumb />
      {(repoListDs.records.length > 0 ? <RepoListPage {...repoListProps} /> : <NoRepoList {...noRepoListPageProps} />)}
    </Page>
  );
};

export default observer(RepoList);
