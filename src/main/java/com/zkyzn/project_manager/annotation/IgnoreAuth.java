package com.zkyzn.project_manager.annotation;

import java.lang.annotation.*;

/**
 * 忽略鉴权
 * @author Zhang Fan
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {
}
