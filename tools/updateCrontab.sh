#!/bin/bash

updatedCrontab=""
tmpfile=$(crontab -l)

while read -r line; do
        if [[ "$line" == *HeapDetector/tools/checkForHeapdump.sh* ]]
        then
                updatedCrontab+="0 $1 * * * /home/user/webapps/HeapDetector/tools/checkForHeapdump.sh\n"
        else
                updatedCrontab+="$line\n"
        fi
done <<< "$tmpfile"

if [[ "$updatedCrontab" != *HeapLogger/tools/checkForHeapdump.sh* ]]
then
    updatedCrontab+="0 $1 * * * /home/user/webapps/HeapDetector/tools/checkForHeapdump.sh\n"
fi

echo -e "$updatedCrontab" | crontab
