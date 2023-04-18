package pcd.assignment1.jpf;

import pcd.assignment1.common.model.Stats;
import pcd.assignment1.common.pool.CustomThreadPool;
import pcd.assignment1.common.tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class MockLinesCountTask implements Task {

    private MockFile file;
    private CustomThreadPool pool;
    private MockStats mockStats;

    public MockLinesCountTask(MockFile file, CustomThreadPool pool, MockStats mockStats) {
        this.file = file;
        this.pool = pool;
        this.mockStats = mockStats;
    }


    @Override
    public void run() {
        if (!pool.isShutDown()) {
            this.mockStats.updateFileStats(this.file.getNumLines());
        }
    }

    @Override
    public String toString() {
        return "MockLinesCountTask{" +
                "file=" + file.getAbsolutePath() +
                '}';
    }
}
