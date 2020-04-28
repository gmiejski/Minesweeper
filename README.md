# Minesweeper

Plan:

* [x] Minesweeper game actions
* [x] Minesweeper game flag field as mine
* [x] Minesweeper game deflag a field 
* [x] Generate game
* [x] start new game
* [x] make an action to a given game
* [x] Win a game
* [x] cannot perform any action when game is lost or won
* [x] run DB on k8s locally?
* [ ] run locally on k8s with one node
* [ ] ES - compare and set with events based on versions 

Later or never:

* [ ] Generate game based on levels

Creating MongoDB:

* `helm repo add stable https://kubernetes-charts.storage.googleapis.com/`
* `helm install  minesweeper-mongo stable/mongodb-replicaset`
* verification:
```
1. After the statefulset is created completely, one can check which instance is primary by running:

    $ for ((i = 0; i < 3; ++i)); do kubectl exec --namespace default minesweeper-mongo-mongodb-replicaset-$i -- sh -c 'mongo --eval="printjson(rs.isMaster())"'; done

2. One can insert a key into the primary instance of the mongodb replica set by running the following:
    MASTER_POD_NAME must be replaced with the name of the master found from the previous step.

    $ kubectl exec --namespace default MASTER_POD_NAME -- mongo --eval="printjson(db.test.insert({key1: 'value1'}))"

3. One can fetch the keys stored in the primary or any of the slave nodes in the following manner.
    POD_NAME must be replaced by the name of the pod being queried.

    $ kubectl exec --namespace default POD_NAME -- mongo --eval="rs.slaveOk(); db.test.find().forEach(printjson)"
```

## Running:

* test: `./gradlew clean test`
* test in docker: `./script/test-ci`
* run locally:
  * in Intellij -> first run `make local-development`
  * in docker -> `make build-and-run`
* deploy to k8s locally:
  * `make deploy-local`
  

