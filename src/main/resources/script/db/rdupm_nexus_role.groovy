package script.db

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_role.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2020-05-28-rdupm_nexus_role") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_nexus_role_s', startValue:"1")
        }
        createTable(tableName: "rdupm_nexus_role", remarks: "制品库_nexus仓库角色信息表") {
            column(name: "role_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "repository_id", type: "bigint",  remarks: "rdupm_nexus_repository表主键")  {constraints(nullable:"false")}  
            column(name: "ne_role_id", type: "varchar(" + 100 * weight + ")",  remarks: "nexus 发布角色Id")   
            column(name: "ne_pull_role_id", type: "varchar(" + 100 * weight + ")",  remarks: "nexus 拉取角色Id")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户Id")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "rdupm_nexus_role", indexName: "rdupm_nexus_role_N1") {
            column(name: "repository_id")
        }

    }
}