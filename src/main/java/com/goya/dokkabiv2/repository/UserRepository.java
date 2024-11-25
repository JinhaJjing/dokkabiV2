package com.goya.dokkabiv2.repository;

import com.goya.dokkabiv2.domain.Role;
import com.goya.dokkabiv2.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT username, email, picture, role FROM users WHERE email = ?";

        // query() 메서드를 사용하여 결과가 하나일 때 Optional로 감싸서 반환
        List<User> users = jdbcTemplate.query(sql, new Object[]{username}, (rs, rowNum) ->
                new User(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("picture"),
                        Role.fromKey(rs.getString("role")) // key를 사용해 Role enum 변환
                )
        );

        // 리스트에서 첫 번째 요소만 반환하거나 비어있는 Optional을 반환
        return users.stream().findFirst();
    }

    public User save(User user) {
        String sql = "INSERT INTO users(username, email, picture, role) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPicture(), user.getRole().getKey());
        return user;
    }
}
