package de.mirb.util.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mibo on 01.08.15.
 */
public class Result {
  private final boolean success;
  private final String message;
  private final Map<String, Object> values;

  public Result(boolean success, String message) {
    this(success, message, new HashMap<String, Object>());
  }

  public Result(boolean success, String message, Map<String, Object> values) {
    this.success = success;
    this.message = message;
    this.values = values;
  }

  public static Result success() {
    return new Result(true, "");
  }

  public static Result failed(String message) {
    return new Result(false, message);
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }

  public Map<String, Object> getValues() {
    return Collections.unmodifiableMap(values);
  }

  public Object getValue(String name) {
    return values.get(name);
  }
}
