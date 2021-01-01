package ua.kyivstar.couchcleantgt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MysqlService {
    private final JdbcTemplate jdbcTemplate;

    public List<String> fetchAccountIds() {
        List<String> result = new ArrayList<>();
        jdbcTemplate.query("select accountId " +
                "from account_logins al, temp_logins_to_delete_tgt tt " +
                "where al.login = tt.login", rs -> {
            result.add(rs.getString("login"));
        });
        return result;
    }
}
