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
	
	protected void sendMessage(String toSend, String ip, int port) throws IOException{
		dtgSend = new DatagramPacket(toSend.getBytes(), toSend.length(), InetAddress.getByName(ip) , port);
		socket.send(dtgSend);
	}
	
	protected String receiveMessage() throws IOException{
		dtgReceive = new DatagramPacket(new byte[512], 512);
   	 	socket.receive(dtgReceive);
   	 	return new String(dtgReceive.getData());
	}	
	
	protected void sendBroadcast(String toSend) throws IOException{
		for(String port : clientList.replaceAll("(\\d*\\.){3}\\d*#", "").split("#")){
			sendMessage(toSend, "255.255.255.255", Integer.parseInt(port));
		}
	}
	
    public void close(){
    	this.socket.close();
    }
}
