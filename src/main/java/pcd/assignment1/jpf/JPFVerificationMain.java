package pcd.assignment1.jpf;

import pcd.assignment1.common.util.AtomicBooleanFlag;
import pcd.assignment1.common.util.Flag;

public class JPFVerificationMain {

    public static final int MAX_WORKERS = 2;

    public static void main(String[] args) throws InterruptedException {
        String rootDirPath = "rootDir";
        String sub1Path = rootDirPath + "/sub1";
        MockDirectory rootDir = new MockDirectory(rootDirPath);
        rootDir.addEntry(new MockFile(rootDirPath + "/file1", 10));
        rootDir.addEntry(new MockFile(rootDirPath + "/file2", 20));
        MockDirectory sub1Dir = new MockDirectory(sub1Path);
        rootDir.addEntry(sub1Dir);
        sub1Dir.addEntry(new MockFile(sub1Path + "/file3", 30));
        sub1Dir.addEntry(new MockFile(sub1Path + "/file4", 40));

        Flag stopFlag = new AtomicBooleanFlag();
        MockStats mockStats = new MockStats();
        MockMasterAgent mockMasterAgent = new MockMasterAgent(MAX_WORKERS, rootDir, mockStats, stopFlag);
        MockViewUpdaterAgent mockViewUpdaterAgent = new MockViewUpdaterAgent(mockStats, stopFlag);
        mockMasterAgent.start();
        mockMasterAgent.join();
        stopFlag.set();
        mockViewUpdaterAgent.join();
        MockStats.Snapshot mockSnapshot = mockStats.getSnapshot();
//        if (mockSnapshot.getNumOfAnalyzedDirs() != 2) {
//            System.out.println("Error: Expected: 2, Actual: " + mockSnapshot.getNumOfAnalyzedDirs());
//
//        }

        int numOfAnalyzedDirs = mockSnapshot.getNumOfAnalyzedDirs();
        int numOfAnalyzedFiles = mockSnapshot.getNumOfAnalyzedFiles();
        long totLinesRead = mockSnapshot.getTotLinesRead();

        assert numOfAnalyzedDirs == 2;
        assert numOfAnalyzedFiles == 4;
        assert totLinesRead == 100;

    }
}
