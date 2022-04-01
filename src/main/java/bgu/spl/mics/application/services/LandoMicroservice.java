package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.bombDestroyEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long landoDuration;

    public LandoMicroservice(long duration) {
        super("Lando");
    }

    @Override
    protected void initialize() {
        Diary diary=Diary.getInstance();
        super.subscribeEvent(bombDestroyEvent.class, (bombDestroyEvent c) -> {
            try{
                Thread.sleep(landoDuration);
            }
            catch (InterruptedException e){}
            complete(c,true);
            sendBroadcast(new TerminationBroadcast());
        });
        super.subscribeBroadcast(TerminationBroadcast.class,(TerminationBroadcast b) ->{
            diary.setDiaryMap("LandoTerminate",System.currentTimeMillis());
            this.terminate();
        });
    }
}
