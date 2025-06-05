package com.zkyzn.project_manager.configs;

import com.zkyzn.project_manager.utils.ResUtil;
import org.springframework.core.annotation.Order;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ControllerAdvice
@Order
public class GlobalExceptionConfig {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Object exceptionProc(IllegalArgumentException e) {
        return ResUtil.fail(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Object exceptionProc(MethodArgumentNotValidException validateException) {
        List<ObjectError> errorList = validateException.getBindingResult().getAllErrors();
        for (ObjectError error : errorList) {
            if(error instanceof  FieldError fieldError) {
                String msg =  fieldError.getField() + fieldError.getDefaultMessage();
                return ResUtil.fail(msg);
            }
            return ResUtil.fail(error.getDefaultMessage());
        }
        return ResUtil.fail(validateException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exceptionProc(Exception e) {
        return ResUtil.fail(e.getMessage());
    }
}
