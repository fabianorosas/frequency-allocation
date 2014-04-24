package projetoAp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
	
	private static int NUMBER_OF_APS;
	private static ArrayList<char []> topology = new ArrayList<char []>();
	private static ArrayList<Ap> network = new ArrayList<Ap>();
	private static Map<Integer,String> apAddrs = new HashMap<Integer, String>();
	
	public static void main (String[] args) throws FileNotFoundException, SocketException{
		Scanner file = new Scanner(new FileReader(args[0]));

		for(int i = 0; file.hasNext(); i++){
			topology.add(file.nextLine().toCharArray());
			mapTopology(i, instantiateAp());
		}
		file.close();
		
		NUMBER_OF_APS = topology.size();
		
		setupClientLists();
		startAps();
	}
	
	private static void mapTopology(int idx, Ap ap){
		apAddrs.put(idx, ap.getSocket().getLocalAddress().getHostAddress() + ":" + ap.getSocket().getLocalPort());
	}
	
	private static Ap instantiateAp() throws SocketException{
		Ap ap = new Ap();
		network.add(ap);
		return ap;
	}
	
	private static void setupClientLists(){
		for(int ap=0; ap < NUMBER_OF_APS; ap++){
			network.get(ap).setNUMBER_OF_CLIENTS(NUMBER_OF_APS);
			network.get(ap).setClientList(makeClientList(ap));
		}		
	}
	
	private static ArrayList<String> makeClientList(int ap){
		ArrayList<String> clientList = new ArrayList<String>(NUMBER_OF_APS);
			
		for(int i=0; i < NUMBER_OF_APS; i++){
			if(topology.get(ap)[i] == '1'){
				clientList.add(apAddrs.get(i));
			}
		}
		return clientList;
	}
	
	private static void startAps(){
		for(Ap ap : network){
			ap.start();
		}
	}
}
