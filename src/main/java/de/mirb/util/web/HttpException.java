package de.mirb.util.web;

/**
 * Created by mibo on 01.08.15.
 */
public class HttpException extends Exception {
  private final HttpStatusCode statusCode;

  public HttpException(HttpStatusCode statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public HttpStatusCode getStatusCode() {
    return statusCode;
  }
}
