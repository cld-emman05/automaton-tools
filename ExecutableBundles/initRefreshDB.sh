#Test database filename (omit .mv.db)
test_db_file=posdb_fresh
temp_db_file=/opt/pos2/
pos_db_file=posdb

echo "Current Task: Renewing the database to a fresh state using [$test_db_file].mv.db"

# Commands
java -cp "$CLASSPATH" org.h2.tools.Script -script '/opt/pos2/dump.sql' -user sa -url jdbc:h2:./$test_db_file
rm $pos_db_file.mv.db
java -cp "$CLASSPATH" org.h2.tools.RunScript -script /opt/pos2/dump.sql -user sa -url jdbc:h2:./new_posdb
mv new_posdb.mv.db $pos_db_file.mv.db
rm dump.sql