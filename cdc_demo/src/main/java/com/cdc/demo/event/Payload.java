package com.cdc.demo.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Payload {

  @JsonProperty("before")
  private Before before;

  @JsonProperty("after")
  private After after;

  @JsonProperty("op")
  private String action;

}
