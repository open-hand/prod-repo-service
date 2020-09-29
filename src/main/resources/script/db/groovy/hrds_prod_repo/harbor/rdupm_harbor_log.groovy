package script.db.groovy.hrds_prod_repo.harbor

databaseChangeLog(logicalFilePath: 'script/db/rdupm_harbor_log.groovy') {
    changeSet(author: "xiuhong.chen@hand-china.com", id: "2020-04-29-rdupm_harbor_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_harbor_log_s', startValue:"1")
        }
        createTable(tableName: "rdupm_harbor_log", remarks: "制品库-harbor日志表") {
            column(name: "log_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "operator_id", type: "bigint(20)",  remarks: "操作者ID")  {constraints(nullable:"false")}  
            column(name: "project_id", type: "bigint(20)",  remarks: "猪齿鱼项目ID")  {constraints(nullable:"false")}  
            column(name: "organization_id", type: "bigint(20)",  remarks: "组织ID")  {constraints(nullable:"false")}  
            column(name: "operate_type", type: "varchar(" + 30 * weight + ")",  remarks: "操作类型")  {constraints(nullable:"false")}  
            column(name: "content", type: "varchar(" + 200 * weight + ")",  remarks: "日志内容")  {constraints(nullable:"false")}  
            column(name: "operate_time", type: "datetime",  remarks: "操作时间")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_LOGIN", type: "int(11)",   defaultValue:"-1",   remarks: "")   

        }
   createIndex(tableName: "rdupm_harbor_log", indexName: "rdupm_harbor_log_N1") {
            column(name: "project_id")
        }
   createIndex(tableName: "rdupm_harbor_log", indexName: "rdupm_harbor_log_N2") {
            column(name: "organization_id")
        }

    }
    changeSet(id: '2020-09-29-rdupm_harbor_log-update', author: 'weisen.yang@hand-china.com') {
        sql("alter table rdupm_harbor_log modify created_by bigint(20)")
        sql("alter table rdupm_harbor_log modify last_updated_by bigint(20)")
        sql("alter table rdupm_harbor_log modify last_update_login bigint(20)")
    }
}