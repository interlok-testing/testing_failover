# Failover Testing

[![license](https://img.shields.io/github/license/interlok-testing/testing_failover.svg)](https://github.com/interlok-testing/testing_failover/blob/develop/LICENSE)
[![Actions Status](https://github.com/interlok-testing/testing_failover/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/interlok-testing/testing_failover/actions/workflows/gradle-build.yml)

Project tests interlok-failover features

## What it does

This project contains three very basic Interlok instances that will form a failover cluster.

Each cluster instance will share the libraries and configuration files except for the bootstrap.properties.
We have a separate bootstrap.properties file for each instance to solve any port conflicts running multiple instances on the same physical machine. 

![failover diagram](/failover.png "failover diagram")
 
## Getting started

* `./gradlew clean build`
* `(cd ./build/distribution && java -jar lib/interlok-boot.jar --failover bootstrap.properties)`

### The logging

Once started the first instance (known as the primary) will start as usual.

```
INFO  [main] [c.a.c.m.UnifiedBootstrap.createAdapter()] Adapter created
INFO  [main] [c.a.f.SimpleBootstrap.startFailover()] Starting Interlok instance in failover mode as a secondary.
INFO  [main] [c.a.f.SimpleBootstrap.startFailover()] No secondary position has been set, one will be allocated.

-------------------------------------------------------------------
GMS: address=DESKTOP-EDI1AH1-30874, cluster=myFailoverCluster, physical address=192.168.1.133:63039
-------------------------------------------------------------------
DEBUG [Failover Monitor Thread] [c.a.f.FailoverManager.assignSecondaryNumber()] Assigning myself secondary position 1
TRACE [Failover Monitor Thread] [c.a.f.FailoverManager.checkPromotion()] Primary not available, promoting myself to primary.
INFO  [Failover Monitor Thread] [c.a.f.SimpleBootstrap.promoteToPrimary()] Promoting to PRIMARY
TRACE [Failover Monitor Thread] [c.a.c.m.j.JmxRemoteComponent.configureSecurity()] JMX Protocol=[jmxmp]
TRACE [Failover Monitor Thread] [c.a.c.m.j.JmxRemoteComponent.createJmxWrapper()] JMX Environment : {}
TRACE [Failover Monitor Thread] [c.a.c.m.j.JmxRemoteComponent.register()] MBean [com.adaptris:type=JmxConnectorServer] registered
DEBUG [JmxRemoteComponent] [c.a.c.m.j.JmxRemoteComponent.run()] Starting JMXConnectorServer : com.adaptris:type=JmxConnectorServer
DEBUG [JmxRemoteComponent] [c.a.c.m.j.JmxRemoteComponent.run()] Started JMXConnectorServer : com.adaptris:type=JmxConnectorServer
WARN  [JMX-Request-0] [c.a.c.Adapter.warnOnErrorHandlerBehaviour()] [Adapter(expressions-testing)] has a MessageErrorHandler with no behaviour; messages may be discarded upon exception
DEBUG [JMX-Request-0] [c.a.c.Adapter.registerWorkflowsInRetrier()] FailedMessageRetrier []
TRACE [JMX-Request-0] [c.a.c.ChannelList.init()] Channels that can be manipulated are: []
INFO  [JMX-Request-0] [c.a.c.Adapter.init()] Adapter(expressions-testing) Initialised
INFO  [JMX-Request-0] [c.a.c.Adapter.start()] Adapter(expressions-testing) Started
```
### Launching the 2nd and 3rd instance

```
$ java -jar lib/interlok-boot.jar --failover bootstrap.failover1.properties
$ java -jar lib/interlok-boot.jar --failover bootstrap.failover2.properties
```

Both of these instances will start-up as 'secondaries'.  if you shutdown the primary instance, one of these two will pick up the slack and start itself up.
