package leo.android.core;

public class AsyncCallback<P, R> {
	
	public static final int RESULT_OK = -1;
	public static final int RESULT_ERR = -2;
	
	private MethodAsyncTask asyncTask;
	private boolean bSynchronized;
	
	public AsyncCallback() {
		this.bSynchronized = false;
	}
	
	public AsyncCallback(boolean bSynchronized) {
		this.bSynchronized = bSynchronized;
	}
	
	protected void setAsyncTask(MethodAsyncTask asyncTask) {
		this.asyncTask = asyncTask;
	}

	public void sendProgress(P...ps) {
		if (bSynchronized) {
			onProgress(ps);
		} else {
			asyncTask.sendProgress(ps);
		}
	}
	
	public void sendResult(int err) {
		if (bSynchronized) {
			onResult(err, (R[]) null);
		} else {
			asyncTask.sendResult(err, (R) null);
		}
	}
	
	public void sendResult(int err, R...rs) {
		if (bSynchronized) {
			onResult(err, rs);
		} else {
			asyncTask.sendResult(err, rs);
		}
	}
	
	public boolean isTaskCancelled() {
		if (bSynchronized) {
			return false;
		} else {
			return asyncTask.isCancelled();
		}
	}

	protected void onProgress(P...ps) {}
	protected void onResult(int err, R...rs) {}
	protected void onCancel() {}
	
}
