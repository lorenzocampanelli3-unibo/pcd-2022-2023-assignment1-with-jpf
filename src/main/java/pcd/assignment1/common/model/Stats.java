package pcd.assignment1.common.model;

import pcd.assignment1.common.util.Pair;
import pcd.assignment1.common.util.Range;

import java.nio.file.Path;
import java.util.*;

public class Stats {

    private static final Comparator<Pair<String, Long>> DESC_NUM_LINES_ORDER_COMPARATOR = (o1, o2) -> {
        int countComparison = o2.getY().compareTo(o1.getY());
        return countComparison == 0 ? o1.getX().compareTo(o2.getX()) : countComparison;
    };

    private Set<Pair<String, Long>> filesLinesCount;
    private Map<Range, Long> distribution;

    private int numIntervals;
    private int maxLines;
    private int topN;

    private int numOfAnalyzedDirs;
    private int numOfAnalyzedFiles;

    private Path rootPath;

    public Stats() {

    }

    public void init(Path rootPath, int numIntervals, int maxLines, int topN) {
        this.numIntervals = numIntervals;
        this.maxLines = maxLines;
        this.topN = topN;
        this.rootPath = rootPath;
        this.numOfAnalyzedDirs = 0;
        this.numOfAnalyzedFiles = 0;
        this.filesLinesCount = new TreeSet<>(DESC_NUM_LINES_ORDER_COMPARATOR);
        this.distribution = initDistribution(maxLines, numIntervals);
    }

    public synchronized void updateFileStats(Path filePath, long linesCount) {
        Pair<String, Long> newFile = new Pair<>(rootPath.relativize(filePath).toString(), linesCount);
        //TODO: maybe remove contains check
        if (!this.filesLinesCount.contains(newFile)) {
            this.filesLinesCount.add(newFile);
        } else {
            throw new IllegalArgumentException(filePath + "has already been added to the files count.");
        }
        Optional<Range> optBucket = this.distribution.keySet().stream().filter(key -> key.contains(linesCount)).findAny();
        if (optBucket.isPresent()) {
            Range bucket = optBucket.get();
            this.distribution.put(bucket, this.distribution.get(bucket) + 1);
        } else {
            throw new IllegalArgumentException("The lines count: " + linesCount + "does not belong to any range");
        }
        this.numOfAnalyzedFiles++;
    }

    public synchronized void updateDirStats() {
        this.numOfAnalyzedDirs++;
    }

    public synchronized int getNumOfAnalyzedDirs() {
        return numOfAnalyzedDirs;
    }

    public synchronized int getNumOfAnalyzedFiles() {
        return numOfAnalyzedFiles;
    }

    public synchronized Snapshot getSnapshot() {
       List<Pair<String, Long>> filesLinesCountSnap = this.topN == 0 ? List.copyOf(this.filesLinesCount) :
                                                                     this.filesLinesCount.stream().limit(topN).toList();
       Map<Range, Long> distributionSnap = Collections.unmodifiableMap(new LinkedHashMap<>(this.distribution));

       return new Snapshot(filesLinesCountSnap, distributionSnap);
    }

    private Map<Range, Long> initDistribution(int maxLines, int numIntervals) {
        long numLinesPerBucket = maxLines / (numIntervals - 1);
        long remainder = maxLines % numIntervals;
        Map<Range, Long> distribution = new TreeMap<>();
        long currentRangeMin = 0;
        long currentRangeMax = 0;
        for (int i = 0; i < numIntervals - 1; i++) {
            currentRangeMax = currentRangeMin + numLinesPerBucket - 1;
            distribution.put(new Range(currentRangeMin, currentRangeMax), 0L);
            currentRangeMin = currentRangeMax + 1;
        }
        distribution.put(new Range(currentRangeMin, Long.MAX_VALUE), 0L);
        return distribution;
    }

    public static class Snapshot {
        private List<Pair<String, Long>> filesLinesCountSnap;
        private Map<Range, Long> distributionSnap;

        public Snapshot(List<Pair<String, Long>> filesLinesCountSnap, Map<Range, Long> distributionSnap) {
            this.filesLinesCountSnap = filesLinesCountSnap;
            this.distributionSnap = distributionSnap;
        }

        public List<Pair<String, Long>> getFilesLinesCountSnap() {
            return filesLinesCountSnap;
        }

        public Map<Range, Long> getDistributionSnap() {
            return distributionSnap;
        }
    }
}
