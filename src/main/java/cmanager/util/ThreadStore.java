package cmanager.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

/** Container for handling multiple threads. */
public class ThreadStore implements UncaughtExceptionHandler {

    /** The actual threads. */
    private final List<Thread> threads = new ArrayList<>();

    /** The exception observed when executing the threads. */
    private Throwable exception = null;

    /**
     * Add the given thread and run it.
     *
     * @param thread The thread to add an run.
     */
    public void addAndRun(Thread thread) {
        thread.setUncaughtExceptionHandler(this);
        threads.add(thread);
        thread.start();
    }

    /**
     * Wait for all threads to terminate. If one of them raised an exception, throw it at the end.
     *
     * @throws Throwable One of the threads reported an error.
     */
    public void joinAndThrow() throws Throwable {
        for (final Thread thread : threads)
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }

        if (exception != null) {
            throw exception;
        }
    }

    /**
     * The given thread terminated due to the given exception.
     *
     * @param thread The thread.
     * @param throwable The exception.
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        exception = throwable;
    }

    /**
     * Get the number of available cores, with not more than the given maximum value.
     *
     * @param maximum The maximum value allowed.
     * @return The available number of cores, &lt;= `maximum`.
     */
    public int getCores(int maximum) {
        int cores = Runtime.getRuntime().availableProcessors();
        if (cores > maximum) {
            cores = maximum;
        }
        return cores;
    }
}
