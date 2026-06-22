#!/bin/sh

# Wait for pubsub-emulator to come up
bash -c 'while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' '$PUBSUB_SETUP_HOST')" != "200" ]]; do sleep 1; done'

curl -X PUT http://$PUBSUB_SETUP_HOST/v1/projects/our-project/topics/rm-internal-questionnaire-uac-linked
curl -X PUT http://$PUBSUB_SETUP_HOST/v1/projects/our-project/subscriptions/rm-internal-questionnaire-uac-linked-service -H 'Content-Type: application/json' -d '{"topic": "projects/our-project/topics/rm-internal-questionnaire-uac-linked"}'
