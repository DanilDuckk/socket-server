# socket-server[

A project consists of three main components: a client, 
a communication server, and a file transfer server.

**The client** is responsible for interacting with both servers. 
It connects to the communication server to send and receive messages, 
and it interacts with the file transfer server to transfer files.

**The communication server** handles client connections and facilitates 
real-time messaging between clients. It routes messages, manages sessions, 
and may also handle authentication. Additionally, it can notify users when 
a file transfer is initiated.

**The file transfer server** is dedicated to handling file transferring. 
It ensures reliable transmission of files using a designated protocol, and socket connection.

The communication between these components follows structured protocol. 
The communication server primarily uses WebSockets or TCP sockets for real-time interaction, 
while the file transfer server operates over a dedicated port.
