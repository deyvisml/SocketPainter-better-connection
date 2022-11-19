# SocketPainter
A Java project allowing users to draw shapes and send messages using [Sockets](https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html), 
[Threads](https://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html),
[ObjectInputStream](https://docs.oracle.com/javase/7/docs/api/java/io/ObjectInputStream.html), 
[ObjectOutputStream](https://docs.oracle.com/javase/7/docs/api/java/io/ObjectOutputStream.html), and 
[JPanel](https://docs.oracle.com/javase/7/docs/api/javax/swing/JPanel.html)

## How to Use
1. Run `socketPainter/Hub.java` to initialize the main communication hub.
2. Run `socketPainter/Painter.java` and enter a username when prompted to add a Painter (user) to the chatroom.
3. Begin drawing shapes and/or sending chat messages! If you want to have more than one user connected at a time, simply 
   Run another `Painter` (ensuring that your IDE configurations allow for multiple instances of the same class to run in parallel)
   
## Overview:
### `Hub.java`
This is the main ServerSocket, it keeps track of the masterCanvas (an ArrayList of all `PaintingPrimitive`s drawn on the shared canvas). All communication (shapes or text) is sent to/broadcasted by the Hub. 
Once the Hub is started, it will listen for new `Painter` Socket connections. 
When a new connection is detected, it will accept the connection, and delegate the `Painter` to a new `PainterThread`, 
send the masterCanvas to the new `Painter`, broadcast the user's arrival into the chatroom, then continue listening for new connections.

### `PainterThread.java`
The main line of communication between the `Painter` and the `Hub`. 
Each `PainterThread` is responsible for exactly one `Painter`. When an update (either a chat message of type `String` or 
a newly drawn shape of type `PaintingPrimitive`) is received from `Painter` via `ObjectInputStream`, it determines the 
type of update and calls `Hub.broadcastMessage` or `Hub.broadcastShape` accordingly.

Similarly, each `PainterThread` also listens for chat and shape updates from the hub (`PainterThread.chatUpdateFromHub` 
and `PainterThread.shapeUpdateFromHub`, respectively). When an update is received, it is forwarded to its respective 
`Painter` via `ObjectOutputStream`.


### `Painter.java`
Each `Painter` is a unique user in the studio. When a new `Painter` is initialized, it prompts the user for a username. 
If a username is entered, the `Painter` will try to connect to the `Hub`. If the connection is successful, 
it will initialize the canvas, and begin listening for: 
1. [MouseEvents](https://docs.oracle.com/javase/7/docs/api/java/awt/event/MouseEvent.html) to define the size/location 
   of a shape (which is sent to the `PainterThread` to be forwarded along to / broadcasted by the `Hub`);
2. [ActionEvents](https://docs.oracle.com/javase/7/docs/api/java/awt/event/ActionEvent.html) to change this `Painter`'s pen color or shape;
3. Updates from the `Hub` (that have been forwarded by the `PainterThread`). 
   These updates can be of type `String` (chat messages) or `PaintingPrimitive` (shapes). 
   Once an update is received, the `Painter` determines the type, then either appends the chat to the `feed` or adds the shape to the `paintPanel` accordingly.
   
New `Painter`s can see the masterCanvas upon arrival, but cannot see any of the previous chatroom messages.


## Next Steps
- Currently, when a `Painter` disconnects, the `Hub` crashes. Next, I hope to implement graceful disconnect so that when a `Painter` leaves the studio, the `Hub` acknowledges its disconnect, and the other `Painter`s can continue painting/chatting without any hiccups.
- When a user is actively drawing a shape (`mouseDragged`), show a preview of the shape being drawn to only that Painter - don't send shape to `PainterThread` until `mouseReleased`.
