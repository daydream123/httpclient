package com.goodluck.httpclient.download;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.text.TextUtils;

import com.goodluck.httpclient.HttpClient;
import com.goodluck.httpclient.method.GetMethod;
import com.goodluck.httpclient.utils.FileNameUtils;
import com.goodluck.httpclient.utils.IOUtils;
import com.goodluck.httpclient.utils.OnProgressListener;
import com.goodluck.httpclient.utils.ProgressAwareInputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URLConnection;

public class DownloadUtils {
    private static final int BUFFER_SIZE = 4096;

    /**
     * Download bitmap of small size, if bitmap is very big you can use
     * {@link DownloadUtils#downloadBitmap(String, Options, OnProgressListener)} instead.
     */
    static Bitmap downloadBitmap(String imageUrl, OnProgressListener progressListener) {
        return downloadBitmap(imageUrl, null, progressListener);
    }

    /**
     * Download bitmap of big size.
     */
    public static Bitmap downloadBitmap(String imageUrl, Options options, OnProgressListener progressListener) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            downloadToOutputStream(imageUrl, outputStream, progressListener);

            if (options != null) {
                return BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size(), options);
            } else {
                return BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Download file without specified file name.
     */
    public static File download(String downloadUrl, String storePath,
                                boolean isBreakpointMode, OnProgressListener progressListener) throws IOException {
        return download(downloadUrl, storePath, null, isBreakpointMode, progressListener);
    }

    /**
     * Download file with specified file name.
     */
    public static File download(String downloadUrl, String storePath, String fileName,
                                boolean isBreakpointMode, OnProgressListener progressListener) throws IOException {
        if (TextUtils.isEmpty(downloadUrl)) {
            throw new RuntimeException("fileURL cannot be empty or null.");
        }

        if (TextUtils.isEmpty(storePath)) {
            throw new RuntimeException("outputFile cannot be null.");
        }

        File storeFile;
        GetMethod method = new GetMethod(downloadUrl);
        HttpClient httpClient = new HttpClient();
        int responseCode = httpClient.executeMethod(method);
        if (responseCode != HttpURLConnection.HTTP_PARTIAL &&
                responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("http response code error: " + responseCode);
        }

        URLConnection connection = method.getConnection();

        // prepare store file
        if (TextUtils.isEmpty(fileName)) {
            fileName = IOUtils.readFileNameFromUrlConnection(connection);

            if (TextUtils.isEmpty(fileName)) {
                fileName = FileNameUtils.getName(downloadUrl);
            }
        }
        storeFile = new File(storePath, fileName);

        // create parent directory first
        if (!storeFile.getParentFile().exists()) {
            boolean created = storeFile.getParentFile().mkdirs();
            if (!created) {
                throw new IOException("Cannot make directory: " + storeFile.getParentFile().getPath());
            }
        }

        // always check HTTP response code first
        if (isBreakpointMode) {
            if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                long localSize = storeFile.length();
                downloadPartially(connection, storeFile, localSize, progressListener);
            } else { // HttpURLConnection.HTTP_OK
                downloadDirectly(connection, storeFile, progressListener);
            }
        } else {
            if (responseCode == HttpURLConnection.HTTP_OK) {
                downloadDirectly(connection, storeFile, progressListener);
            }
        }
        return storeFile;
    }

    private static void downloadDirectly(URLConnection connection, File storeFile, OnProgressListener progressListener) throws IOException {
        ProgressAwareInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            int contentLength = connection.getContentLength();
            inputStream = new ProgressAwareInputStream(connection.getInputStream(), contentLength, 0L, connection.getURL().getPath());
            inputStream.setOnProgressListener(progressListener);

            // opens an output stream to store file
            outputStream = new FileOutputStream(storeFile);
            IOUtils.copy(inputStream, outputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private static void downloadToOutputStream(String downloadUrl, OutputStream outputStream, OnProgressListener progressListener) throws IOException {
        GetMethod method = new GetMethod(downloadUrl);
        HttpClient httpClient = new HttpClient();
        int responseCode = httpClient.executeMethod(method);
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("error response code: " + responseCode);
        }

        URLConnection connection = method.getConnection();
        int contentLength = connection.getContentLength();

        ProgressAwareInputStream inputStream = null;
        try {
            inputStream = new ProgressAwareInputStream(connection.getInputStream(), contentLength, 0L, connection.getURL().getPath());
            inputStream.setOnProgressListener(progressListener);
            IOUtils.copy(inputStream, outputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    private static void downloadPartially(URLConnection connection, File storeFile, long localSize, OnProgressListener progressListener) throws IOException {
        long remainSize = connection.getContentLength();
        long totalSize = localSize + remainSize;

        ProgressAwareInputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;

        try {
            inputStream = new ProgressAwareInputStream(connection.getInputStream(), totalSize, localSize, connection.getURL().getPath());
            inputStream.setOnProgressListener(progressListener);

            // seek position the be the end of file
            randomAccessFile = new RandomAccessFile(storeFile.getPath(), "rw");
            randomAccessFile.seek(localSize);

            connection.setRequestProperty("Range", "bytes=" + localSize + "-");

            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                randomAccessFile.write(buffer, 0, bytesRead);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(randomAccessFile);
        }
    }
}
