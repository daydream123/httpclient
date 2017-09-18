## What kind of this httpclient?

If you are used to be a java web developer, HttpClient of Apache is common used library, since its API is designed so friendly that we can use it do everything about http easily.

But as is known to all, Apache's HttpClient is not recommended to use in Android App project, because Google no longer maintain it, so it may has bugs inside and anther bad news is that the source was removed from Andriod 6.0 by Google, so HttpUrlConnection is the suggested one, Google will improve its performance and make it more and more stable in the future.

From the output log printed by logcat when we can see the implementation of HttpUrlConnection was changed to OKHttp(a new high-performance http client) from Android 4.4（sound it is a good news).

Nowdays the HttpUrlConnection is stable and has high performance, but it's api is not friendly to us, why not to design and develop a new HttpClient with HttpUrlConnection's API?

## How it works?
```java
@Test
public void testHttHeader() throws Exception {
    GetMethod method = new GetMethod("http://10.1.158.59:8088/header");
    method.addHeader("info", "hello world");

    HttpClient httpClient = new HttpClient();
    int code = httpClient.executeMethod(method);
    Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

    String response = method.getResponseBodyAsString();
    Assert.assertEquals(response, RESPONSE);
}

@Test
public void testHttpGetParams() throws Exception {
    GetMethod method = new GetMethod("http://10.1.158.59:8088/get/params");
    method.addParam("name", "hello world");
    method.addParam("age", "10");

    HttpClient httpClient = new HttpClient();

    int code = httpClient.executeMethod(method);
    Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

    String response = method.getResponseBodyAsString();
    Assert.assertEquals(response, RESPONSE);
}

@Test
public void testHttpPostUrlEncoded() throws IOException {
    PostMethod method = new PostMethod("http://10.1.158.59:8088/post/urlencoded");

    UrlEncodedFormBody body = new UrlEncodedFormBody();
    body.setNameValuePairs(new NameValuePair("name", "hello"), new NameValuePair("age", "111"));
    method.setBody(body);

    HttpClient httpClient = new HttpClient();
    int code = httpClient.executeMethod(method);
    Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

    String response = method.getResponseBodyAsString();
    Assert.assertEquals(response, RESPONSE);
}

@Test
public void testHttpPostText() throws IOException {
    PostMethod method = new PostMethod("http://10.1.158.59:8088/post/text");
    method.setBody(new TextBody("hello world, this is test log"));
    HttpClient httpClient = new HttpClient();

    int code = httpClient.executeMethod(method);
    Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

    String response = method.getResponseBodyAsString();
    Assert.assertEquals(response, RESPONSE);
}

@Test
public void testHttpPostJSON() throws IOException {
    PostMethod method = new PostMethod("http://10.1.158.59:8088/post/json");
    method.setBody(new JsonBody("{\"name\":\"hello\",\"age\":10}"));
    HttpClient httpClient = new HttpClient();

    int code = httpClient.executeMethod(method);
    Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

    String response = method.getResponseBodyAsString();
    Assert.assertEquals(response, RESPONSE);
}

@Test
public void testHttpPosFile() throws IOException {
    PostMethod method = new PostMethod("http://10.1.158.59:8088/post/file");
    method.setBody(new FileBody("G:\\sticky\\src\\Main.java", 0, new OnProgressListener() {
        @Override
        public void onError(String errorMsg) {
            Log.d(TAG, "upload error: " + errorMsg);
        }

        @Override
        public void onProgress(int percentage) {
            Log.d(TAG, "upload percentage: " + percentage);
        }

        @Override
        public void onCompleted() {
            Log.d(TAG, "upload complete");
        }
    }));
    HttpClient httpClient = new HttpClient();

    int code = httpClient.executeMethod(method);
    Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

    String response = method.getResponseBodyAsString();
    Assert.assertEquals(response, RESPONSE);
}

@Test
public void testHttpPostMultiPart() throws IOException {
    PostMethod method = new PostMethod("http://10.1.158.59:8088/post/multipart");
    MultipartBody body = new MultipartBody();
    body.addPart("key1", new TextBody("multipart-text"));
    body.addPart("key2", new XmlBody("<html><head><title>hello world</title></head></html>"));
    body.addPart("key3", new FileBody("/mnt/sdcard/Download/mm.apk", 0, new OnProgressListener() {
        @Override
        public void onError(String errorMsg) {
            Log.d(TAG, "upload error: " + errorMsg);
        }

        @Override
        public void onProgress(int percentage) {
            Log.d(TAG, "upload percentage: " + percentage);
        }

        @Override
        public void onCompleted() {
            Log.d(TAG, "upload complete");
        }
    }));

    method.setBody(body);

    HttpClient httpClient = new HttpClient();
    int code = httpClient.executeMethod(method);
    Assert.assertEquals(code, HttpURLConnection.HTTP_OK);

    String response = method.getResponseBodyAsString();
    Assert.assertEquals(response, RESPONSE);
}
```
