package pcd.assignment1.ex2.gui;

import pcd.assignment1.common.model.Stats;


public class AnalyzerView {
    private AnalyzerUI ui;

    public AnalyzerView(Controller controller, int defaultProcCoresMultiplier, boolean isProcCoresMultiplierInputAllowed) {
        this.ui = new AnalyzerUI(controller, defaultProcCoresMultiplier, isProcCoresMultiplierInputAllowed);
    }

    public synchronized void updateStats(Stats.Snapshot snapshot) {
        ui.updateStats(snapshot);
    }

    public synchronized void updateStatus(String text) {
        ui.updateStatus(text);
    }

    public synchronized void display() {
        ui.display();
    }

    public synchronized void reset() {
        ui.reset();
    }
}
