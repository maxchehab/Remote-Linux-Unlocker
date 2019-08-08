# -*- coding: utf-8 -*-
'''This daemon listens on a designated port (61599) and can respond to requests
to get the status of the screensaver and lock/unlock the screensaver.

It's low on security, but high on convenience!

If multiple users are running graphical sessions it's more or less random which
user will be unlocked. See get_bus_and_uid for discussion.
'''

import socket, sys, json, subprocess, os
import dbus
from pprint import pprint
import pgrep
import psutil

PORT = 61599


def get_bus_and_uid():
    '''Find the first running process with a DBUS session address run by
    a non-daemon user, and return both the DBUS_SESSION_BUS_ADDRESS and the
    uid.'''

    DBUS_ADDRESS = 'DBUS_SESSION_BUS_ADDRESS'
    UIDS = 'uids'

    # Find all non-daemon users.
    all_users = [i.split(':') for i in open('/etc/shadow').readlines()]
    users = [i[0] for i in all_users if i[1] not in ('!', '*')]

    # Find the first non-daemon user process with a DBUS_SESSION_BUS_ADDRESS
    # in it's environment.
    user_address = {}
    for proc in psutil.process_iter():
        try:
            pinfo = proc.as_dict(attrs=['pid', 'username', UIDS])
        except psutil.NoSuchProcess:
            pass
        user = pinfo['username']
        # Ignore process run by daemons.
        if user not in users:
            continue
        environ = psutil.Process(pid=pinfo['pid']).environ()
        if DBUS_ADDRESS in environ:
            # pinfo[uids] returns real, effective and saved uids.
            return environ[DBUS_ADDRESS], pinfo[UIDS][0]
    return None, None


def is_json(myjson):
    try:
        json_object = json.loads(myjson)
    except ValueError, e:
        return False
    return True


def get_interface():
    session_bus = dbus.SessionBus()
    screensaver_list = ['org.gnome.ScreenSaver',
                        'org.cinnamon.ScreenSaver',
                        'org.kde.screensaver',
                        'org.freedesktop.ScreenSaver']
    for each in screensaver_list:
        try:
            object_path = '/{0}'.format(each.replace('.', '/'))
            get_object = session_bus.get_object(each, object_path)
            return dbus.Interface(get_object, each)
        except dbus.exceptions.DBusException:
            pass

def is_locked():
    interface = get_interface()
    return bool(interface.GetActive())

def lock(state):
    interface = get_interface()
    return bool(interface.SetActive(state))


def authenticate_key(key):
    with open(os.path.dirname(os.path.realpath(__file__)) + '/keys.db') as file:
        for line in file:
            if line.strip() == key:
                return True
                break

    return False


# Set the environment variables and uid of our user session.
DBUS_ADDRESS, UID = get_bus_and_uid()
if not DBUS_ADDRESS:
    sys.exit('No DBUS_SESSION_BUS_ADDRESS found.')
os.environ['DBUS_SESSION_BUS_ADDRESS'] = DBUS_ADDRESS
os.seteuid(UID)


# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

# Bind the socket to the port
server_address = ('', PORT)
print >>sys.stderr, 'starting up on %s port %s' % server_address
sock.bind(server_address)

# Listen for incoming connections
sock.listen(1)

while True:
    # Wait for a connection
    print >>sys.stderr, 'waiting for a connection'
    connection, client_address = sock.accept()

    try:
        print >>sys.stderr, 'connection from', client_address

        # Receive the data in small chunks and retransmit it
        while True:
            data = connection.recv(256).strip()
            print >>sys.stderr, 'received "%s"' % data
            if is_json(data):
                data = json.loads(data)
                if data["command"] == "lock" and data["key"] and authenticate_key(data["key"]):
                    print >>sys.stderr, 'client requesting lock'
                    lock(True)
                    connection.sendall('{"status":"success"')
                    break
                elif data["command"] == "unlock" and data["key"] and authenticate_key(data["key"]):
                    print >>sys.stderr, 'client requesting unlock'
                    lock(False)
                    connection.sendall('{"status":"success"')
                    break
                elif data["command"] == "status" and data["key"] and authenticate_key(data["key"]):
                    print >>sys.stderr, 'client requesting echo'
                    response = '{"status":"success","hostname":"' + socket.gethostname() +  '","isLocked":"' + str(is_locked()) + '"}';
                    print >> sys.stderr, response
                    connection.sendall(response)
                    break

            else:
                print >>sys.stderr, 'no more data from', client_address
                break

    finally:
        # Clean up the connection
        connection.close()
