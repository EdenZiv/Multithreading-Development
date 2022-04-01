package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MessageBusImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    protected HashMap<Integer,Ewok> ewoksCollection;

    private static class ewoksSingeltonHolder{  //singelton implementation
        private static final Ewoks instance= new Ewoks();
    }

    private Ewoks(){  //constructor
        ewoksCollection=new HashMap<>();
    }

    public static Ewoks getInstance(){  //instance of the singelton object
        return ewoksSingeltonHolder.instance;
    }

    public void setEwoksCollection(int numOfEwoks){
        for (int i=1;i<=numOfEwoks;i++)
            ewoksCollection.put(i,new Ewok(i,true));
    }

    public List<Integer> askForEwok(List<Integer> resources){
        List<Integer> acquiredEwoks=new ArrayList<>();
        for (Integer i : resources) { //to check if all the ewoks is available
            while (!ewoksCollection.get(i).available) {
                synchronized (ewoksCollection.get(i)) {
                    try {
                        ewoksCollection.get(i).wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            ewoksCollection.get(i).acquire();
            acquiredEwoks.add(i);
        }
        return acquiredEwoks;  //returns the ewoks list that are acquired
    }

    public void releaseEwoks(List<Integer> acquiredEwoks){
        for (Integer i:acquiredEwoks) {
            ewoksCollection.get(i).release();
            synchronized (ewoksCollection.get(i)){
                ewoksCollection.get(i).notifyAll();
            }
        }
    }
}
