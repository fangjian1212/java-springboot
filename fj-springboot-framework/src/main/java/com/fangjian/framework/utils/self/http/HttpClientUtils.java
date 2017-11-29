package com.fangjian.framework.utils.self.http;

import com.fangjian.framework.utils.self.json.JsonUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * http client
 * Created by fangjian on 2017/9/20.
 */
public class HttpClientUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtils.class);


    private static final String PARAM_CHARSET = "UTF-8";
    private static final String RESPONSE_CHARSET = "UTF-8";

    private static final String EQUALS = "=";
    private static final String WHEN = "?";
    private static final String AND = "&";

    /*
     * 最大线程池
     */
    private static final int THREAD_POOL_SIZE = 5;

    public interface HttpClientDownLoadProgress {
        void onProgress(int progress);
    }

    private static HttpClientUtils httpClient = new HttpClientUtils();

    private ExecutorService downloadExcutorService;

    private HttpClientUtils() {
        downloadExcutorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    protected static HttpClientUtils getInstance() {
        return httpClient;
    }

    /**
     * 下载文件
     *
     * @param url
     * @param filePath
     */
    public void download(final String url, final String filePath) {
        downloadExcutorService.execute(new Runnable() {

            @Override
            public void run() {
                httpDownloadFile(url, filePath, null, null);
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url
     * @param filePath
     * @param progress 进度回调
     */
    public void download(final String url, final String filePath,
                         final HttpClientDownLoadProgress progress) {
        downloadExcutorService.execute(new Runnable() {

            @Override
            public void run() {
                httpDownloadFile(url, filePath, progress, null);
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url
     * @param filePath
     */
    private void httpDownloadFile(String url, String filePath,
                                  HttpClientDownLoadProgress progress, Map<String, String> headMap) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            setGetHead(httpGet, headMap);
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
            try {
                LOG.info("httpDownloadFile:{}", response1.getStatusLine());
                HttpEntity httpEntity = response1.getEntity();
                long contentLength = httpEntity.getContentLength();
                InputStream is = httpEntity.getContent();
                // 根据InputStream 下载文件
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int r = 0;
                long totalRead = 0;
                while ((r = is.read(buffer)) > 0) {
                    output.write(buffer, 0, r);
                    totalRead += r;
                    if (progress != null) {// 回调进度
                        progress.onProgress((int) (totalRead * 100 / contentLength));
                    }
                }
                FileOutputStream fos = new FileOutputStream(filePath);
                output.writeTo(fos);
                output.flush();
                output.close();
                fos.close();
                EntityUtils.consume(httpEntity);
            } finally {
                closeResource(response1);
            }
        } catch (Exception e) {
            LOG.error("Exception:{}", e);
        } finally {
            closeResource(httpclient);
        }
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public String httpGet(String url, Map<String, String> paramsMap) {
        return httpGet(url, paramsMap, null);
    }

    /**
     * http get请求
     *
     * @param url
     * @return
     */
    public String httpGet(String url, Map<String, String> paramsMap, Map<String, String> headMap) {
        String responseContent = null;
//        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = getHttpClient();
        try {

            url = setUrlParams(url, paramsMap);
            HttpGet httpGet = new HttpGet(url);
            setGetHead(httpGet, headMap);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            try {
                LOG.info("httpGet:{}", httpResponse.getStatusLine());
                HttpEntity entity = httpResponse.getEntity();
                responseContent = getRespString(entity);
                EntityUtils.consume(entity);
            } finally {
                closeResource(httpResponse);
            }
        } catch (Exception e) {
            LOG.error("Exception:{}", e);
        } finally {
            closeResource(httpClient);
        }
        return responseContent;
    }


    /*
     * 设置url后面请求参数
     * @param url
     * @param paramsMap
     * @return
     * @throws UnsupportedEncodingException
     */
    private String setUrlParams(String url, Map<String, String> paramsMap) throws UnsupportedEncodingException {
        if (paramsMap == null || paramsMap.isEmpty()) {
            return url;
        }
        StringBuffer sb = new StringBuffer();// 存储参数
        String params;// 编码之后的参数
        // 编码请求参数
        if (paramsMap.size() == 1) {
            for (String name : paramsMap.keySet()) {
                sb.append(name).append(EQUALS).append(
                        java.net.URLEncoder.encode(paramsMap.get(name),
                                PARAM_CHARSET));
            }
            params = sb.toString();
        } else {
            for (String name : paramsMap.keySet()) {
                if (paramsMap.get(name) != null) {
                    sb.append(name).append(EQUALS).append(
                            java.net.URLEncoder.encode(paramsMap.get(name),
                                    PARAM_CHARSET)).append(AND);
                }
            }
            String temp_params = sb.toString();
            params = temp_params.substring(0, temp_params.length() - 1);
        }


        return url + WHEN + params;
    }


    /**
     * post 请求 url 添加参数
     *
     * @param url
     * @param urlParamsMap
     * @param paramsMap
     * @return
     */
    public String httpPostUrlParam(String url, Map<String, String> urlParamsMap, Map<String, Object> paramsMap) {
        try {
            url = setUrlParams(url, urlParamsMap);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UnsupportedEncodingException:{}", e);
        }
        return httpPost(url, paramsMap);
    }

    /**
     * post 请求
     *
     * @param url
     * @param paramsMap
     * @return
     */
    public String httpPost(String url, Map<String, Object> paramsMap) {
        return httpPost(url, paramsMap, null);
    }

    /**
     * http的post请求
     *
     * @param url
     * @param paramsMap
     * @return
     */
    public String httpPost(String url, Map<String, Object> paramsMap,
                           Map<String, String> headMap) {
        String responseContent = null;
//        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpClient httpClient = getHttpClient();
        try {
            HttpPost httpPost = new HttpPost(url);
            setPostHead(httpPost, headMap);
//            setPostParamsForm(httpPost, paramsMap);
            setPostParamsJson(httpPost, paramsMap);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                LOG.info("httpPost:{}", response.getStatusLine());
                HttpEntity entity = response.getEntity();
                responseContent = getRespString(entity);
                EntityUtils.consume(entity);
            } finally {
                closeResource(response);
            }
        } catch (Exception e) {
            LOG.error("Exception:{}", e);
        } finally {
            closeResource(httpClient);
        }
        return responseContent;
    }

    /*
     * 设置POST的参数 json
     *
     * @param httpPost
     * @param paramsMap
     * @throws Exception
     */
    private void setPostParamsJson(HttpPost httpPost, Map<String, Object> paramsMap)
            throws Exception {
        if (paramsMap == null || paramsMap.isEmpty()) {
            //json方式
            StringEntity entity = new StringEntity(JsonUtil.toString(paramsMap), PARAM_CHARSET);//解决中文乱码问题
            entity.setContentEncoding(PARAM_CHARSET);
            entity.setContentType(ContentType.APPLICATION_JSON.toString());
            httpPost.setEntity(entity);
        }
    }

    /*
     * 设置POST的参数
     *
     * @param httpPost
     * @param paramsMap
     * @throws Exception
     */
    private void setPostParamsForm(HttpPost httpPost, Map<String, Object> paramsMap)
            throws Exception {
        if (paramsMap == null || paramsMap.isEmpty()) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<String> keySet = paramsMap.keySet();
            for (String key : keySet) {
                nvps.add(new BasicNameValuePair(key, (String) paramsMap.get(key)));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        }
    }

    /*
     * 设置Post http的HEAD
     *
     * @param httpPost
     * @param headMap
     */
    private void setPostHead(HttpPost httpPost, Map<String, String> headMap) {
        if (headMap == null || headMap.isEmpty()) {
            Set<String> keySet = headMap.keySet();
            for (String key : keySet) {
                httpPost.addHeader(key, headMap.get(key));
            }
        }
    }

    /*
     * 设置Get http的HEAD
     *
     * @param httpGet
     * @param headMap
     */
    private void setGetHead(HttpGet httpGet, Map<String, String> headMap) {
        if (headMap == null || headMap.isEmpty()) {
            Set<String> keySet = headMap.keySet();
            for (String key : keySet) {
                httpGet.addHeader(key, headMap.get(key));
            }
        }
    }

    /**
     * 上传文件
     *
     * @param serverUrl       服务器地址
     * @param localFilePath   本地文件路径
     * @param serverFieldName
     * @param params
     * @return
     * @throws Exception
     */
    public String uploadFileImpl(String serverUrl, String localFilePath,
                                 String serverFieldName, Map<String, String> params)
            throws Exception {
        String respStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(serverUrl);
            FileBody binFileBody = new FileBody(new File(localFilePath));

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder
                    .create();
            // add the file params
            multipartEntityBuilder.addPart(serverFieldName, binFileBody);
            // 设置上传的其他参数
            setUploadParams(multipartEntityBuilder, params);

            HttpEntity reqEntity = multipartEntityBuilder.build();
            httppost.setEntity(reqEntity);

            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity resEntity = response.getEntity();
                respStr = getRespString(resEntity);
                EntityUtils.consume(resEntity);
            } finally {
                closeResource(response);
            }
        } finally {
            closeResource(httpclient);
        }
        return respStr;
    }

    /*
     * 关闭资源
     *
     * @param resource
     */
    private void closeResource(Closeable resource) {
        try {
            if (null != resource)
                resource.close();
        } catch (IOException e) {
            LOG.error("IOException:{}", e);
        }
    }

    /*
     * 设置上传文件时所附带的其他参数
     *
     * @param multipartEntityBuilder
     * @param params
     */
    private void setUploadParams(MultipartEntityBuilder multipartEntityBuilder,
                                 Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                multipartEntityBuilder
                        .addPart(key, new StringBody(params.get(key),
                                ContentType.TEXT_PLAIN));
            }
        }
    }

    /*
     * 将返回结果转化为String
     *
     * @param entity
     * @return
     * @throws Exception
     */
    private String getRespString(HttpEntity entity) throws Exception {
        if (entity == null) {
            return null;
        }
        InputStream is = entity.getContent();
        StringBuffer strBuf = new StringBuffer();
        byte[] buffer = new byte[4096];
        int r = 0;
        while ((r = is.read(buffer)) > 0) {
            strBuf.append(new String(buffer, 0, r, RESPONSE_CHARSET));
        }
        return strBuf.toString();
    }


    private static final int MAX_TOTAL = 200;

    /*
     * 获取client
     *
     * @return
     */
    private CloseableHttpClient getHttpClient() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        /*
         * 设置最大httpclient pool最大连接数
         */
        cm.setMaxTotal(MAX_TOTAL);
        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setDefaultRequestConfig(getDefaultRequestConfig())
                .setConnectionManager(cm)
                .build();
        return httpClient;
    }


    private static final int CONNECTION_REQUEST_TIMEOUT = 1000;
    private static final int CONNECT_TIMEOUT = 3000;
    private static final int SOCKET_TIMEOUT = 3000;

    /*
     * 设置超时时间
     * @return
     */
    private RequestConfig getDefaultRequestConfig() {
        RequestConfig requestConfig = RequestConfig
                .custom()
                /*
                 * 从连接池中获取连接的超时时间，假设：连接池中已经使用的连接数等于setMaxTotal，新来的线程在等待1000
                 * 后超时，错误内容：org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                 */
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                /*
                 * 这定义了通过网络与服务器建立连接的超时时间。
                 * Httpclient包中通过一个异步线程去创建与服务器的socket连接，这就是该socket连接的超时时间，
                 * 此处设置为2秒。假设：访问一个IP，192.168.10.100，这个IP不存在或者响应太慢，那么将会返回
                 * java.net.SocketTimeoutException: connect timed out
                 */
                .setConnectTimeout(CONNECT_TIMEOUT)
                /*
                 * 指的是连接上一个url，获取response的返回等待时间，假设：url程序中存在阻塞、或者response
                 * 返回的文件内容太大，在指定的时间内没有读完，则出现
                 * java.net.SocketTimeoutException: Read timed out
                 */
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
        return requestConfig;
    }


}