package projetoAp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
	public static void main (String[] args){
		Scanner arquivo = null;
		
		ArrayList<Ap> accessPoints = new ArrayList<Ap>();
		Map<Integer,String> accessPointsAddrs = new HashMap<Integer, String>();
		ArrayList<char []> topologia = new ArrayList<char []>();
		Ap ap = null;
		
		try {
			arquivo = new Scanner(new FileReader(args[0]));
			for(int i = 0; arquivo.hasNext(); i++){
				topologia.add(arquivo.nextLine().toCharArray());
				ap = new Ap();
				accessPoints.add(ap);
				accessPointsAddrs.put(i, ap.getSocket().getInetAddress().getHostAddress() + ":" + ap.getSocket().getPort());
			}
		} catch (FileNotFoundException | SocketException e) {
			System.err.println(e);
		}
		
		for(int i=0; i < topologia.size(); i++){
			String clientList = "";
			
			for(int j=0; j < topologia.get(i).length; j++){
				if(topologia.get(i)[j] == 1){
					clientList.concat(accessPointsAddrs.get(j) + "#");
				}
			}
			
			accessPoints.get(i).setClientList(clientList);
		}
	}
}
