package de.mirb.util.web;

import org.junit.Test;

import java.net.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by michael on 01.08.15.
 */
public class HttpClientTest {

  @Test
  public void builder() {
    HttpClient c = HttpClient.with("GET", "http://example.org").create();
    assertEquals("GET", c.getMethod());
    assertEquals("http://example.org", c.getUrl());

    HttpClient cProxy = HttpClient.with("GET", "http://example.org")
        .addProxy(Proxy.Type.DIRECT, "proxy.domain", 8080)
        .create();
    assertEquals("GET", cProxy.getMethod());
    assertEquals("http://example.org", cProxy.getUrl());
    assertTrue(cProxy.isProxy());
    assertFalse(cProxy.isAuthentication());

    HttpClient cAuth = HttpClient.with("GET", "http://example.org")
//        .addProxy(Proxy.Type.DIRECT, "proxy.domain", 8080)
        .addAuthentication("username", "geheim")
        .create();
    assertEquals("GET", cAuth.getMethod());
    assertEquals("http://example.org", cAuth.getUrl());
    assertFalse(cAuth.isProxy());
    assertTrue(cAuth.isAuthentication());

    HttpClient cAuthAndProxy = HttpClient.with("GET", "http://example.org")
        .addProxy(Proxy.Type.DIRECT, "proxy.domain", 8080)
        .addAuthentication("username", "geheim")
        .create();
    assertEquals("GET", cAuthAndProxy.getMethod());
    assertEquals("http://example.org", cAuthAndProxy.getUrl());
    assertTrue(cAuthAndProxy.isProxy());
    assertTrue(cAuthAndProxy.isAuthentication());
  }
}