package pcd.assignment1.common.pool;

import pcd.assignment1.common.tasks.Task;
import pcd.assignment1.common.util.Log;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class WorkerAgent extends Thread {
    // The queue of tasks that the Worker will monitor
    private BlockingQueue<Task> tasksQueue;
    // The Thread Pool that this Worker belongs to
    private CustomThreadPool threadPool;

    public WorkerAgent(int index, BlockingQueue<Task> tasksQueue, CustomThreadPool threadPool) {
        super("DynamicThreadPool-Worker-" + index);
        this.tasksQueue = tasksQueue;
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
        boolean hasPickedUpAtLeastATask = false;
        Log.log(getName() + " started.");
//        try {
            while (!threadPool.isShutDown() && !tasksQueue.isEmpty()) {
                Task task;
                if ((task = tasksQueue.poll()) != null) {  // if not while
                    hasPickedUpAtLeastATask = true;
                    task.run();
                }
//                Thread.sleep(50);
            }
        /*} catch (InterruptedException e) {
            throw new CustomThreadPoolException(e);
        }*/
        Log.log(getName() + " dying.");
        Log.log(getName() + (hasPickedUpAtLeastATask ? " has picked up at least a task" : " has never picked up a task"));
        this.threadPool.removeWorkerFromPool();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkerAgent worker = (WorkerAgent) o;
        return Objects.equals(tasksQueue, worker.tasksQueue) && Objects.equals(threadPool, worker.threadPool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasksQueue, threadPool);
    }
}
