package com.goodluck.httpclient.utils;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Modeled after {@link AsyncTask}; the basic usage is the same, with extra features:
 * - Bulk cancellation of multiple tasks.  This is mainly used by UI to cancel pending tasks
 *   in onDestroy() or similar places.
 * - Instead of {@link AsyncTask#onPostExecute}, it has {@link #onSuccess(Object)} and {@link #onError(Object)}as the
 *   regular {@link AsyncTask#onPostExecute} is a bit hard to predict when it'll be called and
 *   when it won't, even cannot distinguish success and failed.
 * - Use DiscardOldestPolicy in PARALLEL_EXECUTOR to prevent from throwing RejectedExecutionException.
 * - Support SDK version from GINGERBREAD.
 *
 */
@SuppressWarnings("hiding")
public abstract class TrackedAsyncTask<Params, Progress, Error, Result> {
	// inner messages and handler
    private static final int MESSAGE_POST_PROGRESS = 0x1;
    private static final int MESSAGE_NOTIFY_ERROR = 0x2;
    private static final InternalHandler sHandler = new InternalHandler(Looper.getMainLooper());
    
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(128);
    
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };
    
    /**
     * An {@link Executor} that executes tasks one at a time in serial order.  
     * This serialization is global to a particular process.
     */
    public static final Executor SERIAL_EXECUTOR = Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB ? new SerialExecutor() :
        Executors.newSingleThreadExecutor(sThreadFactory);
    
	/**
	 * An {@link Executor} that can be used to execute tasks in parallel, here
	 * the default PARALLEL_EXECUTOR was changed to use DiscardOldestPolicy,
	 * which will not cause RejectedExecutionException any more.
	 */
	public static final Executor PARALLEL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 
			KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
    
    @TargetApi(VERSION_CODES.GINGERBREAD)
	private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<>();
        Runnable mActive;

        public synchronized void execute(@NonNull final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            
            if (mActive == null) {
                scheduleNext();
            }
        }

        synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
            	PARALLEL_EXECUTOR.execute(mActive);
            }
        }
    }
    
    /**
     * Tracks {@link TrackedAsyncTask}.
     *
     * Call {@link #cancelAll()} to cancel all tasks registered.
     */
    public static class Tracker {
        private final LinkedList<TrackedAsyncTask<?, ?, ?, ?>> mTasks = new LinkedList<>();

        private void add(TrackedAsyncTask<?, ?, ?, ?> task) {
            synchronized (mTasks) {
                mTasks.add(task);
            }
        }

        private void remove(TrackedAsyncTask<?, ?, ?, ?> task) {
            synchronized (mTasks) {
            	task.cancel(true);
                mTasks.remove(task);
            }
        }

        /**
         * Cancel all registered tasks.
         */
        public void cancelAll() {
            synchronized (mTasks) {
                for (TrackedAsyncTask<?, ?, ?, ?> task : mTasks) {
                    task.cancel(true);
                }
                mTasks.clear();
            }
        }

        /**
         * Cancel all instances of the same class as {@code current} other than
         * {@code current} itself.
         */
        public void cancelOthers(TrackedAsyncTask<?, ?, ?, ?> current) {
            final Class<?> clazz = current.getClass();
            synchronized (mTasks) {
                final ArrayList<TrackedAsyncTask<?, ?, ?, ?>> toRemove = new ArrayList<>();
                for (TrackedAsyncTask<?, ?, ?, ?> task : mTasks) {
                    if ((task != current) && task.getClass().equals(clazz)) {
                        task.cancel(true);
                        toRemove.add(task);
                    }
                }
                for (TrackedAsyncTask<?, ?, ?, ?> task : toRemove) {
                    mTasks.remove(task);
                }
            }
        }

        /**
         * Return remaining tasks count which still not been executed.
         */
        public int getRemainingTaskCount() {
            return mTasks.size();
        }

        /**
         * Check specified task whether still not been executed.
         */
        public boolean containsTask(TrackedAsyncTask<?, ?, ?, ?> task) {
            return mTasks.contains(task);
        }
    }

    public interface TrackSupport {
        TrackedAsyncTask.Tracker getTracker();
    }

    private final Tracker mTracker;

    private static class InnerTask<Params2, Progress2, Error2, Result2> extends AsyncTask<Params2, Progress2, Result2> {
        private final TrackedAsyncTask<Params2, Progress2, Error2, Result2> mOwner;

        public InnerTask(TrackedAsyncTask<Params2, Progress2, Error2, Result2> owner) {
            mOwner = owner;
        }
        
        @Override
        protected void onPreExecute() {
        	mOwner.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final Result2 doInBackground(Params2... params) {
            return mOwner.doInBackground(params);
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(Progress2... values) {
        	mOwner.onProgressUpdate(values[0]);
        }

        @Override
        protected void onCancelled() {
            mOwner.unregisterSelf();

            // if error occurred task also will be canceled,
            // so onCancelled() will be called only when error never occurred in that task
            if(!mOwner.mErrorOccurred){
                mOwner.onCancelled();
            }
        }

        @Override
        public void onPostExecute(Result2 result) {
            if (mOwner.mCancelled) {
                mOwner.onCancelled();
            } else if (!mOwner.mErrorOccurred){
                mOwner.onSuccess(result);
            }
            mOwner.unregisterSelf();
        }
    }

    private final InnerTask<Params, Progress, Error, Result> mInnerTask;
    private volatile boolean mCancelled;
    private volatile boolean mErrorOccurred;

	/**
	 * Construction with what create new instances can be canceled by Tracker.
	 * 
	 * @param tracker
	 *            can retrieve instance like : <p>
	 *            TrackedAsyncTask.Tracke mTracke = new TrackedAsyncTask.Tracke();
	 */
	public TrackedAsyncTask(Tracker tracker) {
		mTracker = tracker;
		if (mTracker != null) {
			mTracker.add(this);
		}
		mInnerTask = new InnerTask<>(this);
	}
    
    /**
     * Construction with what create new instances cannot be canceled later. 
     */
    public TrackedAsyncTask(){
    	mTracker = null;
    	mInnerTask = new InnerTask<>(this);
    }
    
    private void unregisterSelf() {
        if (mTracker != null) {
            mTracker.remove(this);
        }
    }
    
    /** @see AsyncTask#onPreExecute() */
    protected  void onPreExecute(){}

    /** @see AsyncTask#doInBackground */
    protected abstract Result doInBackground(Params... params);

    /** @see AsyncTask#cancel(boolean) */
    public void cancel(boolean mayInterruptIfRunning) {
        mCancelled = true;
        mInnerTask.cancel(mayInterruptIfRunning);
    }
    
    /**
    * @see #cancel(boolean)
    */
   public final boolean isCancelled() {
       return mInnerTask.isCancelled();
   }

    /** @see AsyncTask#onCancelled */
    protected void onCancelled() {}
    
    /** @see AsyncTask#onProgressUpdate */
    protected void onProgressUpdate(Progress value){}
    
    /** @see AsyncTask#publishProgress */
    protected final void publishProgress(Progress value) {
        if (!mCancelled) {
            sHandler.obtainMessage(MESSAGE_POST_PROGRESS, new AsyncTaskResult<>(this, value)).sendToTarget();
        }
    }
    
    /**
     * Called in child thread to notify error occurred and
     * {@link #onError(Error)} will be called in main thread.
     */
    protected final void publishError(Error error){
    	if (!mCancelled && !mErrorOccurred) {
    		mErrorOccurred = true;
    		mInnerTask.cancel(true);
            sHandler.obtainMessage(MESSAGE_NOTIFY_ERROR, new AsyncTaskResult<>(this, error)).sendToTarget();
        }
    }
    
    /**
     * If any error occurred, {@link #onSuccess(Object)} will not be executed
     * and current task also been canceled.
     */
    protected void onError(Error error) {}

    /**
     * Similar to {@link AsyncTask#onPostExecute}, but this will never be executed if
     * {@link #cancel(boolean)} has been called before its execution 
     * or {@link #publishError(Error)} has been called even if
     * {@link #doInBackground(Object...)} has completed when cancelled.
     *
     * @see AsyncTask#onPostExecute
     */
    protected void onSuccess(Result result) {}

    /**
     * execute on {@link #PARALLEL_EXECUTOR}
     *
     * @see AsyncTask#execute
     */
    @SafeVarargs
    public final TrackedAsyncTask<Params, Progress, Error, Result> executeParallel(Params... params) {
        return executeInternal(PARALLEL_EXECUTOR, false, params);
    }

    /**
     * execute on {@link #SERIAL_EXECUTOR}
     *
     * @see AsyncTask#execute
     */
    @SafeVarargs
    protected final TrackedAsyncTask<Params, Progress, Error, Result> executeSerial(Params... params) {
        return executeInternal(SERIAL_EXECUTOR, false, params);
    }
    
	/**
	 * execute on {@link #PARALLEL_EXECUTOR} but with options to cancel all
	 * previously created instances of the same class tracked by the same
	 * {@link Tracker}
	 * 
	 * @see AsyncTask#execute
	 */
    @SafeVarargs
    protected final TrackedAsyncTask<Params, Progress, Error, Result> executeParallel(boolean cancelPrevious, Params... params) {
		return executeInternal(PARALLEL_EXECUTOR, cancelPrevious, params);
	}

	/**
	 * execute on {@link #SERIAL_EXECUTOR} but with options to cancel all
	 * previously created instances of the same class tracked by the same
	 * {@link Tracker}
	 * 
	 * @see AsyncTask#execute
	 */
    @SafeVarargs
    protected final TrackedAsyncTask<Params, Progress, Error, Result> executeSerial(boolean cancelPrevious, Params... params) {
		return executeInternal(SERIAL_EXECUTOR, cancelPrevious, params);
	}

    @SafeVarargs
    private final TrackedAsyncTask<Params, Progress, Error, Result> executeInternal(Executor executor,
                                                                                    boolean cancelPrevious, Params... params) {
        if (cancelPrevious) {
            if (mTracker == null) {
                throw new IllegalStateException();
            } else {
                mTracker.cancelOthers(this);
            }
        }
        if(Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD){
        	mInnerTask.executeOnExecutor(executor, params);
        }else{
        	mInnerTask.execute(params);
        }
        return this;
    }

    /**
     * Runs a {@link Runnable} in a bg thread, using {@link #PARALLEL_EXECUTOR}.
     */
    public static TrackedAsyncTask<Void, Void, Void, Void> runAsyncParallel(Runnable runnable) {
        return runAsyncInternal(PARALLEL_EXECUTOR, runnable);
    }

    private static TrackedAsyncTask<Void, Void, Void, Void> runAsyncInternal(Executor executor, final Runnable runnable) {
        TrackedAsyncTask<Void, Void, Void, Void> task = new TrackedAsyncTask<Void, Void, Void, Void>() {
        	
            @Override
            protected Void doInBackground(Void... params) {
                runnable.run();
                return null;
            }
        };
        return task.executeInternal(executor, false, (Void[]) null);
    }

    /**
     * Run {@code} on a worker thread, return execution result.
     * @param newTask callable task
     * @return execution result
     */
	public static <T> T runCallable(Callable<T> newTask){
		ExecutorService pool = null;
		try {
			pool = Executors.newSingleThreadExecutor();
			Future<T> future = pool.submit(newTask);
			return future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}finally{
            if (pool != null) {
                pool.shutdown();
            }
        }
		return null;
	}
	
    /**
     * Wait until {@link #doInBackground} finishes and returns the results of the computation.
     *
     * @see AsyncTask#get
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return mInnerTask.get();
    }
    
    @SuppressWarnings("rawtypes")
    private static class AsyncTaskResult<Data> {
		final TrackedAsyncTask mTask;
        final Data mData;

        AsyncTaskResult(TrackedAsyncTask task, Data data) {
            mTask = task;
            mData = data;
        }
    }
    
    private static class InternalHandler extends Handler {

        InternalHandler(Looper looper) {
            super(looper);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
			AsyncTaskResult result = (AsyncTaskResult) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
                    
                case MESSAGE_NOTIFY_ERROR:
                	result.mTask.onError(result.mData);
                	break;
            }
        }
    }
}
