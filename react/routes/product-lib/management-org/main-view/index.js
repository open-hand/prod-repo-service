import React, { lazy, Suspense, useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import Loading from '@/components/loading';
import { useCheckPermission } from '@/utils';
import { Icon } from 'choerodon-ui';
import { Header, Breadcrumb } from '@choerodon/boot';
import { useProdStore } from '../stores';

import './index.less';

const DockerLib = lazy(() => import('./docker-lib'));
const MavenLib = lazy(() => import('./maven-lib'));
const NpmLib = lazy(() => import('./npm-lib'));

const MainView = observer(() => {
  const {
    prodStore,
    itemTypes: {
      DOCKER,
      MAVEN,
      NPM,
    },
  } = useProdStore();

  const { getSelectedMenu, setSelectedMenu } = prodStore;
  const dockerPermission = useCheckPermission([
    'choerodon.code.project.infra.product-lib.ps.project-owner-harbor',
  ]);
  const mavenPermission = useCheckPermission([
    'choerodon.code.organization.infra.product-lib.ps.organization-admin-maven',
  ]);
  const npmPermission = useCheckPermission([
    'choerodon.code.organization.infra.product-lib.ps.organization-admin-npm',
  ]);

  const content = useMemo(() => {
    if (!setSelectedMenu) return <Loading display />;

    const cmMaps = {
      [DOCKER]: <DockerLib />,
      [MAVEN]: <MavenLib />,
      [NPM]: <NpmLib />,
    };
    return <Suspense fallback={<Loading display />}>{cmMaps[getSelectedMenu]}</Suspense>;
  }, [getSelectedMenu]);

  return (
    <React.Fragment >
      <Header />
      <Breadcrumb />
      <div className="product-lib-org-management">
        <div className="product-lib-org-management-lib-type-list">
          {dockerPermission && (
            <div
              className={getSelectedMenu !== DOCKER ? 'product-lib-org-management-lib-type-list-item' : 'product-lib-org-management-lib-type-list-item-selected'}
              onClick={() => setSelectedMenu(DOCKER)}
            >
              <div className="product-lib-org-management-lib-type-list-item-docker" />
              {getSelectedMenu === DOCKER &&
                <Icon type="check_circle" className="product-lib-org-management-lib-type-list-item-selector-icon" />
              }
            </div>
          )}
          {mavenPermission && (
            <div
              className={getSelectedMenu !== MAVEN ? 'product-lib-org-management-lib-type-list-item' : 'product-lib-org-management-lib-type-list-item-selected'}
              onClick={() => setSelectedMenu(MAVEN)}
            >
              <div className="product-lib-org-management-lib-type-list-item-maven" />
              {getSelectedMenu === MAVEN &&
                <Icon type="check_circle" className="product-lib-org-management-lib-type-list-item-selector-icon" />
              }
            </div>
          )}
          {npmPermission && (
            <div
              className={getSelectedMenu !== NPM ? 'product-lib-org-management-lib-type-list-item' : 'product-lib-org-management-lib-type-list-item-selected'}
              onClick={() => setSelectedMenu(NPM)}
            >
              <div className="product-lib-org-management-lib-type-list-item-npm" />
              {getSelectedMenu === NPM &&
                <Icon type="check_circle" className="product-lib-org-management-lib-type-list-item-selector-icon" />
              }
            </div>
          )}
        </div>
        {content}
      </div>
    </React.Fragment>
  );
});

export default MainView;
