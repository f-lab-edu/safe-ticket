#!/bin/sh

# Apply environment variables to the template file
envsubst < /etc/alertmanager/alertmanager.yml.template > /etc/alertmanager/alertmanager.yml

# Run Alertmanager
exec /bin/alertmanager --config.file=/etc/alertmanager/alertmanager.yml