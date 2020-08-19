package cmanager.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/** Utility class for stacking/queuing frames on top of each other. */
public class FrameHelper {

    /** The order in which the frames appear. */
    private static final List<JFrame> reactivationQueue = new ArrayList<>();

    /**
     * Add the given frame to the queue.
     *
     * @param frame The frame to add.
     */
    private static synchronized void addToQueue(final JFrame frame) {
        reactivationQueue.add(frame);
    }

    /**
     * Check whether the given frame is the first one in the queue.
     *
     * @param frame The frame to check for.
     * @return Whether the given frame is the first one in the queue.
     */
    private static synchronized boolean isFirstInQueue(final JFrame frame) {
        return reactivationQueue.get(reactivationQueue.size() - 1) == frame;
    }

    /**
     * Remove the given frame from the queue.
     *
     * @param frame The frame to remove.
     */
    private static synchronized void removeFromQueue(final JFrame frame) {
        reactivationQueue.remove(frame);
    }

    /**
     * Show a modal frame.
     *
     * <p>This will hide the parent frame, show the new frame in the foreground and wait for it to
     * be closed. Then the parent frame will be shown again.
     *
     * @param newFrame The new frame to show.
     * @param owner The parent frame.
     */
    public static void showModalFrame(final JFrame newFrame, final JFrame owner) {
        addToQueue(owner);

        newFrame.setLocationRelativeTo(owner);
        owner.setVisible(false);
        owner.setEnabled(false);
        newFrame.setVisible(true);
        newFrame.toFront();

        Thread thread =
                new Thread(
                        () -> {
                            while (newFrame.isVisible() || !isFirstInQueue(owner)) {
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException ignored) {
                                }
                            }

                            owner.setVisible(true);
                            owner.setEnabled(true);
                            owner.toFront();
                            removeFromQueue(owner);
                        });
        thread.start();
    }
}
