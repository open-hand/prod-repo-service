package script.db

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_server_config.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2020-03-27-rdupm_nexus_server_config") {
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
            column(name: "config_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "server_name", type: "varchar(" + 100 * weight + ")",  remarks: "服务名称")  {constraints(nullable:"false")}  
            column(name: "server_url", type: "varchar(" + 100 * weight + ")",  remarks: "访问地址")  {constraints(nullable:"false")}  
            column(name: "user_name", type: "varchar(" + 30 * weight + ")",  remarks: "管理用户")  {constraints(nullable:"false")}  
            column(name: "password", type: "varchar(" + 128 * weight + ")",  remarks: "管理用户密码")  {constraints(nullable:"false")}  
            column(name: "anonymous", type: "varchar(" + 30 * weight + ")",  remarks: "匿名访问，用户")   
            column(name: "enabled", type: "tinyint(1)",   defaultValue:"0",   remarks: "是否启用")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint(20)",   defaultValue:"0",   remarks: "租户Id")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "rdupm_nexus_server_config", indexName: "rdupm_nexus_server_config_N1") {
            column(name: "server_name")
        }

    }
}