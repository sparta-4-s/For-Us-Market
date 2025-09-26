package com.sparta.forusmarket.common._dummydata;

import com.sparta.forusmarket.common._dummydata.dto.ProductDto;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchInserter {

    private final JdbcTemplate jdbcTemplate;

    public void insertBatch(List<ProductDto> batch) {
        String SQL = """
                INSERT INTO products (name, price, stock, discount_rate, category, sub_category, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ProductDto p = batch.get(i);
                ps.setString(1, p.name());
                ps.setBigDecimal(2, p.price());
                ps.setInt(3, p.stock());
                ps.setBigDecimal(4, p.discountRate());
                ps.setString(5, String.valueOf(p.category()));
                ps.setString(6, String.valueOf(p.subCategory()));
                ps.setObject(7, p.createdAt());
                ps.setObject(8, p.updatedAt());
            }

            @Override
            public int getBatchSize() {
                return batch.size();
            }
        });
    }
}
