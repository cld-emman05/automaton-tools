start /b cscript possocksstart.vbs
set PATH="C:\Program Files\Java\jre1.8.0_77\bin";%path%
set JAVA_HOME="C:\Program Files\Java\jre1.8.0_77"

rem ########################################
rem ##
rem #Add Device Specific jar's here... #
rem ##
rem ########################################

set CLASSPATH=.
set CLASSPATH=$pos2-53b04268.jar
set CLASSPATH=%CLASSPATH%;lib\commons-codec-1.3.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.L90.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.T82.jar
set CLASSPATH=%CLASSPATH%;lib\commons-httpclient-3.1.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.P20.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.T88V.jar
set CLASSPATH=%CLASSPATH%;lib\uposcommon.core.v1.13.0001.jar
set CLASSPATH=%CLASSPATH%;lib\commons-lang-2.1.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.P60II.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.trace.jar
set CLASSPATH=%CLASSPATH%;lib\uposcommon.core.v1.13.0002.jar
set CLASSPATH=%CLASSPATH%;lib\commons-logging-1.1.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.P80.jar
set CLASSPATH=%CLASSPATH%;lib\jpos111.jar
set CLASSPATH=%CLASSPATH%;lib\uposcommon.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.admin.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.T20II.jar
set CLASSPATH=%CLASSPATH%;lib\jpos113.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.core.v1.13.0001.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.T20.jar
set CLASSPATH=%CLASSPATH%;lib\jpos.jar 
set CLASSPATH=%CLASSPATH%;lib\xml-apis.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.core.v1.13.0002.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.T70II.jar
set CLASSPATH=%CLASSPATH%;lib\log4j-1.2.13.jar 
set CLASSPATH=%CLASSPATH%;lib\xmlpull-1.1.3.1.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.H2000.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.T81II.jar
set CLASSPATH=%CLASSPATH%;lib\pos.jar
set CLASSPATH=%CLASSPATH%;lib\xpp3_min-1.1.4c.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.H6000IV.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.T81.jar
set CLASSPATH=%CLASSPATH%;lib\pos.v3.0001.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.jar
set CLASSPATH=%CLASSPATH%;lib\epsonupos.T82II.jar
set CLASSPATH=%CLASSPATH%;lib\pos.v3.0002.jar

rem #Maven jars
set CLASSPATH=%CLASSPATH%;maven-lib\*

rem #Posiflex jars
set CLASSPATH=%CLASSPATH%;lib\comm.jar
set CLASSPATH=%CLASSPATH%;lib\jcl_editor.jar
set CLASSPATH=%CLASSPATH%;lib\jpos17-controls.jar
set CLASSPATH=%CLASSPATH%;lib\posiflex.jar
set CLASSPATH=%CLASSPATH%;lib\jcl.jar
set CLASSPATH=%CLASSPATH%;lib\jpos17.jar
set CLASSPATH=%CLASSPATH%;lib\Serialio.jar

rem #misc
set CLASSPATH=%CLASSPATH%;$JAVA_HOME\lib\dt.jar
set CLASSPATH=%CLASSPATH%;$JAVA_HOME\jre\lib\rt.jar

ENABLE_REFRESH_DB=1;
ENABLE_SYS_CLOCK_UPDATE=1;
MERGE_AUTOMAVER_RESULTS=0;
CURRENT_DATETIME=$(date +'%Y-%m-%d_%T')
CONTINUOUS_MODE=0


# Refresh POS database from the given pos_db_file.
if [ $MERGE_AUTOMAVER_RESULTS -eq 0 ]; then
	ARCHIVE_FILENAME=./ReceiptLogs/AutomaVer_$CURRENT_DATETIME
	echo Archived previous Automaver result to "$ARCHIVE_FILENAME"
	mv ./ReceiptLogs/AutomaVer $ARCHIVE_FILENAME
	# rm -r ./ReceiptLogs/AutomaVer
	# echo "Successfully deleted AutomaVer records."
fi

# Refresh POS database from the given pos_db_file.
if [ $ENABLE_REFRESH_DB -eq 1 ]; then
	export CLASSPATH
	. ./initRefreshDB.sh
fi


# Execute pre-defined System Update (must enter root password to proceed)

if [ $ENABLE_SYS_CLOCK_UPDATE -eq 1 ]; then
	export CLASSPATH
	. ./sysClockUpdate.sh
fi

launch() {

java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=myRegularTxnScript ph.com.apollo.pos.PosApp
./myRegularTxnScript/runAutomaVer.sh
sleep 5

java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=mySCPromoScript ph.com.apollo.pos.PosApp
./mySCPromoScript/runAutomaVer.sh
sleep 5

java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=myPWDPromoScript ph.com.apollo.pos.PosApp
./myPWDPromoScript/runAutomaVer.sh
sleep 5

java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=mySoloParentPromoScript ph.com.apollo.pos.PosApp
./mySoloParentPromoScript/runAutomaVer.sh
sleep 5

java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=mySampleShiftEndScript ph.com.apollo.pos.PosApp
EXIT_CODE=$?

sleep 5

echo "exit_code: $EXIT_CODE"


# restart the POS application if we came from backup or initial setup
if [ $EXIT_CODE -eq 2 and $CONTINUOUS_MODE -eq 1 ]; then
	launch
#     launch
elif [ $EXIT_CODE -eq 130 ]; then
	restore_time
else
	restore_time
#	sudo -k
#	sudo service ntp start
fi
#elif [ $EXIT_CODE -eq 88 ]; then
#	set-ntp 1
#	timedatectl set-time set-timezone "Asia/Manila"

}

restore_time() {
	if [ $ENABLE_SYS_CLOCK_UPDATE -eq 1 ]; then
		sudo -v
		./restoreSysClock.sh
	fi
}

# trap restore_time INT

launch
