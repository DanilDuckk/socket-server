# Protocol description

This client-server protocol describes the following scenarios:
- Setting up a connection between client and server.
- Broadcasting a message to all connected clients.
- Periodically sending heartbeat to connected clients.
- Disconnection from the server.
- Handling invalid messages.

In the description below, `C -> S` represents a message from the client `C` is send to server `S`. When applicable, `C` is extended with a number to indicate a specific client, e.g., `C1`, `C2`, etc. The keyword `others` is used to indicate all other clients except for the client who made the request. Messages can contain a JSON body. Text shown between `<` and `>` are placeholders.

The protocol follows the formal JSON specification, RFC 8259, available on https://www.rfc-editor.org/rfc/rfc8259.html

# 1. Establishing a connection

The client first sets up a socket connection to which the server responds with a welcome message. The client supplies a username on which the server responds with an OK if the username is accepted or an ERROR with a number in case of an error.
_Note:_ A username may only consist of characters, numbers, and underscores ('_') and has a length between 3 and 14 characters.

## 1.1 Happy flow

client.Client sets up the connection with server.
```
S -> C: READY {"version": "<server version number>"}
```
- `<server version number>`: the semantic version number of the server.

After a while when the client logs the user in:
```
C -> S: ENTER {"username":"<username>"}
S -> C: ENTER_RESP {"status":"OK"}
```

- `<username>`: the username of the user that needs to be logged in.
  To other clients (Only applicable when working on Level 2):
```
S -> others: JOINED {"username":"<username>"}
```

## 1.2 Unhappy flow
```
S -> C: ENTER_RESP {"status":"ERROR", "code":<error code>}
```      
Possible `<error code>`:

| Error code | Description                              |
|------------|------------------------------------------|
| 1000       | User with this name already exists       |
| 1001       | Username has an invalid format or length |      
| 1002       | Already logged in                        |

# 2. Broadcast message

Sends a message from a client to all other clients. The sending client does not receive the message itself but gets a confirmation that the message has been sent.

## 2.1 Happy flow

```
C -> S: BROADCAST_REQ {"message":"<message>"}
S -> C: BROADCAST_RESP {"status":"OK"}
```
- `<message>`: the message that must be sent.

Other clients receive the message as follows:
```
S -> others: BROADCAST {"username":"<username>","message":"<message>"}   
```   
- `<username>`: the username of the user that is sending the message.

## 2.2 Unhappy flow

```
S -> C: BROADCAST_RESP {"status": "ERROR", "code": <error code>}
```
Possible `<error code>`:

| Error code | Description            |
|------------|------------------------|
| 2000       | User is not logged in  |

# 3. Heartbeat message

Sends a ping message to the client to check whether the client is still active. The receiving client should respond with a pong message to confirm it is still active. If after 3 seconds no pong message has been received by the server, the connection to the client is closed. Before closing, the client is notified with a HANGUP message, with reason code 7000.

The server sends a ping message to a client every 10 seconds. The first ping message is send to the client 10 seconds after the client is logged in.

When the server receives a PONG message while it is not expecting one, a PONG_ERROR message will be returned.

## 3.1 Happy flow

```
S -> C: PING
C -> S: PONG
```     

## 3.2 Unhappy flow

```
S -> C: HANGUP {"reason": <reason code>}
[Server disconnects the client]
```      
Possible `<reason code>`:

| Reason code | Description      |
|-------------|------------------|
| 3000        | No pong received |    

```
S -> C: PONG_ERROR {"code": <error code>}
```
Possible `<error code>`:

| Error code | Description         |
|------------|---------------------|
| 3001       | Pong without ping   |    

# 4. List of users

Send a command to get a list of all current users connected to the server.

If the user is not logged in you will get an error code.

## 4.1 Happy flow

The client receives the message as follows:
```
C -> S: CLI_LIST_REQ
S -> C: CLI_LIST {"list": []}
```

- `<username>`: the username of clients will be stored there.

## 4.2 Unhappy flow
```
S -> C: CLI_LIST_RESP {"status": "ERROR", "code": <reason code>}
```      
Possible `<reason code>`: The user is not logged in

| Reason code | Description           |
|-------------|-----------------------|
| 4000        | User is not logged in |  

# 5. Private messaging

Send a message to the server referencing another user to send him a private message.

If the user is not found you will get an error code.

## 5.1 Happy flow

The client (sender) send the message as follows:
```
C -> S: PRIV_MESSAGE_REQ {"user":"<username>" "message":"<message>"}
S -> C: PRIV_MESSAGE_RESP {"status":"OK"}
```

The client (receiver) receives the message as follows:
```
S -> C: PRIV_MESSAGE {"message":"<message>"}
```   

- `<message>`: the message sent by the sender
- `<username>`: the receiver's message

## 4.2 Unhappy flow

The client (sender) send the message as follows:
```
C -> S: PRIV_MESSAGE_REQ {"user":"<username>" "message":"<message>"}
S -> C: PRIV_MESSAGE_RESP {"error": <reason code>}
```      
Possible `<reason code>`: The user is not found

