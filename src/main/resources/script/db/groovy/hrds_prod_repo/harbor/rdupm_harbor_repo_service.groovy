package script.db.groovy.hrds_prod_repo.harbor

databaseChangeLog(logicalFilePath: 'script/db/rdupm_harbor_repo_service.groovy') {
    changeSet(author: "mofei.li@hand-china.com", id: "2020-06-05-rdupm_harbor_repo_service") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_harbor_repo_service_s', startValue:"1")
        }
        createTable(tableName: "rdupm_harbor_repo_service", remarks: "制品库-harbor仓库服务关联表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "custom_repo_id", type: "bigint(20)",  remarks: "自定义镜像仓库ID")  {constraints(nullable:"false")}  
            column(name: "app_service_id", type: "bigint(20)",  remarks: "应用服务ID")   
            column(name: "project_id", type: "bigint(20)",  remarks: "猪齿鱼项目ID")   
            column(name: "organization_id", type: "bigint(20)",  remarks: "猪齿鱼组织ID")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_LOGIN", type: "int(11)",   defaultValue:"-1",   remarks: "")   

        }
   createIndex(tableName: "rdupm_harbor_repo_service", indexName: "rdupm_harbor_repo_service_N1") {
            column(name: "custom_repo_id")
        }
   createIndex(tableName: "rdupm_harbor_repo_service", indexName: "rdupm_harbor_repo_service_N2") {
            column(name: "app_service_id")
        }
   createIndex(tableName: "rdupm_harbor_repo_service", indexName: "rdupm_harbor_repo_service_N3") {
            column(name: "project_id")
        }

    }
}