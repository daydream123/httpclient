# httpclient

If you are used to be a java web developer, HttpClient of Apache is common used library, since its API is designed so friendly that we can use it do everything about http easily.

But as is known to all, Apache's HttpClient is not recommended to use in Android App project, because Google no longer maintain it, so it may has bugs inside and anther bad news is that the source was removed from Andriod 6.0 by Google, so HttpUrlConnection is the suggested one, Google will improve its performance and make it more and more stable in the future.

From the output log printed by logcat when we can see the implementation of HttpUrlConnection was changed to OKHttp(a new high-performance http client) from Android 4.4（sound it is a good news).

Nowdays the HttpUrlConnection is stable and has high performance, but it's api is not friendly to us, why not to design and develop a new HttpClient with HttpUrlConnection's API?
