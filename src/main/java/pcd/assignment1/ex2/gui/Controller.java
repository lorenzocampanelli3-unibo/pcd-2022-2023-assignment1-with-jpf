package pcd.assignment1.ex2.gui;

import pcd.assignment1.ex2.agents.MasterAgent;
import pcd.assignment1.ex2.agents.ViewUpdaterAgent;
import pcd.assignment1.common.model.Stats;
import pcd.assignment1.common.util.AtomicBooleanFlag;
import pcd.assignment1.common.util.Flag;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller {

    private static final int NUM_PROC_CORES = Runtime.getRuntime().availableProcessors();
    private Flag stopFlag;
    private MasterAgent masterAgent;
    private ViewUpdaterAgent viewUpdaterAgent;
    private AnalyzerView view;
    private Stats stats;


    public Controller(Stats stats) {
        this.stats = stats;
        this.stopFlag = new AtomicBooleanFlag();
    }

    public synchronized void setView(AnalyzerView view) {
        this.view = view;
    }

    public synchronized void notifyStarted(String selectedDirPath, int maxLines, int numIntervals, int snapshotSize, int procCoresMultiplier) {
        stopFlag.reset();
        Path rootDirPath = Paths.get(selectedDirPath);
        stats.init(rootDirPath, numIntervals, maxLines, snapshotSize);
        this.masterAgent = new MasterAgent(NUM_PROC_CORES * procCoresMultiplier, rootDirPath, stats, view, this, stopFlag);
        this.viewUpdaterAgent = new ViewUpdaterAgent(stats, view, stopFlag);
        masterAgent.start();
        viewUpdaterAgent.start();
        view.updateStatus("Processing...");
    }

    public synchronized void notifyStopped() {
        if (!this.stopFlag.isSet()) {
            this.stopFlag.set();
            view.updateStatus("Analysis interrupted by the user.");
        }
    }

    public synchronized void notifyCompleted() {
        if (!this.stopFlag.isSet()) {
            this.stopFlag.set();
            view.updateStatus("Analysis completed.");
            view.reset();
        }
    }
}
