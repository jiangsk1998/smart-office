package com.zkyzn.project_manager.utils.typehandle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zkyzn.project_manager.models.User;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component; // 引入 @Component 注解

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component // <-- 添加这个注解
public class JsonListUserTypeHandler extends BaseTypeHandler<List<User>> {

    private static final Logger logger = LoggerFactory.getLogger(JsonListUserTypeHandler.class);
    private final ObjectMapper objectMapper;
    private final TypeReference<List<User>> typeRef = new TypeReference<List<User>>() {};

    public JsonListUserTypeHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<User> parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = objectMapper.writeValueAsString(parameter);
            ps.setString(i, json);
        } catch (IOException e) {
            logger.error("Error serializing List<User> to JSON string for DB storage", e);
            throw new SQLException("Error serializing List<User>", e);
        }
    }

    @Override
    public List<User> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonString = null;
        try {
            jsonString = rs.getString(columnName);
        } catch (SQLException e) {
            logger.warn("Could not get String directly from column {}, trying BLOB. Error: {}", columnName, e.getMessage());
            byte[] bytes = rs.getBytes(columnName);
            if (bytes != null) {
                try {
                    jsonString = new String(bytes, "UTF-8");
                } catch (IOException ioException) {
                    logger.error("Error converting BLOB to String for column {}", columnName, ioException);
                    throw new SQLException("Error converting BLOB to String", ioException);
                }
            }
        }

        if (jsonString != null && !jsonString.trim().isEmpty()) {
            try {
                return objectMapper.readValue(jsonString, typeRef);
            } catch (IOException e) {
                logger.error("Error deserializing JSON string: {} to List<User>", jsonString, e);
                return null;
            }
        }
        return null;
    }

    @Override
    public List<User> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonString = null;
        try {
            jsonString = rs.getString(columnIndex);
        } catch (SQLException e) {
            logger.warn("Could not get String directly from column index {}, trying BLOB. Error: {}", columnIndex, e.getMessage());
            byte[] bytes = rs.getBytes(columnIndex);
            if (bytes != null) {
                try {
                    jsonString = new String(bytes, "UTF-8");
                } catch (IOException ioException) {
                    logger.error("Error converting BLOB to String for column index {}", columnIndex, ioException);
                    throw new SQLException("Error converting BLOB to String", ioException);
                }
            }
        }

        if (jsonString != null && !jsonString.trim().isEmpty()) {
            try {
                return objectMapper.readValue(jsonString, typeRef);
            } catch (IOException e) {
                logger.error("Error deserializing JSON string: {} to List<User>", jsonString, e);
                return null;
            }
        }
        return null;
    }

    @Override
    public List<User> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonString = null;
        try {
            jsonString = cs.getString(columnIndex);
        } catch (SQLException e) {
            logger.warn("Could not get String directly from callable statement column index {}, trying BLOB. Error: {}", columnIndex, e.getMessage());
            byte[] bytes = cs.getBytes(columnIndex);
            if (bytes != null) {
                try {
                    jsonString = new String(bytes, "UTF-8");
                } catch (IOException ioException) {
                    logger.error("Error converting BLOB to String for callable statement column index {}", columnIndex, ioException);
                    throw new SQLException("Error converting BLOB to String", ioException);
                }
            }
        }

        if (jsonString != null && !jsonString.trim().isEmpty()) {
            try {
                return objectMapper.readValue(jsonString, typeRef);
            } catch (IOException e) {
                logger.error("Error deserializing JSON string: {} to List<User>", jsonString, e);
                return null;
            }
        }
        return null;
    }
}