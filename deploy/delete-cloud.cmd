REM Based on https://cloud.google.com/container-engine/docs/tutorials/guestbook
kubectl delete service frontend
REM Todo wait a sec
gcloud compute forwarding-rules list
REM Todo wait a sec
gcloud container clusters delete gateway
