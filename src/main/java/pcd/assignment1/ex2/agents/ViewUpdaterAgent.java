package pcd.assignment1.ex2.agents;

import pcd.assignment1.ex2.gui.AnalyzerView;
import pcd.assignment1.common.model.Stats;
import pcd.assignment1.common.util.Chrono;
import pcd.assignment1.common.util.Flag;


public class ViewUpdaterAgent extends Thread {

    private static final long REFRESH_EVERY_MS = 50;

    private Flag stopFlag;
    private Stats stats;
    private AnalyzerView view;

    private Chrono chrono;

    public ViewUpdaterAgent(Stats stats, AnalyzerView view, Flag stopFlag) {
        super("ViewUpdater-Agent");
        this.stopFlag = stopFlag;
        this.stats = stats;
        this.view = view;
        this.chrono = new Chrono();
    }

    @Override
    public void run() {
        while (!stopFlag.isSet()) {
            try {
                chrono.start();
                Stats.Snapshot snapshot = stats.getSnapshot();
                view.updateStats(snapshot);
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
