#!/bin/bash
# make sure the pipe exists
if [ ! -e "$pipe" ]; then
     echo "This script must started in service mode before lock status can be queried."
     exit
fi

# send a request for screen lock status and read the response
touch "$pipe"
read status < "$pipe"

[ "$status" == "" ] && status="This script must started in service mode before lock status can be queried."
# print reponse to screen for use by calling application
echo $status
