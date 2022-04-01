package bgu.spl.mics.application.services;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }

    @Override
    protected void initialize() {
        Diary diary=Diary.getInstance();
        super.subscribeEvent(AttackEvent.class, (AttackEvent c) -> {
            List<Integer> busyEwoks= Ewoks.getInstance().askForEwok(c.getAttack().getSerials());
            try{
                Thread.sleep(c.getAttack().getDuration());
            }
            catch (InterruptedException e){}
            Ewoks.getInstance().releaseEwoks(busyEwoks);
            complete(c,true);
            diary.getTotalAttacks().incrementAndGet();
            c.getAttackCounter().countDown();
            if(c.getAttackCounter().getCount()==1||c.getAttackCounter().getCount()==0)
                diary.setDiaryMap("HanSoloFinish",System.currentTimeMillis());
        });
        super.subscribeBroadcast(TerminationBroadcast.class,(TerminationBroadcast b) ->{
            diary.setDiaryMap("HanSoloTerminate",System.currentTimeMillis());
            this.terminate();
        });
    }
}
