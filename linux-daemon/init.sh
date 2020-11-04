sudo apt install python
sed -i 's@--edit--@'"$PWD"'@' unlocker-daemon.service
mv unlocker-daemon.service /etc/systemd/system/unlocker-daemon.service

sudo systemctl daemon-reload
sudo systemctl enable unlocker-daemon
sudo systemctl start unlocker-daemon
./remote-linux-pair