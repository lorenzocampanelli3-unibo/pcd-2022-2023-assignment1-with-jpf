package pcd.assignment1.common.pool;

import pcd.assignment1.common.tasks.Task;
import pcd.assignment1.common.util.Flag;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomDynamicThreadPoolImpl implements CustomThreadPool {

    private final Lock lock = new ReentrantLock();
    private int maxPoolSize;

    private int totalNumOfThreadsSpawned;
    private int numOfGhostWorkers;
    private BlockingQueue<Task> tasksQueue;
    private Queue<WorkerAgent> workersQueue;
    private Flag stopFlag;

    public CustomDynamicThreadPoolImpl(final int maxPoolSize, Flag stopFlag) {
        this.maxPoolSize = maxPoolSize;
        this.totalNumOfThreadsSpawned = 0;
        this.numOfGhostWorkers = 0;
        this.tasksQueue = new LinkedBlockingQueue<>();
        this.workersQueue = new ConcurrentLinkedQueue<>();
        this.stopFlag = stopFlag;
    }

    @Override
    public void shutDown() {
//        this.isShutdownInitiated.set();
//        this.stopFlag.set(true);
    }


    @Override
    public boolean isShutDown() {
        return this.stopFlag.isSet();
    }

    @Override
    public void execute(Task task) /*throws InterruptedException*/ {
        if (!this.isShutDown()) {
            tasksQueue.add(task);
            this.lock.lock();
            try {
                int actualPoolSize = (maxPoolSize - 1) + numOfGhostWorkers;
                if (workersQueue.size() < actualPoolSize) {
                    WorkerAgent workerAgent = new WorkerAgent(this.totalNumOfThreadsSpawned, this.tasksQueue, this);
                    workersQueue.add(workerAgent);
                    this.totalNumOfThreadsSpawned++;
                    workerAgent.start();
                }
            } finally {
                this.lock.unlock();
            }
        } /*else {
            throw new InterruptedException("Thread Pool shutdown initiated, unable to execute task.");
        }*/
    }

    public void removeWorkerFromPool() {
        /* Instead of really removing the thread from the workersList, I'll leave it in the list and consider
        * it a "ghost" worker by incrementing the "numOfGhostWorkers" count. I think it's better not to mess up with the
        * list by removing workers when they die, especially if a "master" agent will be joining on all of them to
        * wait for their termination */
//        Log.log("In RemoveWorkerFromPool");
        this.lock.lock();
        try {
            this.numOfGhostWorkers++;
        } finally {
            this.lock.unlock();
        }
//        Log.log("Exit removeWorkerFromPool");
    }

    @Override
    public void joinAllWorkers() {
        WorkerAgent worker;
        while ((worker = this.workersQueue.poll()) != null) {
            try {
//                Log.log("Joining worker" + worker);
                worker.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
