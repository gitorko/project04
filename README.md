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


Create a service account

```bash
kubectl apply -f - <<EOF
apiVersion: v1
kind: Secret
metadata:
  name: default-secret
  annotations:
    kubernetes.io/service-account.name: default
type: kubernetes.io/service-account-token
EOF
```

Edit the service account and update the last 2 lines

```bash
kubectl edit serviceaccounts default

apiVersion: v1
kind: ServiceAccount
metadata:
  creationTimestamp: "XXXX-XX-XXTXX:XX:XXZ"
  name: default
  namespace: default
  resourceVersion: "XXXX"
  uid: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
secrets:
  - name: default-secret
```
Check if token is created

```bash
kubectl describe secret default
```

Provide admin role to the service account

```bash
kubectl apply -f - <<EOF
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: default
  namespace: default
EOF
```

Build the docker image

```bash
docker build -f docker/Dockerfile --force-rm -t project04:1.0.0 .
```

Deploy to k8s

```bash
mkdir /tmp/data
kubectl apply -f docker/deployment.yaml
kubectl get pods -w

kubectl config set-context --current --namespace=default
kubectl get deployments
kubectl scale statefulset project04 --replicas=3
kubectl scale deployment project04 --replicas=1
```

Clean up

```bash
kubectl delete -f docker/deployment.yaml
```
