target_date=2023-06-27
target_time=00:00:00

# Change the date and time of the system
sudo -k
sudo -u root service ntp stop
sudo -u root  hwclock --set --date="$target_date $target_time"
sudo -u root  hwclock --hctosys

echo "2) System time and date has been updated for testing purposes"