#!/bin/bash
SECONDS=0
for i in $(seq 1 10000); do
        VAR_CurlReq_Status="$(curl -s -I -H 'action: throttle' http://ig5.example.com:9000/history/emp1 | grep ^HTTP)"
        DURATION=$SECONDS
        echo -e "\t [$((DURATION))s] $VAR_CurlReq_Status"
done