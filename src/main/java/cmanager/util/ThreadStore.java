package cmanager;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

public class ThreadStore implements UncaughtExceptionHandler {

    private final List<Thread> threads = new ArrayList<>();
    private Throwable exception = null;

    public void addAndRun(Thread thread) {
        thread.setUncaughtExceptionHandler(this);
        threads.add(thread);
        thread.start();
    }

    public void joinAndThrow() throws Throwable {
        for (Thread thread : threads)
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }

        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        exception = throwable;
    }

    public int getCores(int maximum) {
        int cores = Runtime.getRuntime().availableProcessors();
        if (cores > maximum) {
            cores = maximum;
        }
        return cores;
    }
}
