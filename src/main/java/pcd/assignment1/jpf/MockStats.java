package pcd.assignment1.jpf;

import pcd.assignment1.common.util.Pair;
import pcd.assignment1.common.util.Range;

import java.nio.file.Path;
import java.util.*;

public class MockStats {

    private long totLinesRead;
    private int numOfAnalyzedDirs;
    private int numOfAnalyzedFiles;

    public MockStats() {
        this.totLinesRead = 0;
        this.numOfAnalyzedDirs = 0;
        this.numOfAnalyzedDirs = 0;
    }

    public synchronized void updateFileStats(long linesCount) {
        this.numOfAnalyzedFiles++;
        this.totLinesRead += linesCount;
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
       return new Snapshot(this.numOfAnalyzedFiles, this.numOfAnalyzedDirs, this.totLinesRead);
    }

    public static class Snapshot {
        private int numOfAnalyzedFiles;
        private int numOfAnalyzedDirs;
        private long totLinesRead;

        public Snapshot(int numOfAnalyzedFiles, int numOfAnalyzedDirs, long totLinesRead) {
            this.numOfAnalyzedFiles = numOfAnalyzedFiles;
            this.numOfAnalyzedDirs = numOfAnalyzedDirs;
            this.totLinesRead = totLinesRead;
        }

        public int getNumOfAnalyzedDirs() {
            return this.numOfAnalyzedDirs;
        }

        public int getNumOfAnalyzedFiles() {
            return this.numOfAnalyzedFiles;
        }

        public long getTotLinesRead() {
            return this.totLinesRead;
        }
    }
}
