package bgu.spl.mics.application.passiveObjects;


import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {
    private AtomicInteger totalAttacks;
    private HashMap<String, Long> diaryMap;

    private static class diarySingeltonHolder{  //singelton implementation
        private static final Diary instance= new Diary();
    }

    private Diary(){  //constructor
        totalAttacks=new AtomicInteger(0);
        diaryMap=new HashMap<>();
        diaryMap.put("HanSoloFinish",(long)0);
        diaryMap.put("C3POFinish",(long)0);
        diaryMap.put("R2D2Deactivate",(long)0);
        diaryMap.put("LeiaTerminate",(long)0);
        diaryMap.put("HanSoloTerminate",(long)0);
        diaryMap.put("C3POTerminate",(long)0);
        diaryMap.put("R2D2Terminate",(long)0);
        diaryMap.put("LandoTerminate",(long)0);
    }

    public static Diary getInstance(){  //instance of the singelton object
        return Diary.diarySingeltonHolder.instance;
    }

    public void setDiaryMap(String s,Long b){
        diaryMap.put(s,b);
    }

    public AtomicInteger getTotalAttacks() {
        return totalAttacks;
    }

    public Long getHanSoloFinish() {
        return diaryMap.get("HanSoloFinish");
    }

    public Long getC3POFinish() {
        return diaryMap.get("C3POFinish");
    }

    public Long getR2D2Deactivate() { return diaryMap.get("R2D2Deactivate"); }

    public Long getLeiaTerminate() { return diaryMap.get("LeiaTerminate"); }

    public Long getHanSoloTerminate() {
        return diaryMap.get("HanSoloTerminate");
    }

    public Long getC3POTerminate() {
        return diaryMap.get("C3POTerminate");
    }

    public Long getR2D2Terminate() {
        return diaryMap.get("R2D2Terminate");
    }

    public Long getLandoTerminate() {
        return diaryMap.get("LandoTerminate");
    }

    public void resetNumberAttacks(){totalAttacks.getAndSet(0);}
}
