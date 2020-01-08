import socket, json, threading

host = input("\nIPV4 address to bind to:\n")
port = int(input("\nPort to bind to:\n"))
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
jsonList = {"command" : "message", "args" : "Hello world!"}

#====================================#

def formatToMessage(obj):
    string = json.dumps(obj)
    string += "\n"
    return string.encode("utf-8")

def receiveMessages(sock):
    # Thread mainloop
    while True:
        message = sock.recv(4096)
        message = json.loads(message)

        command = message["command"]
        # Decision maker
        if command == "message":
            print(">>Them: " + message["args"])
        elif command == "disconnect":
            sock.close()
            print("Disconnected\n")
            break

#====================================#

sock.connect((host, port))

receiver = threading.Thread(target=receiveMessages, args=(sock,))
receiver.start()

try:
    # Main thread: send message
    while True:
        jsonList["args"] = input()
        jsonList["command"] = "message"
        print(">>You: " + jsonList["args"])
        sock.send(formatToMessage(jsonList))
except KeyboardInterrupt as interrupt:
    # Shutdown:
    jsonList["command"] = "close"
    print("\nDisconnecting... ", end="")
    sock.send(formatToMessage(jsonList))

receiver.join()
input("\nPress <enter> to exit\n")
