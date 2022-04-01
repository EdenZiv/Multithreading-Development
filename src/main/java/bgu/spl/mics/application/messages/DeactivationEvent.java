package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import java.util.concurrent.CountDownLatch;

public class DeactivationEvent implements Event<Boolean> {
    private CountDownLatch deactivationCounter;

    public DeactivationEvent(CountDownLatch deactivationCounter){
        this.deactivationCounter=deactivationCounter;
    }

    public CountDownLatch getDeactivationCounter() {
        return deactivationCounter;
    }
}
