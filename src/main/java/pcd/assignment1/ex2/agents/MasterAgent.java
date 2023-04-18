package pcd.assignment1.ex2.agents;

import pcd.assignment1.common.pool.CustomDynamicThreadPoolImpl;
import pcd.assignment1.common.pool.CustomThreadPool;
import pcd.assignment1.common.util.Chrono;
import pcd.assignment1.common.util.Flag;
import pcd.assignment1.common.util.Log;
import pcd.assignment1.ex2.gui.AnalyzerView;
import pcd.assignment1.ex2.gui.Controller;
import pcd.assignment1.common.model.Stats;
import pcd.assignment1.common.tasks.DirectoryListingTask;
//import pcd.assignment1.util.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public class MasterAgent extends Thread {
//    private int maxWorkersPoolSize;
    private Stats stats;
    private Path rootDir;
    private AnalyzerView view;
    private Controller controller;
    private Flag stopFlag;
    private CustomThreadPool pool;

    private Chrono chrono;

    public MasterAgent(int maxWorkersPoolSize, Path rootDir, Stats stats, AnalyzerView view, Controller controller, Flag stopFlag) {
        super("Master-Agent");
        this.stats = stats;
        this.rootDir = rootDir;
        this.view = view;
        this.controller = controller;
        this.stopFlag = stopFlag;
        this.chrono = new Chrono();
        this.pool = new CustomDynamicThreadPoolImpl(maxWorkersPoolSize, stopFlag);
    }

    @Override
    public void run() {
        Log.log("Master Agent started.");
        if (!stopFlag.isSet()) {
            chrono.start();
            this.pool.execute(new DirectoryListingTask(rootDir, pool, stats));
            this.pool.joinAllWorkers();
            chrono.stop();
        }
        if (!stopFlag.isSet()) {
            this.controller.notifyCompleted();
        }
        Stats.Snapshot snapshot = stats.getSnapshot();
        view.updateStats(snapshot);
        this.view.updateStatus("Analyzed " + stats.getNumOfAnalyzedFiles() + " files in " + stats.getNumOfAnalyzedDirs() + " directories");
        this.view.updateStatus("Elapsed time: " + chrono.getTime() + " ms.");
        Log.log("Master Agent dying.");
        /*Stats.Snapshot snapshot = this.stats.getSnapshot();
        List<Pair<String, Long>> filesLinesCountSnap = snapshot.getFilesLinesCountSnap();
        Map<Range, Long> distributionSnap = snapshot.getDistributionSnap();

        // print the lines count table
        ConsoleTable linesCountTable = new ConsoleTable().withStyle(Styles.BASIC);
        linesCountTable.setHeaders("File Path", "# Lines");
        filesLinesCountSnap.forEach(p -> linesCountTable.addRow(p.getX(), p.getY()));
        System.out.println("LINES COUNT:");
        System.out.print(linesCountTable);

        // print the distribution table
        ConsoleTable distributionTable = new ConsoleTable().withStyle(Styles.BASIC);
        distributionTable.setHeaders("Range", "# Files");
        distributionSnap.forEach(distributionTable::addRow);
        System.out.println("DISTRIBUTION:");
        System.out.print(distributionTable);

        Log.log("Analysis complete. Analyzed " + this.stats.getNumOfAnalyzedFiles() +
                " files in " + this.stats.getNumOfAnalyzedDirs() + " directories.");*/
    }
}
