package projetoAp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Host implements Runnable {
	protected DatagramSocket socket;
	protected DatagramPacket dtgSend;
	protected DatagramPacket dtgReceive;
	    
	protected String msg;
	
	protected String clientList;
	
	public Host() {
		Thread t = new Thread(this);
		t.start();
		clientList = "";
	}

	/**
	 * Method to be used by the child classes
	 * @throws IOException  
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
	
	protected void sendMessage(String toSend, int port) throws IOException{
		dtgSend = new DatagramPacket(toSend.getBytes(), toSend.length(), InetAddress.getLocalHost(), port);
		socket.send(dtgSend);
	}
	
	protected String receiveMessage() throws IOException{
		dtgReceive = new DatagramPacket(new byte[512], 512);
   	 	socket.receive(dtgReceive);
   	 	return new String(dtgReceive.getData());
	}	
	
    public void close(){
    	this.socket.close();
    }
}
