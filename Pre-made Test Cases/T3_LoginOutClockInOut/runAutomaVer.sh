echo Running AutomaVer for validating the matching data between the target value and the database value

java -jar ./AutomaVer.jar "Database Checking - Event" 0 "SELECT count(*) FROM TBL_EVENT"
java -jar ./AutomaVer.jar "Database Checking - Transaction" 0 "SELECT count(*) FROM TBL_TRANSACTION"
java -jar ./AutomaVer.jar "Database Checking - Report" 0 "SELECT count(*) FROM TBL_REPORT"
