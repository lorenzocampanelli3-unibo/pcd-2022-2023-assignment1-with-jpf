package pcd.assignment1.ex2;

import pcd.assignment1.common.model.Stats;
import pcd.assignment1.ex2.gui.AnalyzerView;
import pcd.assignment1.ex2.gui.Controller;

public class DirectoryAnalyzerGUIMain {

    private static final int DEFAULT_MULTIPLIER = 4;

    public static void main(String[] args) {
        Stats stats = new Stats();
        Controller controller = new Controller(stats);
        AnalyzerView view = new AnalyzerView(controller, DEFAULT_MULTIPLIER, true);
        controller.setView(view);
        view.display();
    }
}
