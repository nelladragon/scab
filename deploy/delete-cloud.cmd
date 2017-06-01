REM Based on https://cloud.google.com/container-engine/docs/tutorials/guestbook
call kubectl delete service frontend
REM Todo wait a sec
call gcloud compute forwarding-rules list
REM Todo wait a sec
call gcloud container clusters delete gateway
