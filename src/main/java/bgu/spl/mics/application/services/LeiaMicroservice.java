package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.bombDestroyEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private final Attack[] attacks;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
    }

    @Override
    protected void initialize() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        Diary diary=Diary.getInstance();
        super.subscribeBroadcast(TerminationBroadcast.class,(TerminationBroadcast b) ->{
            diary.setDiaryMap("LeiaTerminate",System.currentTimeMillis());
            this.terminate();
        });
        CountDownLatch attacksLatch = new CountDownLatch(attacks.length);
        CountDownLatch deactivationLatch=new CountDownLatch(1);
        for (Attack attack : attacks)
            sendEvent(new AttackEvent(attack.getSerials(), attack.getDuration(), attacksLatch));
        try {
            attacksLatch.await();
        } catch (InterruptedException e) {
        }
        sendEvent(new DeactivationEvent(deactivationLatch));
        try {
            deactivationLatch.await();
        } catch (InterruptedException e) {
        }
        sendEvent(new bombDestroyEvent());
    }
}
