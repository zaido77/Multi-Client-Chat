# 💬 Java Multi-Client Chat Application (TCP/IP)

A simple real-time multi-client chat system built using Java Sockets and multithreading.  
The project demonstrates client-server communication over TCP/IP, where multiple clients can join a shared chat room and exchange messages through a central server.

---

## 🚀 Features

- Multi-client support using threads
- Real-time message broadcasting
- Unique username validation
- Join/leave notifications
- Simple console-based chat UI
- Exit command to leave the chat safely
- Thread-safe client management on server

---

## 🏗️ Project Structure
Server.java → Handles client connections, messaging, and broadcasting
Client.java → Connects to server and sends/receives messages


---

## ⚙️ How It Works

### 🔹 Server
- Listens on port `1234`
- Accepts incoming client connections
- Assigns each client to a separate thread (`ClientHandler`)
- Broadcasts messages to all connected clients except the sender
- Maintains a list of active clients
- Ensures unique usernames

### 🔹 Client
- Connects to the server at `localhost:1234`
- Prompts user for a username
- Sends messages to the server
- Receives broadcast messages in real time
- Displays chat window after joining
- Types `exit` to leave the chat

---
