import socket
import sys
from subprocess import call


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
            data = connection.recv(16).strip()
            print >>sys.stderr, 'received "%s"' % data
            if data:
                if data == "lock":
                    print >>sys.stderr, 'client requesting lock'
                    call(["loginctl", "lock-sessions"])
                    connection.sendall("locking\n")
                elif data == "unlock":
                    print >>sys.stderr, 'client requesting unlock'
                    call(["loginctl", "unlock-sessions"])
                    connection.sendall("unlocking\n")

            else:
                print >>sys.stderr, 'no more data from', client_address
                break

    finally:
        # Clean up the connection
        connection.close()