| Reason code | Description   |
|-------------|---------------|
| 5000        | No user found |
| 5001        | Not logged in |

# 6. Rock,paper,scissors

User sends another user an invitation to play the "Rock,paper,scissors" game.

The receiver should choose a rock, paper or scissors.

## Part 1 (Invitation)

#### 6.1 Happy flow

Client A sends to Client B a "rock paper scissors game"

```
A -> S: RPS_REQ {"inviter":"<username_sender>", "invitee":"<username_receiver>", "choice":<rock>}
S -> A: RPS_RESP {"status": "OK"}
S -> B: RPS_PLAY {"inviter":"<username_sender>"}
```

#### 6.1 Unhappy flow

The user is not found

```
S -> B: RPS_RESP {"status": <error>, "code": <error code>}
```

The game is already running

```
A -> S: RPS_RESP {"status": <error>, "code": <error code>}
```

Possible `<error code>`:

| Error code | Description     |
|------------|-----------------|
| 6000       | User not found  | 
| 6001       | Game is running | 

## Part 2 (Game)

#### 6.2 Happy flow

Client B plays a game by sending his choice

```
B -> S: RPS_ANSWER {"choice":"paper"}
```

Server sends both clients a message whether they won or not

```
S -> A: RPS_RESULT {"result":<lost>}
S -> B: RPS_RESULT {"result":<won>}
```

#### 6.2 Unhappy flow

Client sent a choice without participating in game

```
A -> B: RPS_ANSWER {"status": <error>, "code": <error code>}
```
Possible `<error code>`:

| Error code | Description                        |
|------------|------------------------------------|
| 6002       | Client doesn't participate in game | 

# 7. File Transfer

Allows direct file transfer between two clients over a separate socket,
ensuring file integrity with a checksum.

### 7.1 Happy Flow

#### 7.1.1 Initiating File Transfer

Client A initiates the file transfer request to the server.

```
C -> S: FILE_REQ {"to":"<username>", "filename":"<filename>", "size":<filesize>}
```

- `<to>`: The recipient's username.
- `<filename>`: The name of the file being sent.
- `<size>`: The size of the file in bytes.

Server forwards the request to Client B.

```
S -> B: FILE_REQ {"from":"<username>", "filename":"<filename>", "size":<filesize>}
```

- `<from>`: The sender's username.

Client B responds to the file transfer request.

```
B -> S: FILE_RESP {"from":"username", "file":"fileName" status":"ACCEPT", "checksum":"<checksum>"}
```

- `<checksum>`: The checksum (e.g., SHA) of the expected file for verification.

Server notifies Client A of the response.

```
S -> A: FILE_RESP {"from":"username", "file":"fileName" status":"ACCEPT", "checksum":"<checksum>"}
```

### 7.1 Unhappy Flow

Client B rejects the file transfer request.

```
B -> S: FILE_RESP {"status":"ERROR", "file":"fileName", "code":7001}
```

| Reason code | Description             |
|-------------|-------------------------|
| 7001        | User rejected a request |
| 7002        | User is not logged in   |


### 7.2 Preparation

Client A sends a request fort server to create a socket with unique uuid.

```
A -> S: FILE_TRANSFER_START {"sender":"sender", "receiver":"receiver", "file": "fileName"}
```

Then server creates a socket and send a uuid and a role to the sender and receiver.

```
S -> A: FILE_TRANSFER_READY {"uuid": "uuid", "role":"role"}
```

```
S -> B: FILE_TRANSFER_READY {"uuid": "uuid", "role":"role"}
```

After that both clients connect to the socket and start file transferring

### 7.3 File Transfer Compelete

Server sends a notification about file transfer completion for both clients

```
S -> A: FILE_COMPLETE {"status": "OK", "uuid": "uuid", "role":"role"}
```

```
S -> B: FILE_COMPLETE {"status": "OK", "uuid": "uuid", "role":"role"}
```

### 7.3 Unhappy Flow

```
B -> S: FILE_COMPLETE {"status":"ERROR", "code":7003}
```

The checksum is failed.

| Error code | Description                 |
|------------|-----------------------------|
| 7003       | Recipient unavailable       |  
| 7004       | Sender disconnected         |  
| 7005       | Recipient unavailable       |

# 8. Termination of the connection

When the connection needs to be terminated, the client sends a bye message. This will be answered (with a BYE_RESP message) after which the server will close the socket connection.

## 8.1 Happy flow
```
C -> S: BYE
S -> C: BYE_RESP {"status":"OK"}
[Server closes the socket connection]
```

Other, still connected clients, clients receive:
```
S -> others: LEFT {"username":"<username>"}
```

## 8.2 Unhappy flow

- None

# 9. Invalid message header

If the client sends an invalid message header (not defined above), the server replies with an unknown command message. The client remains connected.

Example:
```
C -> S: MSG This is an invalid message
S -> C: UNKNOWN_COMMAND
```

# 10. Invalid message body

If the client sends a valid message, but the body is not valid JSON, the server replies with a pars error message. The client remains connected.

Example:
```
C -> S: BROADCAST_REQ {"aaaa}
S -> C: PARSE_ERROR
```