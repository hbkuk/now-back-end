package com.now.core.post.config;

import com.now.core.post.domain.constants.InquiryStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@link InquiryStatus} 타입을 처리하는 MyBatis TypeHandler
 * 해당 TypeHandler를 사용하여 InquiryStatus enum을 문자열로 변환, 문자열을 InquiryStatus enum으로 변환
 */
@MappedTypes(InquiryStatus.class)
public class InquiryStatusTypeHandler extends BaseTypeHandler<InquiryStatus> {

    /**
     * 지정된 매개변수에 대해 PreparedStatement에 non-null 값을 설정
     *
     * @param ps        PreparedStatement 객체
     * @param i         매개변수의 위치
     * @param parameter 설정할 InquiryStatus 객체
     * @param jdbcType  JDBC 타입
     * @throws SQLException SQL 예외가 발생한 경우.
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, InquiryStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    /**
     * ResultSet으로부터 nullable한 결과를 가져옴
     *
     * @param rs          ResultSet 객체
     * @param columnName  컬럼 이름
     * @return 변환된 InquiryStatus enum 객체
     * @throws SQLException SQL 예외가 발생한 경우
     */
    @Override
    public InquiryStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return InquiryStatus.from(code);
    }

    /**
     * ResultSet으로부터 nullable한 결과를 가져옴
     *
     * @param rs           ResultSet 객체
     * @param columnIndex  컬럼 인덱스
     * @return 변환된 InquiryStatus enum 객체
     * @throws SQLException SQL 예외가 발생한 경우
     */
    @Override
    public InquiryStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return InquiryStatus.from(code);
    }

    /**
     * CallableStatement로부터 nullable한 결과를 가져옴
     *
     * @param cs           CallableStatement 객체
     * @param columnIndex  컬럼 인덱스
     * @return 변환된 InquiryStatus enum 객체
     * @throws SQLException SQL 예외가 발생한 경우
     */
    @Override
    public InquiryStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return InquiryStatus.from(code);
    }
}


