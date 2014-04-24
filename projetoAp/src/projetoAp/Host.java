package projetoAp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class Host {
	private DatagramSocket socket;
	protected DatagramPacket dtgSend;
	protected DatagramPacket dtgReceive;
	    
	protected String msg;
	
	private String clientList;
	
	public Host() throws SocketException {
		this.setSocket(new DatagramSocket());
	}

	protected void sendBroadcast(String toSend) {
		for(String port : getClientList().replaceAll("(\\d*\\.){3}\\d*#", "").split("#")){
			sendMessage(toSend, "255.255.255.255", Integer.parseInt(port));
		}
	}

	protected void sendMessage(String toSend, String ip, int port) {
		try {
			dtgSend = new DatagramPacket(toSend.getBytes(), toSend.length(), InetAddress.getByName(ip) , port);
			getSocket().send(dtgSend);
		} catch (IOException e) {
			System.err.println(e.toString());
		}
		
	}
	
	protected String receiveMessage() {
		dtgReceive = new DatagramPacket(new byte[512], 512);
   	 	try {
			getSocket().receive(dtgReceive);
		} catch (IOException e) {
			System.err.println(e.toString());
		}
   	 	return new String(dtgReceive.getData());
	}	
	
    public void close(){
    	this.getSocket().close();
    }

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	public String getClientList() {
		return clientList;
	}

	public void setClientList(String clientList) {
		this.clientList = clientList;
	}
}
