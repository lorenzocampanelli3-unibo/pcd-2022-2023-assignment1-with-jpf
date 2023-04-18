package pcd.assignment1.common.tasks;

import pcd.assignment1.common.pool.CustomThreadPool;
import pcd.assignment1.common.model.Stats;

import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;

public class DirectoryListingTask implements Task {

    private static final PathMatcher JAVA_FILE_TYPE_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.java");
    private static final DirectoryStream.Filter<Path> FILTER  = path -> Files.isDirectory(path) || JAVA_FILE_TYPE_MATCHER.matches(path);
    private Path directoryPath;
    private CustomThreadPool pool;

    private Stats stats;

    public DirectoryListingTask(Path directoryPath, CustomThreadPool pool, Stats stats) {
        this.directoryPath = directoryPath;
        this.pool = pool;
        this.stats = stats;
    }

    @Override
    public void run() {
        boolean analysisStarted = false;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, FILTER)) {
//            Log.log("Analyzing directory: " + directoryPath);
            Iterator<Path> iterator = stream.iterator();
            while (!pool.isShutDown() && iterator.hasNext()) {
                if (!analysisStarted) {
                    analysisStarted = true;
                }
                Path p = iterator.next();
                if (Files.isDirectory(p)) {
                    pool.execute(new DirectoryListingTask(p, pool, stats));
                } else {
                    pool.execute(new LinesCountTask(p, pool, stats));
                }
            }
//            Log.log("Directory " + directoryPath + " successfully analyzed");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        /*try (Stream<Path> dirStream = Files.list(directoryPath)) {
            dirStream
                    .filter(entry -> Files.isDirectory(entry) || JAVA_FILE_TYPE_MATCHER.matches(entry))
                    .forEach(entry -> pool.execute(Files.isDirectory(entry) ? new DirectoryListingTask(entry, pool, stats) :
                                                                              new LinesCountTask(entry, pool, stats)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        if (analysisStarted) {
            this.stats.updateDirStats();
        }
    }

    @Override
    public String toString() {
        return "DirectoryListingTask{" +
                "directoryPath=" + directoryPath +
                '}';
    }
}
