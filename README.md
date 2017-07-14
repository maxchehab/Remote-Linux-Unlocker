# remote-linux-unlocker
Remote Linux Unlocker is an android application paired with a linux daemon that allows users to unlock and lock the Ubuntu Unity lock screen.

### How to install android application
Install the android application from the [Google Play Store](https://play.google.com/store/apps/details?id=com.maxchehab.remotelinuxunlocker) or download the source from this repository.

### How to install linux daemon

```sh
$ wget https://github.com/maxchehab/remote-linux-unlocker/raw/master/linux-daemon/linux-daemon.zip

$ unzip linux-daemon.zip

$ cd linux-daemon

#edit `unlocker-daemon.service` so that the absolute path to unlocker-daemon.py is correct

$ mv unlocker-daemon.service /etc/systemd/system/unlocker-daemon

$ sudo systemctl daemon-reload

$ sudo systemctl enable unlocker-daemon

$ sudo systemctl start unlocker-daemon

$ ./remote-linux-pair
```

