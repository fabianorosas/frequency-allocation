package projetoAp;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main extends Host {
	
	private static int NUMBER_OF_APS;
	private static int NUMBER_OF_PROCESSES;
	private static ArrayList<char []> topology = new ArrayList<char []>();
	private static Map<Integer,String> apAddrs = new HashMap<Integer, String>();
	
	public Main(int port) throws IOException{
		super(port);
		System.out.println("Server starting @ "+ this.socket.getLocalAddress().getHostAddress()+":"+this.socket.getLocalPort());
		setupTopology();
	}
	
	private void setupTopology() throws IOException{
		initAps();
		waitForReplies();
		setupClientLists();
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
				clientList.concat(apAddrs.get(i) + "#");
			}
		}
		return clientList;
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
