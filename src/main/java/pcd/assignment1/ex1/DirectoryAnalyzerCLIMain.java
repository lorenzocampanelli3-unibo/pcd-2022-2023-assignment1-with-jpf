package pcd.assignment1.ex1;

import com.johncsinclair.consoletable.ConsoleTable;
import com.johncsinclair.consoletable.Styles;
import pcd.assignment1.common.model.Stats;
import pcd.assignment1.common.util.*;
import pcd.assignment1.ex1.agents.MasterAgent;

import static pcd.assignment1.common.util.Log.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class DirectoryAnalyzerCLIMain {

    private static final int MAX_NUM_WORKERS = Runtime.getRuntime().availableProcessors() * 4;
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length < 3) {
            System.out.println("Missing command line arguments.");
            System.exit(1);
        }
        String D = args[0];
        int NI = Integer.parseInt(args[1]);
        int MAXL = Integer.parseInt(args[2]);
        int topN = 0;
        if (args.length > 3) {
            topN = Integer.parseInt(args[3]);
        }
        Path rootDir = Paths.get(D);
        if (!Files.exists(rootDir)) {
            System.err.println("The specified directory does not exist.");
            System.exit(1);
        }
        log("Starting with params:\n" +
                "rootDir = " + D + ", NI: " + NI + ", MAXL: " + MAXL + ", topN: " + topN);
        Flag stopFlag = new AtomicBooleanFlag();
        Stats stats = new Stats();
        stats.init(rootDir, NI, MAXL, topN);
        MasterAgent master = new MasterAgent(MAX_NUM_WORKERS, rootDir, stats, stopFlag);
        Chrono chrono = new Chrono();
        chrono.start();
        master.start();
        // STOP TEST
//        Thread.sleep(50);
//        stopFlag.set();

        master.join();
        chrono.stop();
        Stats.Snapshot snapshot = stats.getSnapshot();
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

        Log.log("Analysis complete. Analyzed " + stats.getNumOfAnalyzedFiles() +
                " files in " + stats.getNumOfAnalyzedDirs() + " directories.");
        log("Time elapsed: " + chrono.getTime() + " ms");
        log("Main thread finished.");
//        Files.list()
    }

    private static Map<Range, Integer> initDistribution(int maxLines, int numIntervals) {
        int numLinesPerBucket = maxLines / (numIntervals - 1);
        int remainder = maxLines % numIntervals;
        Map<Range, Integer> distribution = new TreeMap<>();
        int currentRangeMin = 0;
        int currentRangeMax = 0;
        for (int i = 0; i < numIntervals - 1; i++) {
            currentRangeMax = currentRangeMin + numLinesPerBucket - 1;
            distribution.put(new Range(currentRangeMin, currentRangeMax), 0);
            currentRangeMin = currentRangeMax + 1;
        }
        distribution.put(new Range(currentRangeMin, Integer.MAX_VALUE), 0);
        return distribution;
    }
}
