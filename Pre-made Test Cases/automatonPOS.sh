#!/bin/sh

export JAVA_HOME=/usr/lib/jvm/java-8-oracle
export PATH=$JAVA_HOME/bin:$PATH

CLASSPATH=.

########################################
#                                      #
#  Add Device Specific jar's here...   #
#                                      #
########################################

CLASSPATH=$CLASSPATH:posdevversion.jar
CLASSPATH=$CLASSPATH:lib/commons-codec-1.3.jar          
CLASSPATH=$CLASSPATH:lib/epsonupos.L90.jar    
CLASSPATH=$CLASSPATH:lib/epsonupos.T82.jar
CLASSPATH=$CLASSPATH:lib/commons-httpclient-3.1.jar     
CLASSPATH=$CLASSPATH:lib/epsonupos.P20.jar    
CLASSPATH=$CLASSPATH:lib/epsonupos.T88V.jar   
CLASSPATH=$CLASSPATH:lib/uposcommon.core.v1.13.0001.jar
CLASSPATH=$CLASSPATH:lib/commons-lang-2.1.jar           
CLASSPATH=$CLASSPATH:lib/epsonupos.P60II.jar  
CLASSPATH=$CLASSPATH:lib/epsonupos.trace.jar  
CLASSPATH=$CLASSPATH:lib/uposcommon.core.v1.13.0002.jar
CLASSPATH=$CLASSPATH:lib/commons-logging-1.1.jar        
CLASSPATH=$CLASSPATH:lib/epsonupos.P80.jar    
CLASSPATH=$CLASSPATH:lib/jpos111.jar          
CLASSPATH=$CLASSPATH:lib/uposcommon.jar
CLASSPATH=$CLASSPATH:lib/epsonupos.admin.jar            
CLASSPATH=$CLASSPATH:lib/epsonupos.T20II.jar  
CLASSPATH=$CLASSPATH:lib/jpos113.jar          
CLASSPATH=$CLASSPATH:lib/epsonupos.core.v1.13.0001.jar  
CLASSPATH=$CLASSPATH:lib/epsonupos.T20.jar    
CLASSPATH=$CLASSPATH:lib/jpos.jar             
CLASSPATH=$CLASSPATH:lib/xml-apis.jar
CLASSPATH=$CLASSPATH:lib/epsonupos.core.v1.13.0002.jar  
CLASSPATH=$CLASSPATH:lib/epsonupos.T70II.jar  
CLASSPATH=$CLASSPATH:lib/log4j-1.2.13.jar     
CLASSPATH=$CLASSPATH:lib/xmlpull-1.1.3.1.jar
CLASSPATH=$CLASSPATH:lib/epsonupos.H2000.jar            
CLASSPATH=$CLASSPATH:lib/epsonupos.T81II.jar  
CLASSPATH=$CLASSPATH:lib/pos.jar              
CLASSPATH=$CLASSPATH:lib/xpp3_min-1.1.4c.jar
CLASSPATH=$CLASSPATH:lib/epsonupos.H6000IV.jar          
CLASSPATH=$CLASSPATH:lib/epsonupos.T81.jar    
CLASSPATH=$CLASSPATH:lib/pos.v3.0001.jar 
CLASSPATH=$CLASSPATH:lib/epsonupos.jar                  
CLASSPATH=$CLASSPATH:lib/epsonupos.T82II.jar  
CLASSPATH=$CLASSPATH:lib/pos.v3.0002.jar

#Maven jars
CLASSPATH=$CLASSPATH:"maven-lib/*"

#Posiflex jars
CLASSPATH=$CLASSPATH:lib/comm.jar               
CLASSPATH=$CLASSPATH:lib/jcl_editor.jar  
CLASSPATH=$CLASSPATH:lib/jpos17-controls.jar  
CLASSPATH=$CLASSPATH:lib/posiflex.jar  
CLASSPATH=$CLASSPATH:lib/jcl.jar         
CLASSPATH=$CLASSPATH:lib/jpos17.jar           
CLASSPATH=$CLASSPATH:lib/Serialio.jar

#misc
CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/dt.jar
CLASSPATH=$CLASSPATH:$JAVA_HOME/jre/lib/rt.jar
CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/tools.jar

#Automaton jars
CLASSPATH=$CLASSPATH:"automaton-lib/*"

