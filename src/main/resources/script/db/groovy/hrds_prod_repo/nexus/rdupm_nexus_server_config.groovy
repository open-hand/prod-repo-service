package script.db.groovy.hrds_prod_repo.nexus

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_server_config.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2020-05-28-rdupm_nexus_server_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_nexus_server_config_s', startValue:"1")
        }
        createTable(tableName: "rdupm_nexus_server_config", remarks: "制品库_nexus服务信息配置表") {
            column(name: "config_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "server_name", type: "varchar(" + 100 * weight + ")",  remarks: "服务名称")  {constraints(nullable:"false")}  
            column(name: "server_url", type: "varchar(" + 100 * weight + ")",  remarks: "访问地址")  {constraints(nullable:"false")}  
            column(name: "user_name", type: "varchar(" + 30 * weight + ")",  remarks: "管理用户")  {constraints(nullable:"false")}  
            column(name: "password", type: "varchar(" + 128 * weight + ")",  remarks: "管理用户密码")  {constraints(nullable:"false")}  
            column(name: "anonymous", type: "varchar(" + 30 * weight + ")",  remarks: "匿名访问，用户")
            column(name: "anonymous_role", type: "varchar(" + 30 * weight + ")",  remarks: "匿名访问，用户对应角色")
            column(name: "default_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否是Choerodon默认服务")  {constraints(nullable:"false")}
            column(name: "enable_anonymous_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否启用匿名访问控制")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户Id")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "rdupm_nexus_server_config", indexName: "rdupm_nexus_server_config_N1") {
            column(name: "server_name")
        }

    }
}