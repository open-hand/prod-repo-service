package script.db.groovy.hrds_prod_repo.nexus

databaseChangeLog(logicalFilePath: 'script/db/rdupm_nexus_assets.groovy') {
    changeSet(author: "wx@hand-china.com", id: "2021-09-29-rdupm_nexus_assets") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_nexus_assets_s', startValue:"1")
        }
        createTable(tableName: "rdupm_nexus_assets", remarks: "制品库_nexus制品信息表") {

            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)}
            column(name: "name", type: "varchar(" + 200 * weight + ")",  remarks: "nexus包的名称")  {constraints(nullable:"false")}
            column(name: "type", type: "varchar(" + 10 * weight + ")",  remarks: "nexus包的类型")  {constraints(nullable:"false")}
            column(name: "repository_id", type: "bigint",  remarks: "rdupm_nexus_repository表主键")  {constraints(nullable:"false")}
            column(name: "project_id", type: "bigint",  remarks: "项目Id")  {constraints(nullable:"false")}
            column(name: "size", type: "bigint",  remarks: "包的大小")  {constraints(nullable:"false")}


            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }


    }
}