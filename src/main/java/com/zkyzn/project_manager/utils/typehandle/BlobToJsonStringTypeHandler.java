package com.zkyzn.project_manager.utils.typehandle;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// 映射数据库的BLOB类型到Java的String类型
@MappedJdbcTypes(JdbcType.BLOB)
// 映射Java的List<User>类型，但实际上我们这里是转换为String
@MappedTypes(java.lang.String.class) // 注意：这里是String，因为JacksonTypeHandler会处理这个String
public class BlobToJsonStringTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 将字符串参数设置为BLOB类型
            ps.setBytes(i, parameter.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new SQLException("Error setting String to BLOB parameter (UTF-8)", e);
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte[] bytes = rs.getBytes(columnName);
        if (bytes != null) {
            try {
                return new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new SQLException("Error getting String result from BLOB (UTF-8)", e);
            }
        }
        return null;
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte[] bytes = rs.getBytes(columnIndex);
        if (bytes != null) {
            try {
                return new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new SQLException("Error getting String result from BLOB (UTF-8)", e);
            }
        }
        return null;
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte[] bytes = cs.getBytes(columnIndex);
        if (bytes != null) {
            try {
                return new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new SQLException("Error getting String result from BLOB (UTF-8)", e);
            }
        }
        return null;
    }
}