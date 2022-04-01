package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.EOFException;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    private Ewok ewok;

    @BeforeEach
    void setUp() {
        ewok=new Ewok(1,true);
    }

    @Test
    void acquire() {
        assertTrue(ewok.available);
        ewok.acquire();
        assertFalse(ewok.available);
    }

    @Test
    void release() {
        ewok.acquire();
        assertFalse(ewok.available);
        ewok.release();
        assertTrue(ewok.available);
    }
}