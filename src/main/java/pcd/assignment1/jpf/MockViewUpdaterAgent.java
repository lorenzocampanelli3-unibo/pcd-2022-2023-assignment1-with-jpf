package pcd.assignment1.jpf;

import pcd.assignment1.common.model.Stats;
import pcd.assignment1.common.util.Chrono;
import pcd.assignment1.common.util.Flag;
import pcd.assignment1.ex2.gui.AnalyzerView;

import java.lang.reflect.InvocationTargetException;

public class MockViewUpdaterAgent extends Thread {

    private static final long REFRESH_EVERY_MS = 50;

    private Flag stopFlag;

    private MockStats mockStats;

    private Chrono chrono;

    public MockViewUpdaterAgent(MockStats mockStats, Flag stopFlag) {
        super("ViewUpdater-Agent");
        this.stopFlag = stopFlag;
        this.mockStats = mockStats;
        this.chrono = new Chrono();
    }

    @Override
    public void run() {
        while (!stopFlag.isSet()) {
            try {
                chrono.start();
                MockStats.Snapshot snapshot = mockStats.getSnapshot();
                chrono.stop();
                long remainingTime = REFRESH_EVERY_MS - chrono.getTime();
                if (remainingTime > 0) {
                    Thread.sleep(remainingTime);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