ENABLE_REFRESH_DB=1;
ENABLE_SYS_CLOCK_UPDATE=0;
MERGE_AUTOMAVER_RESULTS=0;
CLEAN_AUTOMATON_LOGS=1;
CURRENT_DATETIME=$(date +'%Y-%m-%d_%T')
CONTINUOUS_MODE=0

# Apply cleanup on Automaton Logs and ReceiptLogs
if [ $MERGE_AUTOMAVER_RESULTS -eq 1 ]; then
	ARCHIVE_FILENAME=./ReceiptLogs/AutomaVer_$CURRENT_DATETIME
	echo Archived previous Automaver result to "$ARCHIVE_FILENAME"
	
	rm -rf ./ReceiptLogs
	rm -rf ./AutomatonLogs
	echo "Successfully deleted AutomaVer records."
fi

# Refresh POS database from the given pos_db_file.
if [ $MERGE_AUTOMAVER_RESULTS -eq 0 ]; then
	ARCHIVE_FILENAME=./ReceiptLogs/AutomaVer_$CURRENT_DATETIME
	echo Archived previous Automaver result to "$ARCHIVE_FILENAME"
	mv ./ReceiptLogs/AutomaVer $ARCHIVE_FILENAME
	mv ./AutomatonLogs $ARCHIVE_FILENAME
	# rm -r ./ReceiptLogs/AutomaVer
	# echo "Successfully deleted AutomaVer records."
fi


# Execute pre-defined System Update (must enter root password to proceed)

if [ $ENABLE_SYS_CLOCK_UPDATE -eq 1 ]; then
	export CLASSPATH
	. ./ExecutableBundles/sysClockUpdate.sh
fi

launch() {

#Define your scripts here

# 1) Hardware Installation
TEST_CASE_FOLDER=T1_HardwareInstallation
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
sleep 5

# 2) Database Checking
TEST_CASE_FOLDER=T2_Database_Checking
refreshDatabase
./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 3) Login / Out, Clock In / Out
TEST_CASE_FOLDER=T3_LoginOutClockInOut
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 4) Vatable Transactions
TEST_CASE_FOLDER=T4_Vatable_Transactions
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 5) NonVatable_Transactions
TEST_CASE_FOLDER=T5_NonVatable_Transactions
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 6) Unreadable Barcode Transactions
TEST_CASE_FOLDER=T6_Unreadable_Barcode_Transactions
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 11) Physical Card Service Transaction
TEST_CASE_FOLDER=T11_Service_PhysicalCard
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 12) 7-Connect Service Transaction
TEST_CASE_FOLDER=T12_Service_7Connect
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 14) Menu Product
TEST_CASE_FOLDER=T14_Menu_Product
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 15) Line Item Quantity
TEST_CASE_FOLDER=T15_LineItem_Quantity
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 16) Line Item / Other Validation
TEST_CASE_FOLDER=T16_LineItem_OtherValidations
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 17) Regular to Service
TEST_CASE_FOLDER=T17_LineItem_RegularToService
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 18) Service to Regular
TEST_CASE_FOLDER=T18_LineItem_ServiceToRegular
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 19) Price Reference
TEST_CASE_FOLDER=T19_Price_Reference
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 98) Maintenance Backup
TEST_CASE_FOLDER=T98_Maintenance_Backup
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

# 99) Shutdown POS
TEST_CASE_FOLDER=T99_ShutDownPOS_Manager
refreshDatabase
java -Xms512m -Xmx768m -cp "$CLASSPATH:." -javaagent:AutomatonPOS-1.0.0-all-deps.jar=$TEST_CASE_FOLDER ph.com.apollo.pos.PosApp
#./$TEST_CASE_FOLDER/runAutomaVer.sh
sleep 5

EXIT_CODE=$?

sleep 5

echo "exit_code: $EXIT_CODE"


# restart the POS application if we came from backup or initial setup
if [ $EXIT_CODE -eq 2 ] && [ $CONTINUOUS_MODE -eq 1 ]; then
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

refreshDatabase() {
	if [ $ENABLE_REFRESH_DB -eq 1 ]; then
		export CLASSPATH
		. ./ExecutableBundles/initRefreshDB.sh
	fi
}

restore_time() {
	if [ $ENABLE_SYS_CLOCK_UPDATE -eq 1 ]; then
		sudo -v
		./restoreSysClock.sh
	fi
}

# trap restore_time INT

launch
