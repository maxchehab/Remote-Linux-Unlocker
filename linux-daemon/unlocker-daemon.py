# -*- coding: utf-8 -*-
import socket
import sys
import json

from pprint import pprint
import subprocess

def is_json(myjson):
    try:
        json_object = json.loads(myjson)
    except ValueError, e:
        return False
    return True


def is_locked():
    users = [i.split(':') for i in open('/etc/shadow').readlines()]
    user = [i[0] for i in users if i[1] not in ('!', '*')][0]

    commands = 'su ' + user + ' -c -- "gdbus call -e -d com.canonical.Unity -o /com/canonical/Unity/Session -m com.canonical.Unity.Session.IsLocked"'
    p = subprocess.Popen(commands,stdout=subprocess.PIPE, shell=True)
    if p.communicate().count("true"):
        return True
    else:
        return False
    return False



# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

# Bind the socket to the port
server_address = ('', 61599)
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
                if data["command"] == "lock":
                    print >>sys.stderr, 'client requesting lock'
                    subprocess.call(["loginctl", "lock-sessions"])
                    connection.sendall('{"status":"success"}')
                elif data["command"] == "unlock":
                    print >>sys.stderr, 'client requesting unlock'
                    subprocess.call(["loginctl", "unlock-sessions"])
                    connection.sendall('{"status":"success"}')
                elif data["command"] == "echo":
                    print >>sys.stderr, 'client requesting unlock'
                    connection.sendall('{"status":"success","hostname":"' + socket.gethostname() +  '","isLocked":"' + str(is_locked()) + '"}')

            else:
                print >>sys.stderr, 'no more data from', client_address
                break

    finally:
        # Clean up the connection
        connection.close()
