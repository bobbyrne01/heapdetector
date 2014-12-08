#!/bin/bash

user=$(whoami)

touch /home/"$user"/HeapDetector/heapdumps.txt
touch /home/"$user"/HeapDetector/issues.txt
touch /home/"$user"/HeapDetector/recipients.txt
touch /home/"$user"/HeapDetector/schedule.txt
touch /home/"$user"/HeapDetector/targets.txt
