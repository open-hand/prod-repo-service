/**
* 分配仓库
* @author JZH <zhihao.jiang@hand-china.com>
* @creationDate 2020/6/4
* @copyright 2020 ® HAND
*/
import React, { useEffect } from 'react';
import {
  Form, TextField, Select, SelectBox,
} from 'choerodon-ui/pro';
import { observer, useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';
import uuidv4 from 'uuid/v4';
import { reaction } from 'mobx';
// import debounce from 'lodash/debounce';
import './index.less';

// const intlPrefix = 'infra.prod.lib';

const { Option } = Select;

const AssignRepoModal = ({
  formatMessage, assignDs, modal, name, libListDs, item, repoType,
}) => {
  const uuid = React.useRef(uuidv4()).current;
  const ulEl = React.useRef();
  const loadingEl = React.useRef(document.createElement('div')).current;

  useEffect(() => {
    // loadingEl.innerText = 'loading';
    loadingEl.classList.add(...['c7n-spin', 'c7n-spin-sm', 'c7n-spin-spinning', 'dynamic-loading-loading-container']);
    const spanEl = document.createElement('span');
    spanEl.classList.add('dynamic-loading-custom-animation');
    const i1 = document.createElement('i');
    i1.classList.add('dynamic-loading-point', 'dynamic-loading-point1');
    const i2 = document.createElement('i');
    i2.classList.add('dynamic-loading-point', 'dynamic-loading-point2');
    const i3 = document.createElement('i');
    i3.classList.add('dynamic-loading-point', 'dynamic-loading-point3');
    const i4 = document.createElement('i');
    i4.classList.add('dynamic-loading-point', 'dynamic-loading-point4');
    spanEl.appendChild(i1);
    spanEl.appendChild(i2);
    spanEl.appendChild(i3);
    spanEl.appendChild(i4);
    loadingEl.appendChild(spanEl);
  }, []);

  const localState = useLocalStore(() => ({
    timeout: null,
    debounce: (fn, wait) => {
      if (localState.timeout !== null) {
        clearTimeout(localState.timeout);
      }
      localState.timeout = setTimeout(fn, wait);
    },
  }));

  const loadingListener = (loading) => {
    if (loading) {
      // eslint-disable-next-line
      ulEl.current && ulEl.current.parentNode.insertBefore(loadingEl, ulEl.current);
    } else {
      // eslint-disable-next-line
      ulEl.current && ulEl.current.parentNode.removeChild(loadingEl);
    }
  };

  const orgStore = useLocalStore(() => ({
    params: {},
    orgList: [],
    loading: false,
    fetchOrg: async (params = {}) => {
      orgStore.params = params;
      orgStore.loading = true;
      const res = await axios.get('/iam/choerodon/v1/organizations?enabledFlag=true', {
        params: { ...params },
      });
      orgStore.loading = false;
      return res;
    },
    initOrg: async () => {
      const res = await orgStore.fetchOrg({ page: 1 });
      orgStore.orgList = [...res.content];
    },
    fetchOrgWithPara: async (params) => {
      const res = await orgStore.fetchOrg(params);
      orgStore.orgList = res.content;
    },
    fetchNextPage: async () => {
      const page = orgStore.params.page + 1;
      const res = await orgStore.fetchOrg({ ...orgStore.params, page });
      orgStore.orgList = [...orgStore.orgList, ...res.content];
    },
  }));

  useEffect(() => {
    assignDs.create({
      name,
    });
    orgStore.initOrg();
  }, []);

  React.useEffect(
    () => reaction(() => orgStore.loading, loadingListener),
    [],
  );

  useEffect(() => {
    modal.handleOk(async () => {
      const validate = await assignDs.current.validate(true);
      if (validate) {
        try {
          const submitData = { ...item, ...assignDs.current.toData(), repoType };
          await axios.post('/rdupm/v1/nexus-repositorys/site/repo-distribute', submitData);
          libListDs.query();
          return true;
        } catch (error) {
          // message.error(error);
          return false;
        }
      }
      return false;
    });
  }, [assignDs, modal]);

  const onScroll = React.useCallback((e) => {
    const { scrollTop, scrollHeight, clientHeight } = e.target;
    if (scrollTop + clientHeight === scrollHeight) {
      orgStore.fetchNextPage();
    }
  }, []);

  const onPopupHiddenChange = React.useCallback(async (hidden) => {
    await new Promise((resolve) => setTimeout(() => resolve(), 500));
    const drwpDownEl = window.document.getElementsByClassName(uuid)[0].getElementsByTagName('ul')[0];
    ulEl.current = drwpDownEl;
    if (hidden) {
      drwpDownEl.removeEventListener('scroll', onScroll);
    } else {
      drwpDownEl.addEventListener('scroll', onScroll);
    }
  }, []);

  const onInput = React.useCallback((e) => {
    e.persist();
    // orgStore.fetchOrgWithPara({ page: 0, tenantName: e.target.value });
    localState.debounce(() => orgStore.fetchOrgWithPara(
      { page: 1, tenantName: e.target.value },
    ), 1000);
  }, [localState]);

  const onClear = React.useCallback(() => {
    orgStore.initOrg();
  }, []);

  const handleOrganizationChange = (value) => {
    assignDs.getField('projectId').fetchLookup(true);
  };

  return (
    <Form dataSet={assignDs} columns={1}>
      <TextField name="name" disabled />
      <Select
        name="organizationId"
        searchable
        popupCls={uuid}
        onPopupHiddenChange={onPopupHiddenChange}
        onInput={onInput}
        onClear={onClear}
        onChange={handleOrganizationChange}
      >
        {
          orgStore.orgList.map((o) => (
            <Option key={o.tenantId} value={o.tenantId}>{o.tenantName}</Option>
          ))
        }
      </Select>
      <Select
        name="projectId"
        searchable
        onClear={() => {
          assignDs.current && assignDs.current.set('distributeRepoAdminId', undefined);
        }}
      />
      <Select name="distributeRepoAdminId" searchable disabled={!(assignDs.current && assignDs.current.get('projectId'))} />
      <SelectBox name="allowAnonymous" className="product-lib-assign-repo-selectbox">
        <Option value={1}>{formatMessage({ id: 'yes', defaultMessage: '是' })}</Option>
        <Option value={0}>{formatMessage({ id: 'no', defaultMessage: '否' })}</Option>
      </SelectBox>
    </Form>
  );
};

export default observer(AssignRepoModal);
