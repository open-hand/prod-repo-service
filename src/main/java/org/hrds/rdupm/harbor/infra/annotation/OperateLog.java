package org.hrds.rdupm.harbor.infra.annotation;

import java.lang.annotation.*;

import io.choerodon.core.iam.ResourceLevel;

/**
 * description
 *
 * @author chenxiuhong 2020/04/29 3:00 下午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

    //操作的类型
    String operateType() default "";

    //记录操作的内容
    String content() default "";

}
