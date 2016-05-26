package leo.android.core;

import java.lang.reflect.Method;

import android.os.AsyncTask;

public class MethodAsyncTask extends AsyncTask<Object, Object, Object> {

	private String name;
	private AsyncCallback<Object, Object> asyncCallback;
	private MethodAsyncTaskListener listener;
	private boolean hasSentResult;
	private int err;
	private Object[] rs;
	
	private Object target;
	private Method method;
	private Object[] args;
	
	private boolean bIsRunning;
	
	public static interface MethodAsyncTaskListener {
		public void onDone(String name);
	}

	protected void setName(String name) {
		this.name = name;
	}
	
	protected void setTargetMethod(Object target, Method method, Object[] args) {
		this.target = target;
		this.method = method;
		this.args = args;
	}
	
	protected void setMethodAsyncTaskListener(MethodAsyncTaskListener listener) {
		this.listener = listener;
	}
	
	protected void setAsyncCallback(AsyncCallback<Object, Object> callback) {
		this.asyncCallback = callback;
	}
	
	protected void sendProgress(Object...ps) {
		publishProgress(ps);
	}
	
	protected void sendResult(int err, Object...rs) {
		this.hasSentResult = true;
		this.err = err;
		// if pass null for rs, rs.length will be 1, and rs[0] == null
		this.rs = rs.length == 1 && rs[0] == null ? null : rs;
	}
	
	@Override
	protected void onPreExecute() {
		bIsRunning = true;
	}
	
	@Override
	protected Object doInBackground(Object... arg0) {
		try {
			method.invoke(target, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Object...ps) {
		if (asyncCallback != null) {
			asyncCallback.onProgress(ps);
		}
	}
	
	@Override
	protected void onPostExecute(Object result) {
		bIsRunning = false;
		if (listener != null) {
			listener.onDone(name);
		}
		if (asyncCallback != null && hasSentResult) {
			hasSentResult = false;
			asyncCallback.onResult(err, rs);
		}
	}
	
	@Override
	protected void onCancelled() {
		bIsRunning = false;
		if (listener != null) {
			listener.onDone(name);
		}
		if (asyncCallback != null) {
			asyncCallback.onCancel();
		}
	}
	
	public boolean isRunning() {
		return bIsRunning;
	}
	
}
