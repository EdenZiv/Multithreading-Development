package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

   private Broadcast broadcast;
    private AttackEvent attackEvent;
    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;

    @BeforeEach
    void setUp() {
        attackEvent= new AttackEvent();
        broadcast= new TerminationBroadcast();
        messageBus= MessageBusImpl.getInstance();
        microService1 = new MicroService("first") {
            @Override
            protected void initialize() {

            }
        };
        microService2 = new MicroService("two") {
            @Override
            protected void initialize() {

            }
        };
    }

    @Test
    void subscribeEvent() {
       messageBus.register(microService1);
       messageBus.subscribeEvent(attackEvent.getClass(),microService1);
       messageBus.sendEvent(attackEvent);
       try {
           assertEquals(messageBus.awaitMessage(microService1), attackEvent);
       }
       catch (InterruptedException e){
           System.out.println("something went wrong");
       }
    }

    @Test
    void subscribeBroadcast() {
        messageBus.register(microService1);
        messageBus.register(microService2);
        messageBus.subscribeBroadcast(broadcast.getClass(),microService1);
        messageBus.subscribeBroadcast(broadcast.getClass(),microService2);
        messageBus.sendBroadcast(broadcast);
        try {
            assertEquals(messageBus.awaitMessage(microService1), broadcast);
            assertEquals(messageBus.awaitMessage(microService2), broadcast);
        }
        catch (InterruptedException e){
            System.out.println("something went wrong");
        }
    }

    @Test
    void complete() {
        messageBus.register(microService1);
        messageBus.subscribeEvent(attackEvent.getClass(),microService1);
        Future <Boolean> f= messageBus.sendEvent(attackEvent);
        messageBus.complete(attackEvent, true);
        assertEquals(f.get(),true);
        assertTrue(f.isDone());
    }

    @Test
    void sendBroadcast() {
        messageBus.register(microService1);
        messageBus.register(microService2);
        messageBus.subscribeBroadcast(broadcast.getClass(),microService1);
        messageBus.subscribeBroadcast(broadcast.getClass(),microService2);
        messageBus.sendBroadcast(broadcast);
        try {
            assertEquals(messageBus.awaitMessage(microService1), broadcast);
            assertEquals(messageBus.awaitMessage(microService2), broadcast);
        }
        catch (InterruptedException e){
            System.out.println("something went wrong");
        }
    }

    @Test
    void sendEvent() {
        messageBus.register(microService1);
        messageBus.subscribeEvent(attackEvent.getClass(),microService1);
        messageBus.sendEvent(attackEvent);
        try {
            assertEquals(messageBus.awaitMessage(microService1), attackEvent);
        }
        catch (InterruptedException e){
            System.out.println("something went wrong");
        }
    }
    
    @Test
    void awaitMessage() {
        messageBus.register(microService1);
        messageBus.subscribeEvent(attackEvent.getClass(),microService1);
        messageBus.sendEvent(attackEvent);
        try {
            assertTrue((messageBus.awaitMessage(microService1) instanceof AttackEvent));
        }
        catch (InterruptedException e){
            System.out.println("something went wrong");
        }
    }
}