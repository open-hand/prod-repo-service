// 制品库管理组织层

const docker = {
  // 镜像仓库
  'c7ncd.product-lib-org.docker.mirrorWarehouse.mirrorWarehouse': '镜像仓库',
  'c7ncd.product-lib-org.docker.mirrorWarehouse.globalResourceAllocation': '全局资源分配',
  'c7ncd.product-lib-org.docker.mirrorWarehouse.imageRepositoryName': '镜像仓库名称',
  'c7ncd.product-lib-org.docker.mirrorWarehouse.accessLevel': '访问级别',
  'c7ncd.product-lib-org.docker.mirrorWarehouse.reset': '重置',
  'c7ncd.product-lib-org.docker.mirrorWarehouse.noData': '暂无数据',

  // 镜像列表
  'c7ncd.product-lib-org.docker.mirrorList.mirrorList': '镜像列表',
  'c7ncd.product-lib-org.docker.mirrorList.globalResourceAllocation': '全局资源分配',
  'c7ncd.product-lib-org.docker.mirrorList.imageRepositoryName': '镜像仓库名称',
  'c7ncd.product-lib-org.docker.mirrorList.imageName': '镜像名称',
  'c7ncd.product-lib-org.docker.mirrorList.reset': '重置',
  'c7ncd.product-lib-org.docker.mirrorList.noData': '暂无数据',

  // 用户权限
  'c7ncd.product-lib-org.docker.permission.permission': '用户权限',
  'c7ncd.product-lib-org.docker.permission.permissiontoexport': '导出权限',
  'c7ncd.product-lib-org.docker.permission.imageRepositoryCode': '镜像仓库编码',
  'c7ncd.product-lib-org.docker.permission.imageRepositoryName': '镜像仓库名称',
  'c7ncd.product-lib-org.docker.permission.loginName': '登录名',
  'c7ncd.product-lib-org.docker.permission.userName': '用户姓名',
  'c7ncd.product-lib-org.docker.permission.member': '项目成员角色',
  'c7ncd.product-lib-org.docker.permission.permissionsToRoles': '权限角色',
  'c7ncd.product-lib-org.docker.permission.expirationTime': '过期时间',

  // 操作日志
  'c7ncd.product-lib-org.docker.log.log': '操作日志',
  'c7ncd.product-lib-org.docker.log.permissionOperationRecord': '权限操作记录',
  'c7ncd.product-lib-org.docker.log.imageOperationRecord': '镜像操作记录',
  'c7ncd.product-lib-org.docker.log.userName': '用户名',
  'c7ncd.product-lib-org.docker.log.startDate': '开始日期',
  'c7ncd.product-lib-org.docker.log.endDate': '结束日期',
  'c7ncd.product-lib-org.docker.log.operationType': '操作类型',
  'c7ncd.product-lib-org.docker.log.noData': '暂无操作记录',

};

const maven = {
  // 仓库列表
  'c7ncd.product-lib-org.maven.warehouseList.warehouseList': '仓库列表',
  'c7ncd.product-lib-org.maven.warehouseList.warehouseName': '仓库名称',
  'c7ncd.product-lib-org.maven.warehouseList.WarehouseType': '仓库类型',
  'c7ncd.product-lib-org.maven.warehouseList.WarehouseStrategy': '仓库策略',
  'c7ncd.product-lib-org.maven.warehouseList.reset': '重置',
  'c7ncd.product-lib-org.maven.warehouseList.noData': '暂无数据',
  // 包列表

};

const npm = {
  'c7ncd.product-lib-org.npm.CreateRepository': '创建制品库',
};

export { docker, maven, npm };
