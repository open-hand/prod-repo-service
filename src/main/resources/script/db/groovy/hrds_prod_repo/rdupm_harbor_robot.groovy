package script.db.groovy.hrds_prod_repo

databaseChangeLog(logicalFilePath: 'script/db/rdupm_harbor_robot.groovy') {
    changeSet(author: "mofei.li@hand-china.com", id: "2020-06-01-rdupm_harbor_robot") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_harbor_robot_s', startValue:"1")
        }
        createTable(tableName: "rdupm_harbor_robot", remarks: "制品库-harbor机器人账户表") {
            column(name: "robot_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "harbor_robot_id", type: "bigint(20)",  remarks: "harbor机器人账户ID")  {constraints(nullable:"false")}  
            column(name: "project_id", type: "bigint(20)",  remarks: "猪齿鱼项目id")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 100 * weight + ")",  remarks: "账户名称")  {constraints(nullable:"false")}  
            column(name: "action", type: "varchar(" + 10 * weight + ")",  remarks: "功能，pull/push")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 100 * weight + ")",  remarks: "机器人账户描述，拉取/推送")   
            column(name: "enable_flag", type: "varchar(" + 1 * weight + ")",  remarks: "是否启用，Y启用/N禁用")  {constraints(nullable:"false")}  
            column(name: "token", type: "varchar(" + 1000 * weight + ")",  remarks: "机器人账户token")  {constraints(nullable:"false")}  
            column(name: "end_date", type: "datetime",  remarks: "账户到期时间")  {constraints(nullable:"false")}  
            column(name: "organization_id", type: "bigint(20)",  remarks: "组织id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_LOGIN", type: "int(11)",   defaultValue:"-1",   remarks: "")   

        }
   createIndex(tableName: "rdupm_harbor_robot", indexName: "rdupm_harbor_robot_N1") {
            column(name: "project_id")
        }

        addUniqueConstraint(columnNames:"harbor_robot_id",tableName:"rdupm_harbor_robot",constraintName: "harbor_robot_id")
    }
}