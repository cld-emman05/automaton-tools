@echo off

RUNAS /savecred /user:%ACCOUNT_USERNAME% ""%~f0"

echo "Restore System Time"

net start w32time
w32tm /query /peers
w32tm /resync /nowait

echo "Time restored successfully"