#!/bin/bash
SECONDS=0
VAR_tic=$(($(date +%s%N)/1000000))
for i in $(seq 1 1000); do
        VAR_CurlReq_Status="$(curl -s -I -L -H 'action: throttle' http://ig5.example.com:9000/history/emp1 | grep ^HTTP)"
        DURATION=$SECONDS
        echo -e "\t [$((DURATION % 60))s] $VAR_CurlReq_Status"
done