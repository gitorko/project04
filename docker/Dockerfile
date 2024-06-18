FROM openjdk:21
WORKDIR /var/app
COPY ./build/libs/project04-1.0.0.jar /var/app/project04-1.0.0.jar
ENV _JAVA_OPTIONS '--add-opens=jdk.management/com.sun.management.internal=ALL-UNNAMED \
                   --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED \
                   --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
                   --add-opens=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED \
                   --add-opens=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED \
                   --add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED \
                   --add-opens=java.base/java.io=ALL-UNNAMED \
                   --add-opens=java.base/java.lang=ALL-UNNAMED \
                   --add-opens=java.base/java.nio=ALL-UNNAMED \
                   --add-opens=java.base/java.time=ALL-UNNAMED \
                   --add-opens=java.base/java.util=ALL-UNNAMED \
                   --add-opens=java.base/java.util.concurrent=ALL-UNNAMED \
                   --add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED \
                   --add-opens=java.base/java.lang.invoke=ALL-UNNAMED \
                   -Dignite.kubernetes.enabled=true'
CMD [ "java", "-jar", "/var/app/project04-1.0.0.jar" ]
EXPOSE 8080
