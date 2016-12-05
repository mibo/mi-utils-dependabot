package de.mirb.util.web;

/**
 * Created by mibo on 01.08.15.
 */
public enum HttpHeader {
  ACCEPT("Accept"),
  ACCEPT_CHARSET("Accept-Charset"),
  ACCEPT_ENCODING("Accept-Encoding"),
  ACCEPT_LANGUAGE("Accept-Language"),
  ALLOW("Allow"),
  AUTHORIZATION("Authorization"),
  CACHE_CONTROL("Cache-Control"),
  CONTENT_ENCODING("Content-Encoding"),
  CONTENT_LANGUAGE("Content-Language"),
  CONTENT_LENGTH("Content-Length"),
  CONTENT_LOCATION("Content-Location"),
  CONTENT_TYPE("Content-Type"),
  DATE("Date"),
  ETAG("ETag"),
  EXPIRES("Expires"),
  HOST("Host"),
  IF_MATCH("If-Match"),
  IF_MODIFIED_SINCE("If-Modified-Since"),
  IF_NONE_MATCH("If-None-Match"),
  IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
  LAST_MODIFIED("Last-Modified"),
  LOCATION("Location"),
  LINK("Link"),
  RETRY_AFTER("Retry-After"),
  USER_AGENT("User-Agent"),
  VARY("Vary"),
  WWW_AUTHENTICATE("WWW-Authenticate"),
  COOKIE("Cookie"),
  SET_COOKIE("Set-Cookie");
  
  private final String name;
  
  HttpHeader(String name) {
    this.name = name;
  }

  public String asString() {
    return name;
  }
}
