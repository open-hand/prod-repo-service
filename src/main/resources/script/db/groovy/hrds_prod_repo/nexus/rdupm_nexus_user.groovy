package script.db.groovy.hrds_prod_repo.nexus

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_user.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2020-05-28-rdupm_nexus_user") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_nexus_user_s', startValue:"1")
        }
        createTable(tableName: "rdupm_nexus_user", remarks: "制品库_nexus仓库默认用户信息表") {
            column(name: "user_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "repository_id", type: "bigint",  remarks: "rdupm_nexus_repository表主键")  {constraints(nullable:"false")}  
            column(name: "ne_pull_user_id", type: "varchar(" + 100 * weight + ")",  remarks: "nexus 拉取用户Id")
            column(name: "ne_pull_user_password", type: "varchar(" + 128 * weight + ")",  remarks: "nexus 拉取用户密码")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户Id")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "rdupm_nexus_user", indexName: "rdupm_nexus_user_N1") {
            column(name: "repository_id")
        }

    }
}