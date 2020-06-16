package script.db.groovy.hrds_prod_repo.harbor

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_push.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2020-05-28-rdupm_nexus_push") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_nexus_push_s', startValue:"1")
        }
        createTable(tableName: "rdupm_nexus_push", remarks: "制品库_nexus发布权限校验信息表") {
            column(name: "nexus_push_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "type", type: "varchar(" + 30 * weight + ")",  remarks: "类型：MAVEN")  {constraints(nullable:"false")}  
            column(name: "rule", type: "varchar(" + 1000 * weight + ")",  remarks: "规则，多个规则用逗号隔开")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "rdupm_nexus_push", indexName: "rdupm_nexus_push_N1") {
            column(name: "type")
        }

    }
}