package pcd.assignment1.common.tasks;

import pcd.assignment1.common.pool.CustomThreadPool;
import pcd.assignment1.common.model.Stats;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class LinesCountTask implements Task{

    private Path filePath;
    private CustomThreadPool pool;
    private Stats stats;

    public LinesCountTask(Path filePath, CustomThreadPool pool, Stats stats) {
        this.filePath = filePath;
        this.pool = pool;
        this.stats = stats;
    }


    @Override
    public void run() {
        long count = 0;
       /* try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            while (!pool.isShutDown() && reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            throw new CustomThreadPoolException(e);
        }*/
        if (!pool.isShutDown()) {
            try (Stream<String> linesStream = Files.lines(filePath)) {
                count = linesStream.count();
//                log("Analyzed file: " + filePath + " Lines count: " + count);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            this.stats.updateFileStats(filePath, count);
        }
//        log("Updated file stats");
    }

    @Override
    public String toString() {
        return "LinesCountTask{" +
                "filePath=" + filePath +
                '}';
    }
}
