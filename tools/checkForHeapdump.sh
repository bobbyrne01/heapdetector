#!/bin/bash

INPUT=/usr/local/CustomAppResults/HeapDetector/targets.txt
IFS=","
mailBody=""
recipientsFilename="/usr/local/CustomAppResults/HeapDetector/recipients.txt"
recipients=""

#get recipients for automated email
while read line
do
        recipients="$line"
done < "$recipientsFilename"

#Check targets .csv file exists
[ ! -f $INPUT ] && { echo "$INPUT file not found"; exit 99; }

#Loop through each server
while read target path
do
	windowsFlag=1

	# Determine target OS, and check for heapdump files
	result=$(staf "$target" PROCESS START SHELL COMMAND 'uname' WAIT RETURNSTDOUT STDERRTOSTDOUT)
	compare="not recognized as an internal or external command"
	if [[ $result == *$compare* ]]
	then
		# Windows target
		result=$(python /home/user/webapps/HeapDetector/tools/remoteStafCommand.py $target "dir $path*heapdump* /B")
		resultCode=$?
		windowsFlag=0
	else
		# Linux target
		result=$(python /home/user/webapps/HeapDetector/tools/remoteStafCommand.py "$target" "ls $path*heapdump*")
		resultCode=$?
	fi


	heapNotFoundLinux="No such file or directory"
	heapNotFoundWindows="Not Found"
        if [[ $result == *heapdump.* ]]
        then
                echo "Found a heap dump! Possible OOM issue detected."

                IFS="
"

                #Check if heapdump has already been logged
                for file in $result
                do
                        r=$(grep "$target" /usr/local/CustomAppResults/HeapDetector/heapdumps.txt | grep "$file")

                        if [ $? -ne 0 ]
                        then
				echo "Not yet logged in tool, doing now."

				if [ $windowsFlag -eq 0 ]
				then
					echo "$target:$path$file" | cat - /usr/local/CustomAppResults/HeapDetector/heapdumps.txt > /usr/local/CustomAppResults/HeapDetector/tempheapdumps.txt && mv /usr/local/CustomAppResults/HeapDetector/tempheapdumps.txt /usr/local/CustomAppResults/HeapDetector/heapdumps.txt
					mailBody="$mailBody\n$target:$path$file"
				else
					echo "$target:$file" | cat - /usr/local/CustomAppResults/HeapDetector/heapdumps.txt > /usr/local/CustomAppResults/HeapDetector/tempheapdumps.txt && mv /usr/local/CustomAppResults/HeapDetector/tempheapdumps.txt /usr/local/CustomAppResults/HeapDetector/heapdumps.txt
					mailBody="$mailBody\n$target:$file"
				fi
			else
				echo "Already logged by tool, ignoring."
                        fi
                done

        elif [[ $result == *$heapNotFoundLinux* ]] || [[ $result == *$heapNotFoundWindows* ]]
        then
                echo "No Heap dumps detected on $target"
        else
                now=`date '+%H:%M:%S %d-%m-%Y'`
                echo "$now Could not connect to $target, return code: $resultCode"
                echo "$now Could not connect to $target, return code: $resultCode" | cat - /usr/local/CustomAppResults/HeapDetector/issues.txt > /usr/local/CustomAppResults/HeapDetector/tempissues.txt && mv /usr/local/CustomAppResults/HeapDetector/tempissues.txt /usr/local/CustomAppResults/HeapDetector/issues.txt
        fi

        IFS=","
done < $INPUT

echo "$mailBody"
if [ "$mailBody" == "" ]
then
        echo "No new heap dumps, will not notify engineers."
else
	echo "Notifying engineers of newly found heap dump .."
	echo "$recipients"
	echo -e "Possible outOfMemory issue(s) detected ..\n$mailBody\n\n\nAlso logged here: http://localhost:8080/HeapDetector/" | mail -s "Heap dump(s) found" "$recipients" -- -r "HeapDetector"
fi
