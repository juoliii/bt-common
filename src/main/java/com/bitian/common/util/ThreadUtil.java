package com.bitian.common.util;


import java.util.List;
import java.util.concurrent.*;

public class ThreadUtil {

	public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(200);
	
	public static void executeThread(Runnable runable){
		executor.execute(runable);
	}

	public static <T> Future<T> submitThread(Callable<T> callable){
		return executor.submit(callable);
	}

	public static <T> void submitAndWaitForFinish(List<Callable<T>> list) {
		try {
			List<Future<T>> fs=executor.invokeAll(list);
			fs.forEach(item-> {
				try {
					item.get();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
