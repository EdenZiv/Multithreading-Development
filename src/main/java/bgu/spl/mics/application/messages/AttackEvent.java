package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AttackEvent implements Event<Boolean> {
	private final Attack attack;
	private CountDownLatch attackCounter;

	public AttackEvent(){
	    attack=null;
	    attackCounter=new CountDownLatch(0);
    }

	public AttackEvent(List<Integer> serials,int duration,CountDownLatch attackCounter){
	    attack=new Attack(serials,duration);
	    this.attackCounter=attackCounter;
    }

    public Attack getAttack(){
		return attack;
	}

	public CountDownLatch getAttackCounter() {
		return attackCounter;
	}
}