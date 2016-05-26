package leo.android.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import leo.android.core.MethodAsyncTask.MethodAsyncTaskListener;

public class AsyncTaskProxy<T> implements MethodAsyncTaskListener {
	
	private Map<String, MethodAsyncTask> asyncTaskMap;
	
	@SuppressWarnings("unchecked")
	public final T getProxy() {
		final Object target = this;
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(), getClass().getInterfaces(), new InvocationHandler() {
			@Override
			public Object invoke(Object object, Method method, Object[] args) throws Throwable {
				if (args.length == 0 || !(args[args.length-1] instanceof AsyncCallback)) {
					// No AsyncTask callback
					return method.invoke(target, args);
				} else {
					if (asyncTaskMap == null) {
						asyncTaskMap = new HashMap<String, MethodAsyncTask>();
					}
					AsyncCallback<Object, Object> callback = (AsyncCallback<Object, Object>) args[args.length - 1];
					String key = method.getName();
					if (asyncTaskMap.containsKey(key)) {
						key += "-" + UUID.randomUUID().toString();
					}
					MethodAsyncTask asyncTask = new MethodAsyncTask();
					asyncTask.setName(key);
					asyncTask.setTargetMethod(target, method, args);
					asyncTask.setMethodAsyncTaskListener(AsyncTaskProxy.this);
					asyncTask.setAsyncCallback(callback);
					callback.setAsyncTask(asyncTask);
					asyncTaskMap.put(key, asyncTask);
					asyncTask.execute();
					return asyncTask;
				}
			}
		});
	}
	
	// Cancel all AsyncTasks which launched by this class
	public final void cancelAll() {
		if (asyncTaskMap != null) {
			Collection<MethodAsyncTask> values = asyncTaskMap.values();
			if (values != null) {
				for (MethodAsyncTask asyncTask : values) {
					if (asyncTask != null && asyncTask.isRunning()) {
						asyncTask.cancel(true);
					}
				}
			}
		}
	}

	@Override
	public void onDone(String name) {
		if (asyncTaskMap != null) {
			asyncTaskMap.remove(name); // Remove AsyncTask if it finished
		}
	}

}
