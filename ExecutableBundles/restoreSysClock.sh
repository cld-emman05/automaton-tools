echo "Restore System Time"
sudo -u root service ntp start
sudo -u root hwclock -s
exit