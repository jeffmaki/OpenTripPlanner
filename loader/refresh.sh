#!/bin/sh

cd /opt/websites/walk-shed.jeffmaki.com/_shed-loader

wget -q -O - http://localhost:8080/opentripplanner-api-webapp/ws/shed/clear
wget -q -O - http://localhost:8080/opentripplanner-api-webapp/ws/shed/load
