package projetoAp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class Host implements Runnable {
	protected DatagramSocket socket;
	protected DatagramPacket dtgSend;
	protected DatagramPacket dtgReceive;
	    
	protected String msg;
	
	protected String clientList;
	
	public Host(String clients, int port) {
		Thread t = new Thread(this);
		t.start();
		this.clientList = clients;
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method to be used by the child classes
	 * @throws IOException  
	 */
	@Override
	public void run() {
		ouvirMeio();
	}
	
	public abstract void ouvirMeio();
	
	protected void sendBroadcast(String toSend) {
		for(String port : clientList.replaceAll("(\\d*\\.){3}\\d*#", "").split("#")){
			sendMessage(toSend, "255.255.255.255", Integer.parseInt(port));
		}
	}

	protected void sendMessage(String toSend, String ip, int port) {
		try {
			dtgSend = new DatagramPacket(toSend.getBytes(), toSend.length(), InetAddress.getByName(ip) , port);
			socket.send(dtgSend);
		} catch (IOException e) {
			System.err.println(e.toString());
		}
		
	}
	
	protected String receiveMessage() {
		dtgReceive = new DatagramPacket(new byte[512], 512);
   	 	try {
			socket.receive(dtgReceive);
		} catch (IOException e) {
			System.err.println(e.toString());
		}
   	 	return new String(dtgReceive.getData());
	}	
	
    public void close(){
    	this.socket.close();
    }
}
