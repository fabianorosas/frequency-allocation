package projetoAp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public abstract class Host {
	protected DatagramSocket socket;
	protected String serverIP;
	protected int serverPort;
	protected DatagramPacket dtgSend;
	protected DatagramPacket dtgReceive;
	    
	protected String msg;
	
	private ArrayList<String> clientList;
	
	public Host() {
		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e) {
			System.err.println(e);
		}
		this.clientList = new ArrayList<String>();
	}
	
	public Host(int port) throws SocketException{
		this.socket = new DatagramSocket(port);
	}

	protected void sendBroadcast(String toSend) {
		for(String client : clientList) {
			String[] addr = client.split(":|#");
			sendMessage(toSend, addr[0], Integer.parseInt(addr[1]));
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
			socket.receive(dtgReceive);
		} catch (IOException e) {
			System.err.println(e.toString());
		}
   	 	return new String(dtgReceive.getData());
	}	
	
    public void close(){
    	this.socket.close();
    }

	public DatagramSocket getSocket() {
		return socket;
	}

	public ArrayList<String> getClientList() {
		return clientList;
	}

	public void setClientList(ArrayList<String> clientList) {
		this.clientList = clientList;
	}
}
