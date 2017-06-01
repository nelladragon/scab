REM Create persistent storage to be used to store blockchain in and init the blockchain.

REM create a cluster just for initializing the storage.
call gcloud container clusters create ethinit-cluster --num-nodes=1

REM create the storage
call gcloud compute disks create --size 10GB docker-eth-storage

REM verify kubectl tool is configured correctly to connect with the cluster.
call gcloud container clusters get-credentials ethinit-cluster
