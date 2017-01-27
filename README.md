# Server-Client-Chat
The Server-Application is a seperate GUI App where
the ServerSocket is listening for Clients to connect.

On the other side the Client-Application is a simple GUI which connetcs through
a Port to the Server.
When the Client is connected, the user can set his nickname in the chat-room. 

### Collection pick:
Fot this task i used Copy-On-Write Collection,which is one of the
thread safe Collections like Concurrent Collection.

In Copy-On-Write Collection you can find:
* CopyOnWriteArrayList
* CopyOnWriteArraySet

When you iterate over a CopyOnWriteArrayList and CopyOnWriteArraySet the
iterator uses a snapshot of the underlying list (or set)
and does not reflect any changes to the list or set after the snapshot was created.
##### Server:

![Server Image](http://i.imgur.com/7CTcbGA.png)
##### Client:

![Client Image](http://i.imgur.com/P9VIpYg.png)
