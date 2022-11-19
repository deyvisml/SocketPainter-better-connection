/**
 * @author Brian Weir (https://github.com/bweir27)
 */
package socketPainter;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientController extends Thread{
		
	static ObjectOutputStream oos;
	static ObjectInputStream ois;
	
	private Painter p;
	
	public ClientController(Painter p)
	{
		this.p = p;
		
		//connect to the server
		try {
			System.out.println("El usuario " + p.name_client + " se esta conectando ...");
			Socket s = new Socket("localhost", 9999);
			System.out.println("CONECTAO !! " + p.name_client);

			// DataOutputStream
			{
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());
			};

		} catch (IOException e) {
			System.out.println("PAINTER ERROR: ");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	public  void send(Object obj) { // sending message to the server
		try {
			System.out.println("El cliente envia un MENSAJE nuevo **************");
			oos.writeObject(obj);
			
		} catch (IOException e) {
			System.out.println("NO SE ENVIE EL CLIENT NAME XD");
			e.printStackTrace();
		}
	}
	
	
	public void run() {
		try {
			while(true) {
				Object obj = ois.readObject();

				if(obj.getClass().toString().contains("String")) {
					String newChat = (String) obj;
					
					System.out.println("CLIENTE CONTROLLER RECIBE EL MENSAJE: " + newChat);
					
					this.p.feed.append(newChat);
				}else {
					PaintingPrimitive newShape = (PaintingPrimitive) obj;
					this.p.paintPanel.addPrimitive(newShape);
					this.p.paintPanel.repaint();
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
