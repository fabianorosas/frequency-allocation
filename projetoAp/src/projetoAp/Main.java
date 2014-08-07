package projetoAp;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main extends Host {
	
	private static final int IDLE_RUNS = 50;
	private static int NUMBER_OF_APS;
	private static int NUMBER_OF_PROCESSES;
	private static ArrayList<char []> topology = new ArrayList<char []>();
	private static Map<Integer,String> apAddrs = new HashMap<Integer, String>();
	private static int[] responses;
	
	public Main(int port) throws IOException{
		super(port);
		responses = new int[NUMBER_OF_APS];
		Arrays.fill(responses, 0);
		System.out.println("Server starting @ "+ this.socket.getLocalAddress().getHostAddress()+":"+this.socket.getLocalPort());
		setupTopology();
	}
	
	private void setupTopology() throws IOException{
		initAps();
		waitForReplies();
		setupClientLists();
		System.out.println("Topology set. Waiting for the APs to finish...");
		waitResponses();
	}
	
	private void waitResponses(){		
		while(true){
			msg = receiveMessage();

			if( msg.startsWith("#noop") ) {
				int clientIndex = Integer.parseInt(msg.trim().substring(5));
				responses[clientIndex]++;
			}
			else{
				System.err.println("Stray message: " + msg.trim());
			}
		 
			if(switchingStopped()){
				//notifyAllAps();
				System.out.println("All APs are idle for "+ IDLE_RUNS + " runs.");
				System.exit(0);
			}
		}
	}
	
	private void notifyAllAps(){
		sendBroadcast("#stop");
	}
	
	private boolean switchingStopped(){
		for(int response : responses){
			if(response < IDLE_RUNS){
				return false;
			}
		}
		return true;
	}
	
	private void initAps() throws IOException {
		for(int ap=0; ap < NUMBER_OF_PROCESSES; ap++){
			Runtime.getRuntime().exec(new String[]{"java", "-jar", "ap.jar", String.valueOf(ap), new String(this.socket.getLocalAddress().getHostAddress()+":"+this.socket.getLocalPort())});
			System.out.println("Ap " + String.valueOf(ap) + " started");
		}
	}
	
	public void waitForReplies() {
		int clientIdx;
		int numberOfReplies = 0;
		
		System.out.println("Waiting for APs...");
		
		while(true){
			msg = receiveMessage();
			if( msg.startsWith("#") ) {
				 clientIdx = Integer.parseInt(msg.trim().substring(1));
				apAddrs.put(clientIdx, dtgReceive.getAddress().getHostAddress() + ":" + dtgReceive.getPort());
				numberOfReplies++;
				if(numberOfReplies == NUMBER_OF_APS)
					break;
			}
		}
		
		System.out.println("Everyone replied.");
	}
	
	private void setupClientLists(){
			String[] apAddr;
			
		System.out.println("Distributing client lists...");
		
		for(int ap=0; ap < NUMBER_OF_APS; ap++){
			apAddr = apAddrs.get(ap).split(":");
			sendMessage("#clientList#" + makeClientList(ap), apAddr[0], Integer.parseInt(apAddr[1]));
		}		
		
		System.out.println("All lists distributed.");
	}
	
	private String makeClientList(int ap){
		String clientList = new String();

		System.out.println("Assembling client list for AP " + ap);
		
		for(int i=0; i < NUMBER_OF_APS;	 i++){
			if(topology.get(ap)[i] == '1'){
				clientList = clientList.concat(apAddrs.get(i) + "#");
			}
		}
		return clientList;
	}
	
	@Override
	protected void sendBroadcast(String toSend) {
		for(String apAddr : apAddrs.values()) {
			String[] addr = apAddr.split(":|#");
			sendMessage(toSend, addr[0], Integer.parseInt(addr[1]));
		}
	}
	
	/**
	 * Starts the APS and sets up the topology.
	 * @param args <topology file> [number of aps to start]
	 * @throws IOException
	 */
	public static void main (String[] args) throws IOException{
		Scanner file = new Scanner(new FileReader(args[0]));
		int port = 7777;
		
		while(file.hasNext()){
			topology.add(file.nextLine().toCharArray());
		}
		file.close();
		
		NUMBER_OF_APS = topology.size();

		if(args.length == 2){
			NUMBER_OF_PROCESSES = Integer.parseInt(args[1]);
		} else{
			NUMBER_OF_PROCESSES = NUMBER_OF_APS; 
		}	
		
		new Main(port);
	}
}