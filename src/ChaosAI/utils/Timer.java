package ChaosAI.utils;

import tools.ElapsedCpuTimer;

/**
 * Created by blindguard on 25/05/16.
 */
public class Timer {
    private ElapsedCpuTimer timer;
    private long startingTime;
    private int timer_limit;
    private boolean advanceCheck = false;

    private long maxAdvanceDuration;
    private long totalAdvanceTime;
    private int advanceCounter;

    private long maxComputeDuration;
    private long totalComputeTime;
    private int computeCounter;

    public Timer(ElapsedCpuTimer timer) {
        this.startingTime = timer.elapsed();
        this.timer = timer;
        this.timer_limit = 5; // 5ms for linux, 10ms for windows

        this.maxAdvanceDuration = 0;
        this.maxComputeDuration = 0;

        this.totalAdvanceTime = 0;
        this.totalComputeTime = 0;

        this.computeCounter = 0;
        this.advanceCounter = 0;
    }

    public void updateTimer(ElapsedCpuTimer timer) {
        this.advanceCounter = 0;
        this.totalAdvanceTime = 0;

        this.computeCounter = 0;
        this.totalComputeTime = 0;

        this.advanceCheck = false;
        this.startingTime = timer.elapsed();
        this.timer = timer;
    }

    public ElapsedCpuTimer getTimer() {
        return this.timer;
    }

    public long elapsed() {
        return this.timer.elapsed();
    }

    public long elapsedMillis() {
        return this.timer.elapsedMillis();
    }

    public long elapsedTimeSinceStart() {
        return this.timer.elapsed() - startingTime;
    }

    public void checkAdvanceTimer() {
        if(this.advanceCheck) {
            long avgTime = 0;

            if (advanceCounter > 0)
                avgTime = totalAdvanceTime / advanceCounter;
            if(!(this.timer.remainingTimeMillis() > 2 * avgTime && this.timer.remainingTimeMillis() > timer_limit))
                throw new OutOfTimeException(this.timer.remainingTimeMillis());
        }
    }

    public void checkComputeTimer() {
        long avgTime = 0;

        if(this.computeCounter > 0)
            avgTime = this.totalComputeTime / this.computeCounter;

        if(!(this.timer.remainingTimeMillis() > 2*avgTime && this.timer.remainingTimeMillis() > timer_limit)) {
            this.advanceCheck = true;
        }
    }

    public void updateOnCompute(long computeTime) {
        if(computeTime > maxComputeDuration)
            maxComputeDuration = computeTime;

        this.computeCounter++;
        this.totalComputeTime += computeTime;
    }

    public void updateOnAdvance(long advanceTime) {
        if(advanceTime > maxAdvanceDuration)
            maxAdvanceDuration = advanceTime;

        this.advanceCounter++;
        this.totalAdvanceTime += advanceTime;
    }
}

