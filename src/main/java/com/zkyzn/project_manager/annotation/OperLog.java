package com.zkyzn.project_manager.annotation;

import java.lang.annotation.*;

/**
 * 项目阶段 项目任务 操作日志注解
 *
 * @author jiangsk
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {
    /**
     * 操作类型
     */
    String type();

    /**
     * 操作描述
     */
    String desc();

    /**
     * 操作对象类型
     */
    Class<?> targetType() default Object.class;

    /**
     * 对象ID在方法参数中的位置
     */
    int idPosition() default 0;

    /**
     * 项目ID在方法参数中的位置
     */
    int projectIdPosition() default -1;

    /**
     * 是否记录原始值（仅用于UPDATE/DELETE）
     */
    boolean recordOriginal() default false;
}