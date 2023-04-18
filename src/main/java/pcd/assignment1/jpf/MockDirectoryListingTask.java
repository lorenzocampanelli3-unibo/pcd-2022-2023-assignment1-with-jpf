package pcd.assignment1.jpf;

import pcd.assignment1.common.pool.CustomThreadPool;
import pcd.assignment1.common.tasks.Task;

import java.util.Iterator;

public class MockDirectoryListingTask implements Task {

    private MockDirectory directory;
    private CustomThreadPool pool;

    private MockStats mockStats;

    public MockDirectoryListingTask(MockDirectory directory, CustomThreadPool pool, MockStats mockStats) {
        this.directory = directory;
        this.pool = pool;
        this.mockStats = mockStats;
    }

    @Override
    public void run() {
        boolean analysisStarted = false;
        Iterator<MockEntry> iterator = this.directory.getEntries().stream().iterator();
        while (!pool.isShutDown() && iterator.hasNext()) {
            if (!analysisStarted) {
                analysisStarted = true;
            }
            MockEntry entry = iterator.next();
            if (entry.getType() == MockEntryType.DIRECTORY) {
                pool.execute(new MockDirectoryListingTask((MockDirectory) entry, pool, mockStats));
            } else {
                pool.execute(new MockLinesCountTask((MockFile) entry, pool, mockStats));
            }
        }
        if (analysisStarted) {
            this.mockStats.updateDirStats();
        }
    }

    @Override
    public String toString() {
        return "MockDirectoryListingTask{" +
                "directory=" + directory.getAbsolutePath() +
                '}';
    }
}
