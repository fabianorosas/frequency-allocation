package projetoAp;

import java.net.SocketException;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Ap extends Host {
	
	private static final int BUSY_SWITCHING = -1;
	private static final int CAN_SWITCH = 0;
	
	private static final int CHANNEL_SWITCHING_DELAY = 1000;
	private static final int CHANNEL_SWITCHING_PERIOD = 1000;
	private int NUMBER_OF_CLIENTS;

	private Timer timer;
	private TimerTask switchChannel;
	
	private int psi;
	private int channel;
	private int[] possibleChannels;
	private int[] clientResponse;
	private String[] clients;
	private Map<Integer,Double> interferenceModel;
		
	public Ap() throws SocketException{
		super();

		this.psi = CAN_SWITCH;
		this.channel = 1;
		this.possibleChannels = new int[]{1,6,11};
		
		countClients();
		initClientsResponse();
		initInterferenceModel();
		
		startTimer();
		listen();
	}

	/**
	 * Executes the channel switching logic every <CHANNEL_SWITCHING_PERIOD> milliseconds.
	 */
	private void startTimer(){
		timer = new Timer();
		switchChannel = new TimerTask(){
			@Override
			public void run(){
				if(canBeLocked()){
					startPhase1();
					startPhase2();
				}
			}
		};
		this.timer.schedule(switchChannel, CHANNEL_SWITCHING_DELAY, CHANNEL_SWITCHING_PERIOD); //TODO: randomize period
	}
	
	/**
	 * Corresponds to the part where the AP functions as a client.
	 */
	private void listen(){
		msg = receiveMessage();
		if( msg.startsWith("#lock") ) { 
			if(canBeLocked()){
				psi++;
				reply("#" + channel);
			}
			else{
				reply("#c" + BUSY_SWITCHING );
			}
		}
		else if( msg.startsWith("#unlock") ){
			psi--;
		}
		else if ( msg.startsWith("#c") ){
			int clientIndex = Arrays.binarySearch(clients, dtgReceive.getAddress().getHostAddress() + ":" + dtgReceive.getPort() + "#");
			clientResponse[clientIndex] = Integer.parseInt(msg.substring(2));
		}
		else{
			System.err.println("Stray message: " + msg);
		}
	}	
	
	private void startPhase1(){
		this.psi = BUSY_SWITCHING;
		lockAllClients();
	}
	
	private void startPhase2(){
		waitForReplies();
		if(allClientsAreLocked()){
			updateChannel();
			sendBroadcast("#unlock");
		}
		else{
			unlockAllClients();
		}
	}

	private void waitForReplies(){
		while(!allClientsReplied());
	}

	private boolean allClientsReplied(){
		for(int response : clientResponse){
			if(response == -2){
				return false;
			}
		}
		return true;
	}
	
	private void reply(String toSend){
		sendMessage(toSend, dtgReceive.getAddress().getHostAddress(), dtgReceive.getPort());
	}
	
	private void updateChannel(){
		int minInterferenceChannel=0;
		double minInterference = Double.MAX_VALUE;
		
		for(int possibleChannel : possibleChannels){
			double maxInterferenceInChannel = getMaxInterference(possibleChannel); 
			if(maxInterferenceInChannel < minInterference){
				minInterference = maxInterferenceInChannel;
				minInterferenceChannel = channel;
			}
		}
		if( minInterferenceChannel != this.channel ){
			this.channel = minInterferenceChannel;
			System.out.println("Channel switch");
		}
	}
	
	private double getMaxInterference(int possibleChannel){
		double maxInterferenceInChannel = 0.0;
		
		for(int response : clientResponse){
			maxInterferenceInChannel = Math.max(maxInterferenceInChannel, interferenceModel.get(Math.abs(possibleChannel-response)));
		}
		
		return maxInterferenceInChannel;
	}
	
	private void lockAllClients(){
		resetClientResponse();
		sendBroadcast("#lock");
	}
	
	private void unlockAllClients(){
		for(int i = 0; i < clientResponse.length; i++){
			if(clientResponse[i] != BUSY_SWITCHING){
				int idx = clients[i].indexOf(":");
				sendMessage("#unlock", clients[i].substring(0, idx), Integer.parseInt(clients[i].substring(idx)));
			}
		}
	}
	
	private boolean allClientsAreLocked(){
		for(int response : clientResponse){
			if(response == BUSY_SWITCHING){
				return false;
			}
		}
		return true;
	}
	
	private void countClients(){
		clients = getClientList().split("#");
		Arrays.sort(clients);
		NUMBER_OF_CLIENTS = clients.length;
	}
		
	private void initClientsResponse(){
		clientResponse = new int[NUMBER_OF_CLIENTS];
		resetClientResponse();
	}
	
	private void initInterferenceModel(){
		interferenceModel.put(0, 1.0);
		interferenceModel.put(1, 0.7272);
		interferenceModel.put(2, 0.2714);	
		interferenceModel.put(3, 0.0375);
		interferenceModel.put(4, 0.0054);
		interferenceModel.put(5, 0.0008);
		interferenceModel.put(6, 0.0002);
		interferenceModel.put(7, 0.0);
		interferenceModel.put(8, 0.0);
		interferenceModel.put(9, 0.0);
		interferenceModel.put(10, 0.0);		
	}
	
	private void resetClientResponse(){
		Arrays.fill(clientResponse, -2);
	}
	
	private boolean canBeLocked(){
		return !(this.psi == BUSY_SWITCHING);
	}
	//TODO: add a toString method to show the final channel
}