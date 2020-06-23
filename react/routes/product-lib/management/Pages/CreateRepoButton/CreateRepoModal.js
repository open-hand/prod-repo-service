/**
* 制品库创建仓库
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/4/1
* @copyright 2020 ® HAND
*/
import React, { useMemo, useEffect, useState } from 'react';
import { Icon } from 'choerodon-ui';
import { Form, Select, SelectBox } from 'choerodon-ui/pro';
import { observer, useLocalStore } from 'mobx-react-lite';
import classnames from 'classnames';
import { axios, stores } from '@choerodon/boot';
import { reaction } from 'mobx';
import MavenCreateForm from './MavenCreateForm';
import DockerCreateForm from './DockerCreateForm';
import DockerCustomCreateForm from './DockerCustomCreateForm';
import MavenAssociateForm from './MavenAssociateForm';
import NpmAssociateForm from './NpmAssociateForm';
import NpmCreateForm from './NpmCreateForm';
import './index.less';

const { Option } = Select;

const CreateLibTypeEnum = {
  MAVEN: 'maven',
  DOCKER: 'docker',
  NPM: 'npm',
};

const CreateDockerTypeEnum = {
  DEFAULT: 'default',
  CUSTOM: 'custom',
};

const NexusCreateTypeEnum = {
  CREATE: 'create',
  ASSOCIATE: 'associate',
};

