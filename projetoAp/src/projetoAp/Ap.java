package projetoAp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Ap extends Host {
	
	private static final int BUSY_SWITCHING = -1;
	private static final int CAN_SWITCH = 0;
	
	private static final int CHANNEL_SWITCHING_PERIOD = 2000;
	private static final int CHANNEL_SWITCHING_DELAY =  new Random().nextInt((CHANNEL_SWITCHING_PERIOD - 1000) + 1) + 1000;
	
	private int NUMBER_OF_CLIENTS;

	private Timer timer;
	private TimerTask switchChannel;

	private int idx;
	private int psi;
	private int channel;
	private int[] clientResponse;
	private ChannelUpdateStrategy updateStrategy;

	private Logger log;
    	
	public Ap(int idx, String[] serverAddr) {
		super();
		this.idx = idx;
		startLogging();
		
		this.serverIP = serverAddr[0];
		this.serverPort = Integer.parseInt(serverAddr[1]);
		this.psi = CAN_SWITCH;
		this.channel = 1;
		this.updateStrategy = new GlobalCoord(new int[]{1,6,11});
		
		sayHello();
		
		waitForClientList();
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
				if(canSwitch()){
					startPhase1();
					startPhase2();
					psi = CAN_SWITCH;
				}
			}
		};
		this.timer.schedule(switchChannel, CHANNEL_SWITCHING_DELAY, CHANNEL_SWITCHING_PERIOD);
	}
	
	/**
	 * Corresponds to the part where the AP functions as a client.
	 */
	private void listen(){
		while(true){
			msg = receiveMessage();

			synchronized(this){
				if( msg.startsWith("#lock") ) {
					//log.info("locked");
					if(canBeLocked()){
						psi++;
						reply("#c" + channel);
					}
					else{
						reply("#c" + BUSY_SWITCHING );
					}
				}
				else if( msg.startsWith("#unlock") ){
					//log.info("unlocked");
					psi--;
				}
				else if ( msg.startsWith("#c") ){
					int clientIndex = getClientList().indexOf(dtgReceive.getAddress().getHostAddress() + ":" + dtgReceive.getPort());
					clientResponse[clientIndex] = Integer.parseInt(msg.trim().substring(2));
				}
				else if ( msg.startsWith("#stop") ){
					log.info("CHANNEL:" + channel);
					System.exit(0);
				}
				else{
					log.warning("Stray message: " + msg.trim());
				}
			}
		}
	}	
	
	private void startPhase1(){
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
		//log.info("wait replies");
		while(!allClientsReplied());
		//log.info("all replied");
	}

	private boolean allClientsReplied(){
		for(int i = 0; i < clientResponse.length; i++){
			if(clientResponse[i] == -2){
				return false;
			}
		}
		return true;
	}
	
	private void reply(String toSend){
		sendMessage(toSend, dtgReceive.getAddress().getHostAddress(), dtgReceive.getPort());
	}
	
	private void lockAllClients(){
		resetClientResponse();
		sendBroadcast("#lock");
	}
	
	private void unlockAllClients(){
		String[] clientAddr;
		
		for(int i = 0; i < clientResponse.length; i++){
			if(clientResponse[i] != BUSY_SWITCHING){
				clientAddr = getClientList().get(i).split(":"); 
				sendMessage("#unlock", clientAddr[0], Integer.parseInt(clientAddr[1]));
			}
		}
	}

	private void resetClientResponse(){
		Arrays.fill(clientResponse, -2);
	}
	
	private boolean allClientsAreLocked(){
		for(int response : clientResponse){
			if(response == BUSY_SWITCHING){
				return false;
			}
		}
		return true;
	}
	
	private void updateChannel(){
		int minInterferenceChannel = updateStrategy.updateChannel(channel, clientResponse);
		
		if( minInterferenceChannel != this.channel ){
			this.channel = minInterferenceChannel;
			log.info("Switched channel!");
		}
		else{
			sendMessage("#noop"+idx, serverIP, serverPort);
		}
	}
	
	private void initClientsResponse(){
		clientResponse = new int[NUMBER_OF_CLIENTS];
		resetClientResponse();
	}

	private synchronized boolean canSwitch(){
		if(this.psi == CAN_SWITCH){ 
			this.psi = BUSY_SWITCHING;
			return true;
		}
		return false;
	}
	
	private boolean canBeLocked(){
		return (this.psi != BUSY_SWITCHING);
	}
	
	public int getNUMBER_OF_CLIENTS() {
		return NUMBER_OF_CLIENTS;
	}

	public void setNUMBER_OF_CLIENTS(int NUMBER_OF_CLIENTS) {
		this.NUMBER_OF_CLIENTS = NUMBER_OF_CLIENTS;
	}
	
	private void sayHello(){
		sendMessage("#" + idx, serverIP, serverPort);
	}
	
	public void waitForClientList(){
		String[] clientList;
		
		msg = receiveMessage();

		if( msg.startsWith("#clientList#") ) {
			clientList = msg.substring(12).trim().split("#");
			super.setClientList(new ArrayList<String>(Arrays.asList(clientList)));
			setNUMBER_OF_CLIENTS(clientList.length);
			initClientsResponse();
		}
	}	
	
	private void startLogging() {
		FileHandler handler;
		try {
			handler = new FileHandler("ap" + idx + ".log", 0, 1, true);
			handler.setFormatter(new SimpleFormatter());
			handler.setLevel(Level.ALL);
			log = Logger.getLogger("projetoAp.Ap");
			log.setUseParentHandlers(false);
			log.addHandler(handler);
			log.setLevel(Level.FINER);
		} catch (SecurityException | IOException e) {
			System.err.println(e);
		}
	}
	
	/**
	 * Starts an Access Point.
	 * @param args <index number in the topology file> <server ip:server port>
	 * @throws IOException 
	 * @throws SecurityException 
	 * @throws NumberFormatException 
	 */
	public static void main (String[] args) {
		new Ap(Integer.parseInt(args[0]), args[1].split(":"));
	}
}