package app.freerouting.gui;

import app.freerouting.logger.FRLogger;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.OK_OPTION;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        handleException(e);
    }

    public static void handleException(Throwable e) {
        // Here you should have a more robust, permanent record of problems
        JOptionPane.showMessageDialog(findActiveFrame(), e.toString(), "Exception Occurred", OK_OPTION);
        FRLogger.error(e.getLocalizedMessage(), e);
    }

    private static Frame findActiveFrame() {
        Frame[] frames = JFrame.getFrames();
        for (Frame frame : frames) {
            if (frame.isVisible())
                return frame;
        }
        return null;
    }
}
