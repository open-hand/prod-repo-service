import JSONBig from 'json-bigint';
import forEach from 'lodash/forEach';
import isEmpty from 'lodash/isEmpty';

const intlPrefix = 'infra.prod.lib';

export default ((formatMessage, organizationId, projectId) => ({
  autoQuery: false,
  selection: false,
  paging: 'server',
  pageSize: 10,
  primaryKey: 'id',
  parentField: 'parentId',
  idField: 'id',
  transport: {
    read: () => ({
      url: `/rdupm/v1/nexus-components/${organizationId}/project/${projectId}`,
      method: 'GET',
      transformResponse(response) {
        try {
          const res = JSONBig.parse(response);
          const data = [];
          if (res?.failed) {
            return response;
          }
          if (!isEmpty(res?.content)) {
            forEach(res.content, (item) => {
              data.push(item);
              const { components } = item || {};
              forEach(components || [], (component) => {
                // eslint-disable-next-line no-param-reassign
                component.parentId = item?.id;
                data.push(component);
              });
            });
          }
          return data;
        } catch (e) {
          return response;
        }
      },
    }),
  },
  fields: [
    { name: 'version', type: 'string', label: 'version' },
    { name: 'group', type: 'string', label: 'groupId' },
    { name: 'name', type: 'string', label: 'artifactId' },
    { name: 'creatorRealName', type: 'string', label: formatMessage({ id: `${intlPrefix}.model.author`, defaultMessage: '创建人' }) },
    { name: 'creationDate', type: 'string', label: formatMessage({ id: 'createDate', defaultMessage: '创建时间' }) },
  ],
  queryFields: [
    { name: 'version', type: 'string', label: 'version' },
    { name: 'group', type: 'string', label: 'groupId' },
    { name: 'name', type: 'string', label: 'artifactId' },
  ],
}));
