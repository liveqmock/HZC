package com.haozan.caipiao.taskbasic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskPoolService {

	public static final int MAX_CONNECTIONS = 5;

	private static TaskPoolService service;
	private ExecutorService mThreadPool = Executors.newFixedThreadPool(MAX_CONNECTIONS);

	public static TaskPoolService getInstance() {
		if (service == null) {
		    service = new TaskPoolService();
		}
		return service;
	}

	public void requestService(Runnable runnable) {
		mThreadPool.submit(runnable);
	}

}
