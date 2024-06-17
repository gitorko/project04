# Project 04

Distributed Locking - Apache Ignite

[https://gitorko.github.io/distributed-locking-apache-ignite/](https://gitorko.github.io/distributed-locking-apache-ignite/)

### Version

Check version

```bash
$java --version
openjdk 21.0.3 2024-04-16 LTS
```


### Dev

To run the code.

```bash
./gradlew clean build
./gradlew bootRun
./gradlew bootJar
```

To run many node instances

```bash
cd build/libs
java -jar project04-1.0.0.jar --server.port=8081 --ignite.nodeName=node1
java -jar project04-1.0.0.jar --server.port=8082 --ignite.nodeName=node2
java -jar project04-1.0.0.jar --server.port=8083 --ignite.nodeName=node3

```

JVM tuning parameters

```bash
java -jar -Xms1024m -Xmx2048m -XX:MaxDirectMemorySize=256m -XX:+DisableExplicitGC -XX:+UseG1GC -XX:+ScavengeBeforeFullGC -XX:+AlwaysPreTouch project04-1.0.0.jar --server.port=8080 --ignite.nodeName=node0
```
