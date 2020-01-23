import socket, threading

ip = input("\nIPV4 adress to bind to:\n")
port = int(input("\nPort to bind to:\n"))
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

#====================================#

def receiveMessages(sock):
    # Thread mainloop
    while True:
        message = sock.recv(4096)
        print(message.decode())

#====================================#

sock.connect((ip, port))

receiver = threading.Thread(target=receiveMessages, args=(sock,))
receiver.start()

# Main thread: send message
while True:
    sock.send(input().encode('UTF-8'))
