package com.zkyzn.project_manager.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zkyzn.project_manager.so.Err;
import com.zkyzn.project_manager.so.Result;
import com.zkyzn.project_manager.so.ResultList;


import java.util.List;

public class ResUtil {

    public static <T> Result<T> ok(T obj) {
        Result<T> result = new Result<>();
        result.setOk(true);
        result.setErr(null);
        result.setData(obj);
        return result;
    }


    public static <T> ResultList<T> list(IPage<T> page) {
        ResultList<T> result = new ResultList<>();
        result.setOk(true);
        result.setErr(null);
        result.setPageNo((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setDataTotal((int) page.getTotal());
        result.setData(page.getRecords());
        return result;
    }

    public static <T> ResultList<T> list(List<T> data) {
        ResultList<T> result = new ResultList<>();
        result.setOk(true);
        result.setErr(null);
        result.setData(data);
        return result;
    }


    public static <T> Result<T> fail(String failMsg) {
        Result<T> result = new Result<>();
        result.setOk(false);
        Err err = new Err();
        err.setCode(0);
        err.setMsg(failMsg);
        result.setErr(err);
        return result;
    }

    public static <T> Result<T> fail(Integer code,String failMsg) {
        Result<T> result = new Result<>();
        result.setOk(false);
        Err err = new Err();
        err.setCode(code);
        err.setMsg(failMsg);
        result.setErr(err);
        return result;
    }
}
