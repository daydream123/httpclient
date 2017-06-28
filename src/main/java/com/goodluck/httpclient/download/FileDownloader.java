package com.goodluck.httpclient.download;

import android.text.TextUtils;

import com.goodluck.httpclient.utils.OnProgressListener;
import com.goodluck.httpclient.utils.TrackedAsyncTask;

import java.io.File;
import java.io.IOException;

/**
 * A file download utils which can notify download status like: progress update,
 * error and complete.
 */
public abstract class FileDownloader extends TrackedAsyncTask<Void, Integer, String, File> {
	private String mDownloadUrl;
	private String mStorePath;
	private boolean mUseBreakpoint;

	public FileDownloader(TrackedAsyncTask.Tracker tracker, String downloadUrl, String storePath, boolean useBreakpoint) {
		super(tracker);
		mDownloadUrl = downloadUrl;
		mStorePath = storePath;
		mUseBreakpoint = useBreakpoint;
	}

	@Override
	protected File doInBackground(Void... params) {
		if(TextUtils.isEmpty(mDownloadUrl)){
			throw new RuntimeException("download url cannot be empty or null.");
		}

		if(mStorePath == null){
			throw new RuntimeException("file to store cannot be null.");
		}

		try {
			return DownloadUtils.download(mDownloadUrl, mStorePath, mUseBreakpoint,
                    new OnProgressListener() {

                        @Override
                        public void onProgress(int percentage) {
                            publishProgress(percentage);
                        }

                        @Override
                        public void onError(String errorMsg) {
                            publishError(errorMsg);
                        }

                        @Override
                        public void onCompleted() {
                            // do nothing, onSuccess() will be
                            // executed instead.
                        }
                    });
		} catch (IOException e) {
			publishError(e.getMessage());
			return null;
		}
	}

	/**
	 * Start download task.
	 *
	 * @param oneByOne
	 *            if set true download task one by one polled from queue,
	 *            otherwise download tasks at the same time.
	 */
	public void startDownload(boolean oneByOne) {
		if (oneByOne) {
			executeSerial();
		} else {
			executeParallel();
		}
	}
}
