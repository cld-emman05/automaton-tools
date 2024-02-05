#!/bin/bash

#################################################################
# Utility script to match the CLASSPATH with the POS JAR version
#################################################################

ls pos*jar
POS_JAR=`ls pos*jar | head -n 1` || POS_JAR="nada"

if [ $POS_JAR = "nada" ]; then
    echo "No POS JAR found. Terminating...."
    echo "-----------------------------------------------------"
    echo "Please make sure to run this on the main directory of"
    echo "your POS installation.  POS JAR should be on the same"
    echo "level, as with the Automaton test scripts and directories."
else
    POS_JAR=`basename $POS_JAR`
	sed -i "s/posdevversion\.jar/$POS_JAR/" automatonPOS.sh
	# sed -i "s/posdevversion\.jar/$POS_JAR/" automatonPOS.bat
    echo "Scripts updated"
fi
