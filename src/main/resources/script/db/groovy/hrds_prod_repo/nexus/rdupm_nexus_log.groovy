package script.db.groovy.hrds_prod_repo.nexus

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_log.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2020-05-28-rdupm_nexus_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_nexus_log_s', startValue:"1")
        }
        createTable(tableName: "rdupm_nexus_log", remarks: "制品库_nexus日志表") {
            column(name: "log_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "operator_id", type: "bigint",  remarks: "操作者ID")  {constraints(nullable:"false")}  
            column(name: "project_id", type: "bigint",  remarks: "猪齿鱼项目ID")  {constraints(nullable:"false")}  
            column(name: "organization_id", type: "bigint",  remarks: "组织ID")  {constraints(nullable:"false")}  
            column(name: "repository_id", type: "bigint",  remarks: "rdupm_nexus_repository 表主键")  {constraints(nullable:"false")}
            column(name: "operate_type", type: "varchar(" + 30 * weight + ")",  remarks: "操作类型")  {constraints(nullable:"false")}  
            column(name: "content", type: "varchar(" + 255 * weight + ")",  remarks: "日志内容")  {constraints(nullable:"false")}  
            column(name: "operate_time", type: "datetime",  remarks: "操作时间")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "rdupm_nexus_log", indexName: "rdupm_nexus_log_N1") {
            column(name: "project_id")
        }
   createIndex(tableName: "rdupm_nexus_log", indexName: "rdupm_nexus_log_N2") {
            column(name: "organization_id")
        }
   createIndex(tableName: "rdupm_nexus_log", indexName: "rdupm_nexus_log_N3") {
            column(name: "repository_id")
        }

    }
}