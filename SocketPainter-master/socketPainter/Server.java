/**
 * @author Brian Weir (https://github.com/bweir27)
 */
package socketPainter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Server {

	public ArrayList<ServerController> painters = new ArrayList<ServerController>();
	public ArrayList<PaintingPrimitive> masterCanvas = new ArrayList<PaintingPrimitive>();

	/**
	 * broadcastMessage - receives a message from a PainterThread, and broadcasts it to all connected painters
	 * @param message: String
	 */
	public void broadcastMessage(String message) {
		/*When a new user joins, they do not see the master chat, the first message
		 * they will see is the server's announcement of their entering
		 */
		System.out.println("Hub: Broadcasting message- \"" + message+"\"");
		System.out.println("Tamaño painters- \"" + painters.size()+"\"");
		
		for(ServerController pt : painters) {
			pt.send_message(message + "\n");
		}
	}

	/**
	 * broadcastShape -- receives a shape from a PainterThread, and broadcasts it to all connected painters
	 * @param shape: PaintingPrimitive
	 */
	public void broadcastShape(PaintingPrimitive shape) {
		//add shape to masterCanvas
		masterCanvas.add(shape);

		//since a shape cannot be deleted, only need to send the most recent shape to each Painter
		for(ServerController pt : painters) {
			try {
				pt.send_shape(shape);
			}catch(Exception e) {
				System.out.println("HUB ERROR: shapeUpdateFromHub");
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	/**
	 * broadcastDisconnect -- receives a shutdown notice from a PainterThread, and removes it from the list of painters
	 * TODO: get this working
	 * @param pt: PainterThread
	 */
	public void broadcastDisconnect(ServerController pt) {
		painters.remove(pt);
	}

	/**
	 * startHub -- listens for new Painter connections, assigns a new PainterThread to be the intermediary,
	 * 				forks off a new thread, and resumes listening for new connections
	 */
	private void startHub() {
		System.out.println("Hub started, awaiting Painter connections...");
		ServerSocket ss = null;
		Socket s = null;
		
		try {
			ss = new ServerSocket(9999);
			
			while(true) { // se queda esperando nuevas conexiones al servidor
				s = ss.accept();
				ServerController pt = new ServerController(s, this);
				
				//Thread thread = new Thread(pt);
				
				painters.add(pt);
				
				for(PaintingPrimitive p : masterCanvas) { // al nuevo pintor se le agregan todas las figuras que se tienen hasta el momento
					pt.send_shape(p);
				}
				
				pt.start();
			}
		}catch(IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		new Server().startHub();
	}
}
