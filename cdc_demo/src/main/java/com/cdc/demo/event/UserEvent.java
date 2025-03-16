package com.cdc.demo.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public class UserEvent {

  @JsonProperty("payload")
  private Payload payload;

  public Before getBefore() {
    return payload.getBefore();
  }

  public After getAfter() {
    return payload.getAfter();
  }

  public String getAction() {
    return switch (payload.getAction()) {
      case "c" -> "CREATED";
      case "u" -> "UPDATED";
      case "d" -> "DELETED";
      default -> "Unexpected action: " + payload.getAction();
    };
  }

}
