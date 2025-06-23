package com.adaptris.failover.test;

import com.adaptris.core.StartedState;
import com.adaptris.core.StoppedState;
import com.adaptris.testing.AdapterInstance;
import com.adaptris.testing.MultiAdapterFunctionalTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class DefaultFunctionalTest extends MultiAdapterFunctionalTest {

    @BeforeEach
    public void setUp() throws Exception {
        this.adapterStartWaitTime = 5000;
        this.adapterStartMaxWaitTime = 30000;
    }

    @Override
    public List<File> getBootstrapFiles() {
        return List.of (
                new File("config/bootstrap.properties"),
                new File("config/bootstrap.failover1.properties")
                //new File("config/bootstrap.failover2.properties")
        );
    }

    protected Void launchAdapter(AdapterInstance instance) throws Exception {
        try {
            instance.withFailover();
            return instance.launchProcess();
        } catch (Exception ex) {
            // ignore as its likely an error with the failover adapters JMX not connecting
            ex.printStackTrace();
        }
        return null;
    }

    @Test
    public void test() throws Exception {
        Thread.sleep(this.adapterStartMaxWaitTime);
        instances.forEach(instance-> {
            try {
                instance.connectJMX();
            } catch (Exception e) {
                // ignore as inevitably one adapter would be secondary
            }
        });
        AdapterInstance started = instances.stream().filter(instance -> instance.isJmxConnected() && instance.getComponentState().getClass().equals(StartedState.class)).findFirst().get();

        started.shutdown(adapterCloseMaxWaitTime);
        Thread.sleep(this.adapterStartWaitTime);
        Assertions.assertThrows(Exception.class, () -> started.getComponentState().getClass().equals(StoppedState.class));
        Thread.sleep(this.adapterStartMaxWaitTime);
        assert instances.stream().filter(instance -> !instance.equals(started)).anyMatch(ai -> {
            try {
                ai.connectJMX();
                return ai.getComponentState().getClass().equals(StartedState.class);
            } catch (Exception ex) {
                return false;
            }
        });
    }

    @Override
    protected void shutdownAdapter() throws Exception {
        try {
            super.shutdownAdapter();
        } catch (Exception ex) {
            // ignore
        }

    }
}
