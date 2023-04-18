package pcd.assignment1.common.pool;

import pcd.assignment1.common.tasks.Task;

public interface CustomThreadPool {
    void shutDown();

    boolean isShutDown();

    void execute(Task task) /*throws InterruptedException*/;

    void removeWorkerFromPool();

    void joinAllWorkers();
}
