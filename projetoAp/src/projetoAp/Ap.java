package projetoAp;

import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class Ap extends Host {
	
	private static final int BUSY_SWITCHING = -1;
	private static final int CAN_SWITCH = 0;
	
	private static final int CHANNEL_SWITCHING_DELAY = 1000;
	private static final int CHANNEL_SWITCHING_PERIOD = 1000;
	
	private int psi;
	private Timer timer;
	private TimerTask switchChannel;
	
	public Ap(String clients, int port) throws SocketException{
		super(clients, port);
		this.psi = CAN_SWITCH;
		
		startTimer();
		listen();
	}

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
		this.timer.schedule(switchChannel, CHANNEL_SWITCHING_DELAY, CHANNEL_SWITCHING_PERIOD);
	}
	
	private void listen(){
		msg = receiveMessage();
		if( msg.startsWith("#lock") ) { 
			//	TODO: locking logic
		}
		else if( msg.startsWith("#unlock") ){
			//	TODO: unlocking logic
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
		if(allChildrenAreLocked()){
			switchChannel();
		}
		unlockAll();
	}

	private void waitForReplies(){
		
	}

	private void switchChannel(){
		
	}
	
	private void lockAllClients() {
		sendBroadcast("#lock");
	}
	
	private void unlockAll(){
		
	}
	
	private boolean allChildrenAreLocked(){
		return false;
	}
	
	private boolean canBeLocked(){
		return !(this.psi == BUSY_SWITCHING);
	}
}
