@echo off

RUNAS /savecred /user:%ACCOUNT_USERNAME% "%~f0"

set /p new_date="06/27/2023"
set /p new_time="12:00:00 AM"

date="%new_date%
time=%new_time%"

echo System time and date has been updated for testing purposes