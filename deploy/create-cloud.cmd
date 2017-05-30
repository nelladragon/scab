REM Based on https://cloud.google.com/container-engine/docs/tutorials/guestbook
gcloud container clusters create gateway --num-nodes=3

REM should I need to do this?
gcloud container clusters get-credentials gateway

kubectl create -f gateway-deployment.yaml
kubectl create -f gateway-service.yaml