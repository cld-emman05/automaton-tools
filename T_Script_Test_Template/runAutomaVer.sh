TEST_TITLE="Database Checking"

echo Running AutomaVer for validating the matching data between the target value and the database value

java -jar ./AutomaVer.jar "$TEST_TITLE - Event" 0 "SELECT count(*) FROM TBL_EVENT"
java -jar ./AutomaVer.jar "$TEST_TITLE - Transaction" 0 "SELECT count(*) FROM TBL_TRANSACTION"
java -jar ./AutomaVer.jar "$TEST_TITLE - Report" 0 "SELECT count(*) FROM TBL_REPORT"


# This is to ensure that all values set in the ID generator are reseted
# This can also be expounded by using the where ID from the table itself
java -jar ./AutomaVer.jar "$TEST_TITLE - ID Generator" "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" "SELECT value FROM TBL_ID_GENERATOR"