package com.cdc.demo.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.ToString;

@ToString
public class UserState {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("email")
  private String email;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;
}
