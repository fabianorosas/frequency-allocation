package projetoAp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main extends Host {
	
	private static int NUMBER_OF_APS;
	private static ArrayList<char []> topology = new ArrayList<char []>();
	private static Map<Integer,String> apAddrs = new HashMap<Integer, String>();
	
	public Main(int port) throws SocketException{
		super(port);
		setupTopology();
	}
	
	private void setupTopology() throws SocketException{
		initAps();
		waitForReplies();
		setupClientLists();		
	}
	
	private void initAps() throws SocketException {
		for(int ap=0; ap < NUMBER_OF_APS; ap++){
			Ap.main(new String[]{String.valueOf(ap)});
		}
	}
	
	public void waitForReplies() {
		int clientIdx;
		int numberOfReplies = 0;
		
		while(true){
			msg = receiveMessage();
			if( msg.startsWith("#") ) {
				clientIdx = Integer.parseInt(msg.substring(1));
				apAddrs.put(clientIdx, dtgReceive.getAddress().getHostAddress() + ":" + dtgReceive.getPort());
				numberOfReplies++;
				if(numberOfReplies == NUMBER_OF_APS)
					break;
			}
		}
	}
	
	private void setupClientLists(){
		String[] apAddr;
		
		for(int ap=0; ap < NUMBER_OF_APS; ap++){
			apAddr = apAddrs.get(ap).split(":");
			sendMessage("#clientList#" + makeClientList(ap), apAddr[0], Integer.parseInt(apAddr[1]));
		}		
	}
	
	private static String makeClientList(int ap){
		String clientList = new String();
			
		for(int i=0; i < NUMBER_OF_APS;	 i++){
			if(topology.get(ap)[i] == '1'){
				clientList.concat(apAddrs.get(i) + "#");
			}
		}
		return clientList;
	}

	public static void main (String[] args) throws FileNotFoundException, SocketException{
	Scanner file = new Scanner(new FileReader(args[0]));

		while(file.hasNext()){
			topology.add(file.nextLine().toCharArray());
		}
		file.close();
		
		NUMBER_OF_APS = topology.size();
	}
}