const CreateRepoModal = ({
  validateStore,
  formatMessage,
  mavenCreateDs,
  npmCreateDs,
  dockerCreateBasicDs,
  dockerCustomCreateDs,
  modal,
  init,
  testBtnStore,
  mavenAssociateDs,
  npmAssociateDs,
  enableAnonymousFlag,
}) => {
  const [hasHarborRepo, setHasHarborRepo] = useState(true);

  const createChoiceStore = useLocalStore(() => ({
    libType: CreateLibTypeEnum.MAVEN,
    setLibType(value) {
      this.libType = value;
    },
  }));

  const createMavenStore = useLocalStore(() => ({
    mavenCreateType: NexusCreateTypeEnum.CREATE,
    setMavenCreateType(value) {
      this.mavenCreateType = value;
    },
  }));

  const createNpmStore = useLocalStore(() => ({
    npmCreateType: NexusCreateTypeEnum.CREATE,
    setNpmCreateType(value) {
      this.npmCreateType = value;
    },
  }));

  const createDockerStore = useLocalStore(() => ({
    dockerCreateType: CreateDockerTypeEnum.DEFAULT,
    setDockerCreateType(value) {
      this.dockerCreateType = value;
    },
    get isShowTestConnectBtn() {
      return createDockerStore.dockerCreateType === CreateDockerTypeEnum.CUSTOM && createChoiceStore.libType === CreateLibTypeEnum.DOCKER;
    },
  }));

  const isShowTestBtnListener = (isShow) => {
    testBtnStore.setIsShow(isShow);
  };

  useEffect(
    () => reaction(() => createDockerStore.isShowTestConnectBtn, isShowTestBtnListener),
    [],
  );

  useEffect(() => {
    async function initCreate() {
      const { currentMenuType: { projectId } } = stores.AppState;
      const res = await axios.get(`/rdupm/v1/harbor-project/list-project/${projectId} `);
      if (res && res.length > 0) {
        setHasHarborRepo(true);
        createDockerStore.setDockerCreateType(CreateDockerTypeEnum.CUSTOM);
      } else {
        setHasHarborRepo(false);
      }
    }
    initCreate();
  }, []);

  const npmAssociateFormProps = useMemo(() => ({ modal, formatMessage, npmAssociateDs, init }), [modal, init, npmAssociateDs, formatMessage]);
  const mavenAssociateFormProps = useMemo(() => ({ modal, formatMessage, mavenAssociateDs, init }), [modal, init, mavenAssociateDs, formatMessage]);
  const mavenCreateFormProps = useMemo(() => ({ modal, formatMessage, mavenCreateDs, enableAnonymousFlag, init }), [modal, init, enableAnonymousFlag, mavenCreateDs, formatMessage]);
  const dockerCreateFormProps = useMemo(() => ({ modal, formatMessage, dockerCreateBasicDs, init }), [modal, init, dockerCreateBasicDs, formatMessage]);
  const dockerCustomCreateProps = useMemo(() => ({ modal, validateStore, dockerCustomCreateDs, formatMessage, init }), [validateStore, dockerCustomCreateDs, modal, init, formatMessage]);
  const npmCreateFormProps = useMemo(() => ({ modal, formatMessage, enableAnonymousFlag, npmCreateDs, init }), [modal, init, enableAnonymousFlag, npmCreateDs, formatMessage]);

  return (
    <React.Fragment>
      <div className="product-lib-create-repo-lib-selector">
        <div className="product-lib-create-repo-lib-selector-label">制品库类型</div>
        <div className="product-lib-create-repo-lib-selector-field">
          <div
            onClick={() => createChoiceStore.setLibType(CreateLibTypeEnum.MAVEN)}
            className={classnames('product-lib-create-repo-maven-img', { 'product-lib-create-repo-lib-img-active': createChoiceStore.libType === CreateLibTypeEnum.MAVEN })}
          >
            {createChoiceStore.libType === CreateLibTypeEnum.MAVEN &&
              <Icon type="check_circle" className="product-lib-create-repo-lib-selector-icon" />
            }
          </div>

          <div
            onClick={() => createChoiceStore.setLibType(CreateLibTypeEnum.DOCKER)}
            className={classnames('product-lib-create-repo-docker-img', { 'product-lib-create-repo-lib-img-active': createChoiceStore.libType === CreateLibTypeEnum.DOCKER })}
          >
            {createChoiceStore.libType === CreateLibTypeEnum.DOCKER &&
              <Icon type="check_circle" className="product-lib-create-repo-lib-selector-icon" />
            }
          </div>

          <div
            onClick={() => createChoiceStore.setLibType(CreateLibTypeEnum.NPM)}
            className={classnames('product-lib-create-repo-npm-img', { 'product-lib-create-repo-lib-img-active': createChoiceStore.libType === CreateLibTypeEnum.NPM })}
          >
            {createChoiceStore.libType === CreateLibTypeEnum.NPM &&
              <Icon type="check_circle" className="product-lib-create-repo-lib-selector-icon" />
            }
          </div>
        </div>
      </div>


      {createChoiceStore.libType === CreateLibTypeEnum.MAVEN &&
        <React.Fragment>
          <Form>
            <SelectBox
              className={classnames('product-lib-createrepo-selectbox', 'product-lib-createrepo-selectbox-type')}
              label={formatMessage({ id: 'infra.prod.lib.view.createRepoType', defaultMessage: '创建方式' })}
              onChange={(val) => createMavenStore.setMavenCreateType(val)}
              value={createMavenStore.mavenCreateType}
            >
              <Option value={NexusCreateTypeEnum.CREATE}>
                {formatMessage({ id: 'infra.prod.lib.view.createRepo', defaultMessage: '创建仓库' })}
              </Option>
              <Option value={NexusCreateTypeEnum.ASSOCIATE}>
                {formatMessage({ id: 'infra.prod.lib.view.associateRepo', defaultMessage: '关联仓库' })}
              </Option>
            </SelectBox>
          </Form>
          {createMavenStore.mavenCreateType === NexusCreateTypeEnum.CREATE &&
            <MavenCreateForm {...mavenCreateFormProps} />
          }
          {createMavenStore.mavenCreateType === NexusCreateTypeEnum.ASSOCIATE &&
            <MavenAssociateForm {...mavenAssociateFormProps} />
          }
        </React.Fragment>
      }


      {createChoiceStore.libType === CreateLibTypeEnum.DOCKER &&
        <React.Fragment>
          <Form>
            <SelectBox
              className={classnames('product-lib-createrepo-selectbox', 'product-lib-createrepo-selectbox-type')}
              label={formatMessage({ id: 'infra.prod.lib.view.createDockerType', defaultMessage: '仓库来源' })}
              onChange={(val) => createDockerStore.setDockerCreateType(val)}
              value={createDockerStore.dockerCreateType}
              disabled={hasHarborRepo}
            >
              <Option value={CreateDockerTypeEnum.DEFAULT}>
                {formatMessage({ id: 'infra.prod.lib.view.defaultRepo', defaultMessage: '默认仓库' })}
              </Option>
              <Option value={CreateDockerTypeEnum.CUSTOM}>
                {formatMessage({ id: 'infra.prod.lib.view.customRepo', defaultMessage: '自定义仓库' })}
              </Option>
            </SelectBox>
          </Form>
          {createDockerStore.dockerCreateType === CreateDockerTypeEnum.DEFAULT &&
            <DockerCreateForm {...dockerCreateFormProps} />
          }
          {createDockerStore.dockerCreateType === CreateDockerTypeEnum.CUSTOM &&
            <DockerCustomCreateForm {...dockerCustomCreateProps} />
          }
        </React.Fragment>
      }

      
      {createChoiceStore.libType === CreateLibTypeEnum.NPM &&
        <React.Fragment>
          <Form>
            <SelectBox
              className={classnames('product-lib-createrepo-selectbox', 'product-lib-createrepo-selectbox-type')}
              label={formatMessage({ id: 'infra.prod.lib.view.createRepoType', defaultMessage: '创建方式' })}
              onChange={(val) => createNpmStore.setNpmCreateType(val)}
              value={createNpmStore.npmCreateType}
            >
              <Option value={NexusCreateTypeEnum.CREATE}>
                {formatMessage({ id: 'infra.prod.lib.view.createRepo', defaultMessage: '创建仓库' })}
              </Option>
              <Option value={NexusCreateTypeEnum.ASSOCIATE}>
                {formatMessage({ id: 'infra.prod.lib.view.associateRepo', defaultMessage: '关联仓库' })}
              </Option>
            </SelectBox>
          </Form>
          {createNpmStore.npmCreateType === NexusCreateTypeEnum.CREATE &&
            <NpmCreateForm {...npmCreateFormProps} />
          }
          {createNpmStore.npmCreateType === NexusCreateTypeEnum.ASSOCIATE &&
            <NpmAssociateForm {...npmAssociateFormProps} />
          }
        </React.Fragment>
      }
    </React.Fragment>
  );
};

export default observer(CreateRepoModal);
