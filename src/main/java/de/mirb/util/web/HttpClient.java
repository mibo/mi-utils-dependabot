package de.mirb.util.web;

import de.mirb.util.io.StringHelper;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpClient {

  public static final String APPLICATION_JSON = "application/json";
  public static final String APPLICATION_XML = "application/xml";
  public static final String APPLICATION_ATOM_XML = "application/atom+xml";
  public static final String METADATA = "$metadata";
  public static final int DEFAULT_BUFFER = 8192;
  //
  private Proxy.Type proxyProtocol;
  private String proxyHostname;
  private int proxyPort;
  private boolean useProxy;
  private String username;
  private String password;
  private boolean useAuthentication;

  private String url;
  private String httpMethod;
  private InputStream body;

  private HttpClient() {
    this.useProxy = false;
    this.useAuthentication = false;
    this.proxyProtocol = Proxy.Type.HTTP;
    this.proxyPort = 80;
  }

  public boolean isProxy() {
    return useProxy;
  }

  public boolean isAuthentication() {
    return useAuthentication;
  }

  public static class HttpClientBuilder {
    private HttpClient client = new HttpClient();
    private Map<String, List<String>> headers = new LinkedHashMap<>();

    private HttpClientBuilder(String method, String url) {
      client.httpMethod = method;
      client.url = url;
    }

    public HttpClientBuilder addAuthentication(String user, String password) {
      client.setAuthentication(user, password);
      return this;
    }

    public HttpClientBuilder addProxy(Proxy.Type type, String host, int port) {
      client.setProxy(type, host, port);
      return this;
    }

    public HttpClientBuilder addHeader(String name, String value) {
      List<String> values = headers.get(name);
      if(values == null) {
        values = new LinkedList<>();
        headers.put(name, values);
      }
      values.add(value);
      return this;
    }

    public HttpClientBuilder setBody(InputStream body) {
      client.body = body;
      return this;
    }

    public HttpClient create() {
      return client;
    }

    public ClientResponse execute() throws IOException, HttpException {
      return client.execute();
    }

    public ClientResponse executeGet() throws IOException, HttpException {
      return client.executeGet();
    }

    public ClientResponse executePost() throws IOException, HttpException {
      return client.executePost();
    }
  }

  public static class ClientResponse {
    private final HttpURLConnection urlConnection;

    public ClientResponse(HttpURLConnection connection) {
      this.urlConnection = connection;
    }

    public InputStream getBody() throws IOException {
      return urlConnection.getInputStream();
    }

    public Map<String, List<String>> getHeaders() {
      return urlConnection.getHeaderFields();
    }
  }

  private void setAuthentication(String username, String password) {
    this.username = username;
    this.password = password;
    this.useAuthentication = true;
  }

  private void setProxy(Proxy.Type type, String host, int port) {
    this.proxyHostname = host;
    this.proxyPort = port;
    this.proxyProtocol = type;
    this.useProxy = true;
  }

  public static HttpClientBuilder get(String url) {
    return new HttpClientBuilder("GET", url);
  }

  public static HttpClientBuilder with(String httpMethod, String url) {
    return new HttpClientBuilder(httpMethod, url);
  }

  public ClientResponse execute() throws IOException, HttpException {
    if("GET".equals(httpMethod)) {
      return executeGet();
    } else if("POST".equals(httpMethod)) {
      return executePost();
    } else {
      throw new UnsupportedOperationException("HttpMethod '" + httpMethod + "' is not supported yet.");
    }
  }

  public ClientResponse executeGet() throws IOException, HttpException {
//      HttpClient client = new HttpClient();
    return new ClientResponse(getRequest(url, getContentType(), httpMethod));
  }

  public ClientResponse executePost() throws IOException, HttpException {
//      HttpClient client = new HttpClient();
    if(body == null) {
      throw new IllegalArgumentException("Body must not be null for post request.");
    }
    return new ClientResponse(postRequest(url, body, getContentType(), httpMethod));
  }

  public String getMethod() {
    return httpMethod;
  }

  public String getUrl() {
    return url;
  }

  /** private methods */

  private String getContentType() {
    // TODO: change
    return APPLICATION_JSON;
  }


  private void checkStatus(HttpURLConnection connection) throws IOException, HttpException {
    if (400 <= connection.getResponseCode() && connection.getResponseCode() <= 599) {
      HttpStatusCode httpStatusCode = HttpStatusCode.fromStatusCode(connection.getResponseCode());
      throw new HttpException(httpStatusCode, httpStatusCode.getStatusCode() + " " + httpStatusCode.toString());
    }
  }

  public static InputStream getRawHttpEntity(String relativeUri, String contentType) throws HttpException, IOException {
    HttpClient client = new HttpClient();
    return (InputStream) client.getRequest(relativeUri, contentType, "GET").getContent();
  }

  private HttpURLConnection getRequest(String relativeUri, String contentType, String httpMethod) throws IOException, HttpException {
    HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);

    connection.connect();

    checkStatus(connection);

    return connection;
  }

  private HttpURLConnection postRequest(String relativeUri, InputStream is, String contentType, String httpMethod)
      throws IOException, HttpException {
    HttpURLConnection connection = initializeConnection(relativeUri, contentType, httpMethod);
    byte[] buffer = new byte[DEFAULT_BUFFER];
    int size = is.read(buffer);

    connection.setDoOutput(true);
    //
    Logger.getLogger(HttpClient.class.getName()).log(Level.INFO, "\n" + new String(buffer, 0, size) + "\n");
    //
    connection.getOutputStream().write(buffer, 0, size);
    connection.connect();
    checkStatus(connection);

    return connection;
  }

  private HttpURLConnection initializeConnection(String url, String contentType, String httpMethod) throws MalformedURLException, IOException {
    URL requestUrl = new URL(url);
    HttpURLConnection connection;
    if (useProxy) {
      Proxy proxy = new Proxy(proxyProtocol, new InetSocketAddress(proxyHostname, proxyPort));
      connection = (HttpURLConnection) requestUrl.openConnection(proxy);
    } else {
      connection = (HttpURLConnection) requestUrl.openConnection();
    }
    // TODO: do better
    connection.setRequestMethod(httpMethod);
    connection.setRequestProperty("Accept", contentType);
    connection.setRequestProperty(HttpHeader.CONTENT_TYPE.asString(), contentType);
    //

    if (useAuthentication) {
      String authorization = "Basic ";
      authorization += new String(Base64.encodeBase64((username + ":" + password).getBytes()));
      connection.setRequestProperty("Authorization", authorization);
    }

    return connection;
  }

  private String rawContentOfLastRequest;

  public String getRawContentOfLastRequest() {
    return rawContentOfLastRequest;
  }

  private InputStream storeRawContent(InputStream content) {
    try {
      String rawContent = StringHelper.asString(content);
      rawContentOfLastRequest = rawContent;
      return new ByteArrayInputStream(rawContent.getBytes("UTF-8"));
    } catch (IOException ex) {
      Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    return content;
  }
}
