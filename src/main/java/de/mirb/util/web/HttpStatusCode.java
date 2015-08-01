package de.mirb.util.web;

/**
 * Created by michael on 01.08.15.
 */
public enum HttpStatusCode {
  OK(200, "OK"),
  CREATED(201, "Created"),
  ACCEPTED(202, "Accepted"),
  NO_CONTENT(204, "No Content"),
  RESET_CONTENT(205, "Reset Content"),
  PARTIAL_CONTENT(206, "Partial Content"),
  MOVED_PERMANENTLY(301, "Moved Permanently"),
  FOUND(302, "Found"),
  SEE_OTHER(303, "See Other"),
  NOT_MODIFIED(304, "Not Modified"),
  USE_PROXY(305, "Use Proxy"),
  TEMPORARY_REDIRECT(307, "Temporary Redirect"),
  BAD_REQUEST(400, "Bad Request"),
  UNAUTHORIZED(401, "Unauthorized"),
  PAYMENT_REQUIRED(402, "Payment Required"),
  FORBIDDEN(403, "Forbidden"),
  NOT_FOUND(404, "Not Found"),
  METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
  NOT_ACCEPTABLE(406, "Not Acceptable"),
  PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
  REQUEST_TIMEOUT(408, "Request Timeout"),
  CONFLICT(409, "Conflict"),
  GONE(410, "Gone"),
  LENGTH_REQUIRED(411, "Length Required"),
  PRECONDITION_FAILED(412, "Precondition Failed"),
  REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
  REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
  UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
  REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
  EXPECTATION_FAILED(417, "Expectation Failed"),
  PRECONDITION_REQUIRED(428, "Precondition Required"),
  INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
  NOT_IMPLEMENTED(501, "Not Implemented"),
  BAD_GATEWAY(502, "Bad Gateway"),
  SERVICE_UNAVAILABLE(503, "Service Unavailable"),
  GATEWAY_TIMEOUT(504, "Gateway Timeout"),
  HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

  private final int code;
  private final String info;

  HttpStatusCode(int statusCode, String info) {
    this.code = statusCode;
    this.info = info;
  }

  public static HttpStatusCode fromStatusCode(int statusCode) {
    HttpStatusCode[] arr$ = values();
    int len$ = arr$.length;

    for(int i$ = 0; i$ < len$; ++i$) {
      HttpStatusCode s = arr$[i$];
      if(s.code == statusCode) {
        return s;
      }
    }

    return null;
  }

  public int getStatusCode() {
    return this.code;
  }

  public String getInfo() {
    return this.toString();
  }

  public String toString() {
    return this.info;
  }
}
