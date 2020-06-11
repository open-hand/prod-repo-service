package script.db.groovy.hrds_prod_repo

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_project_service.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2020-06-10-rdupm_nexus_project_service") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_nexus_project_service_s', startValue:"1")
        }
        createTable(tableName: "rdupm_nexus_project_service", remarks: "制品库-项目与nexus服务关系表") {
            column(name: "project_service_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "config_id", type: "bigint",  remarks: "rdupm_nexus_server_config 表主键")  {constraints(nullable:"false")}  
            column(name: "project_id", type: "bigint",  remarks: "猪齿鱼项目ID")  {constraints(nullable:"false")}  
            column(name: "organization_id", type: "bigint",  remarks: "猪齿鱼组织ID")  {constraints(nullable:"false")}  
            column(name: "enable_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否启用")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "rdupm_nexus_project_service", indexName: "rdupm_nexus_project_service_N1") {
            column(name: "project_id")
        }

        addUniqueConstraint(columnNames:"config_id",tableName:"rdupm_nexus_project_service",constraintName: "config_id")
    }
}