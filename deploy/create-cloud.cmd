REM Based on https://cloud.google.com/container-engine/docs/tutorials/guestbook
call gcloud container clusters create gateway --num-nodes=3

REM Do I need to do this each time?
call gcloud container clusters get-credentials gateway

call kubectl create -f redis-master-deployment.yaml
call kubectl create -f redis-master-service.yaml

call kubectl create -f gateway-deployment.yaml
call kubectl create -f gateway-service.yaml