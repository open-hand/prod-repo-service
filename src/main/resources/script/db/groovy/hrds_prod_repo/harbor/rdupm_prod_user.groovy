package script.db.groovy.hrds_prod_repo.harbor

databaseChangeLog(logicalFilePath: 'script/db/rdupm_prod_user.groovy') {
    changeSet(author: "xiuhong.chen@hand-china.com", id: "2020-05-21-rdupm_prod_user") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'rdupm_prod_user_s', startValue:"1")
        }
        createTable(tableName: "rdupm_prod_user", remarks: "制品库-制品用户表") {
            column(name: "id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "user_id", type: "bigint(20)",  remarks: "猪齿鱼用户ID")  {constraints(nullable:"false")}  
            column(name: "login_name", type: "varchar(" + 100 * weight + ")",  remarks: "登录名")  {constraints(nullable:"false")}  
            column(name: "password", type: "varchar(" + 150 * weight + ")",  remarks: "密码")  {constraints(nullable:"false")}  
            column(name: "pwd_update_flag", type: "tinyint(1)",   defaultValue:"0",   remarks: "密码是否被修改，0否表示明文默认密码 1是")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "int(11)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_LOGIN", type: "int(11)",   defaultValue:"-1",   remarks: "")   

        }

        addUniqueConstraint(columnNames:"login_name",tableName:"rdupm_prod_user",constraintName: "login_name")
        addUniqueConstraint(columnNames:"user_id",tableName:"rdupm_prod_user",constraintName: "user_id")
    }
}