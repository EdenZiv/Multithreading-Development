package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long R2D2Duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        R2D2Duration=duration;
    }

    @Override
    protected void initialize() {
        Diary diary=Diary.getInstance();
        super.subscribeEvent(DeactivationEvent.class, (DeactivationEvent c) -> {
            try{
                Thread.sleep(R2D2Duration);
            }
            catch (InterruptedException e){}
            complete(c,true);
            diary.setDiaryMap("R2D2Deactivate",System.currentTimeMillis());
            c.getDeactivationCounter().countDown();
        });
        super.subscribeBroadcast(TerminationBroadcast.class,(TerminationBroadcast b) ->{
            diary.setDiaryMap("R2D2Terminate",System.currentTimeMillis());
            this.terminate();
        });
    }
}
