package org.jfteam.util;

import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.*;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.LineParser;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.CharArrayBuffer;
import org.jfteam.vo.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: fengwenping
 * Date: 2018-01-14
 * Time: 下午5:40
 */
public class HttpRequestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtils.class);

    private static final int DEFAULT_CONNECT_TIMEOUT = 2000;
    private static final int DEFAULT_CONNECT_REQUEST_TIMEOUT = 1000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    private static final int DEFAULT_SOCKET_MAX_TOTAL = 200;
    private static final int DEFAULT_MAX_PER_ROUTE = 200;
    private static CloseableHttpClient closeableHttpClient = null;
    private static CookieStore cookieStore = null;
    private static CredentialsProvider provider = null;

    private static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("HttpRequestUtils close closeable object failure.", e);
        }
    }

    public static RequestConfig createDefaultRequestConfig() {
        final RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                .build();
        return defaultRequestConfig;
    }

    private static CredentialsProvider credentialsProvider() {
        if (provider == null) {
            provider = new BasicCredentialsProvider();
        }
        return provider;
    }

    private static CookieStore createCookieStore() {
        if (cookieStore == null) {
            cookieStore = new BasicCookieStore();
        }
        return cookieStore;
    }

    public static CloseableHttpClient createHttpClient() {
        if (closeableHttpClient == null) {
            HttpMessageParserFactory<HttpResponse> responseParserFactory = new DefaultHttpResponseParserFactory() {
                @Override
                public HttpMessageParser<HttpResponse> create(
                        SessionInputBuffer buffer, MessageConstraints constraints) {
                    LineParser lineParser = new BasicLineParser() {

                        @Override
                        public Header parseHeader(final CharArrayBuffer buffer) {
                            try {
                                return super.parseHeader(buffer);
                            } catch (ParseException ex) {
                                return new BasicHeader(buffer.toString(), null);
                            }
                        }

                    };
                    return new DefaultHttpResponseParser(
                            buffer, lineParser, DefaultHttpResponseFactory.INSTANCE, constraints) {

                        @Override
                        protected boolean reject(final CharArrayBuffer line, int count) {
                            return false;
                        }

                    };
                }

            };
            HttpMessageWriterFactory<HttpRequest> requestWriterFactory = new DefaultHttpRequestWriterFactory();

            HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(requestWriterFactory, responseParserFactory);

            SSLContext sslcontext = SSLContexts.createSystemDefault();

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();

            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connFactory);
            connManager.setMaxTotal(DEFAULT_SOCKET_MAX_TOTAL);
            connManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);

            final CloseableHttpClient httpclient = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .setDefaultCookieStore(HttpRequestUtils.createCookieStore())
                    .setDefaultCredentialsProvider(HttpRequestUtils.credentialsProvider())
                    .setDefaultRequestConfig(HttpRequestUtils.createDefaultRequestConfig())
                    .build();
            closeableHttpClient = httpclient;
        }
        return closeableHttpClient;
    }

    public static ApiResponse doPost(String url, Header[] headers, Map<String, String> params) {
        if (url == null) return null;
        RequestConfig requestConfig = RequestConfig.copy(HttpRequestUtils.createDefaultRequestConfig())
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(DEFAULT_CONNECT_REQUEST_TIMEOUT)
                .build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(HttpRequestUtils.createCookieStore());
        context.setCredentialsProvider(HttpRequestUtils.credentialsProvider());
        httpPost.setHeaders(headers);
        // 创建参数队列
        List<NameValuePair> formParams = new ArrayList<>();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey() != null && entry.getKey().length() > 0) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
        }
        UrlEncodedFormEntity urlEncodedFormEntity;
        CloseableHttpResponse httpResponse = null;
        ApiResponse apiResponse = new ApiResponse();
        try {
            urlEncodedFormEntity = new UrlEncodedFormEntity(formParams, SysConstants.DEFAULT_CHARSET);
            httpPost.setEntity(urlEncodedFormEntity);
            httpResponse = HttpRequestUtils.createHttpClient().execute(httpPost, context);
            StatusLine statusLine = httpResponse.getStatusLine();
            apiResponse.setStatusCode(statusLine.getStatusCode());
            apiResponse.setReasonPhrase(statusLine.getReasonPhrase());
            apiResponse.setHeaders(httpResponse.getAllHeaders());
            apiResponse.setHttpEntity(httpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("HttpRequestUtils doPost failure.", e);
        } finally {
            HttpRequestUtils.close(httpResponse);
            HttpRequestUtils.close(closeableHttpClient);
        }
        return apiResponse;
    }

}
