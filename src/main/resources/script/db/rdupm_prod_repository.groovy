package script.db

databaseChangeLog(logicalFilePath: 'script/db/rdupm_prod_repository.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2020-03-27-rdupm_prod_repository") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_prod_repository_s', startValue:"1")
        }
        createTable(tableName: "rdupm_prod_repository", remarks: "制品库_制品信息表") {
            column(name: "prod_repository_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "type", type: "varchar(" + 30 * weight + ")",  remarks: "类型： MVANE、DOCKER、NPM ")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 100 * weight + ")",  remarks: "名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint(20)",   defaultValue:"0",   remarks: "租户Id")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"type",tableName:"rdupm_prod_repository",constraintName: "type")
    }
}