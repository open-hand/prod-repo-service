package org.hrds.rdupm.nexus.infra.annotation;

import java.lang.annotation.*;

/**
 * description
 *
 * @author chenxiuhong 2020/04/29 3:00 下午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NexusOperateLog {

    //操作的类型
    String operateType() default "";

    //记录操作的内容
    String content() default "";

}
