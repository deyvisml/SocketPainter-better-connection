/**
 * @author Brian Weir (https://github.com/bweir27)
 */
package socketPainter;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerController extends Thread{

	//	private int[] output;
	private Socket client;
	private Server hub;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	public ServerController(Socket client, Server hub) {
		
		this.client = client;
		this.hub = hub;

		try {
			this.ois = new ObjectInputStream(this.client.getInputStream());
			this.oos = new ObjectOutputStream(this.client.getOutputStream());
			
			System.out.println("La conexión con un nuevo cliente fue establecida ****");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("PAINTERTHREAD ERROR: oos/ois init failure");
			e.printStackTrace();
		}
	}

	
	/**
	 * chatUpdateFromHub -- receives a chat update from the Hub, and forwards it to this.feed
	 * @param message: String
	 */
	public void send_message(String message) { // sending a message to ClientController
		try {
			System.out.println("SE ENVIA UN MENSAJE DE SERVERCONTROLLER to CLIENTCONTROLLER: " + message);
			oos.writeObject(message);
		} catch (IOException e) {
			System.out.println("PainterThread ERROR: al enviar mensaje del SERVIDOR al CLIENTE");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * shapeUpdateFromHub -- received a shape from the Hub, and forwards it to to this.PaintingPanel
	 * @param shape
	 */
	public void send_shape(PaintingPrimitive shape) { // sending a message to ClientController
		try {
//			System.out.println("PainterThread received: " + shape.toString());
			oos.writeObject(shape);
		} catch (IOException e) {
			System.out.println("PainterThread ERROR: shapeUpdateFromHub failure");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	public void run() {
		try {
			while(true) {
				System.out.println("bbbbbbbbbbbbb ");
				
				Object obj = ois.readObject();
				
				if(obj.getClass().toString().contains("String")) { // determina si es un mensaje o una figura
					String toBroadcast = (String) obj;
					
					System.out.println("::::Se capturo el mensaje: " + toBroadcast);
					
					this.hub.broadcastMessage(toBroadcast);
				}
				else {
					PaintingPrimitive toBroadcast = (PaintingPrimitive) obj;
					this.hub.broadcastShape(toBroadcast);
				}
			}
		}catch(ClassNotFoundException e) {
			System.out.println("PAINTERTHREAD ERROR: canvasListener failure");
			e.printStackTrace();
		} catch (EOFException eof) {
			System.out.println("PAINTERTHREAD ERROR: canvasListener EOF");
			eof.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
