package com.cdc.demo.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User implements Persistable<Long> {

  @Id
  private Long id;
  private String name;
  private String email;
  @Column(value = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  @Transient
  private boolean isNew = false;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return isNew || id == null;
  }

  public static User createNew(String name, String email) {
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setCreatedAt(LocalDateTime.now());
    user.setNew(true);
    return user;
  }
}
