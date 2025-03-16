package com.cdc.demo;

import com.cdc.demo.entity.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  @Query("SELECT * FROM users WHERE name = :name AND email = :email")
  Optional<User> findByNameAndEmail(@Param("name") String name, @Param("email") String email);

  @Modifying
  @Query("INSERT INTO users (name, email) VALUES (:name, :email) ON CONFLICT (name, email) DO NOTHING RETURNING id")
  Long insert(@Param("name") String name, @Param("email") String email);

  // Method to handle batch inserts of unique users
  default List<User> insertAllUnique(List<User> users) {
    return users.stream()
        .map(user -> {
          Optional<User> existingUser = findByNameAndEmail(user.getName(), user.getEmail());
          return existingUser.orElseGet(() -> this.save(user));
        })
        .toList();
  }
}
