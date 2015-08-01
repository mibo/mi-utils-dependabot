/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package de.mirb.util.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 01.08.15.
 */
public class Result {
  private final boolean success;
  private final String message;
  private final Map<String, Object> values;

  public Result(boolean success, String message) {
    this(success, message, new HashMap<>());
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
