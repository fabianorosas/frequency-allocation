package projetoAp;

import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

public class Ap extends Host {
	
	private static final int BUSY_SWITCHING = -1;
	private static final int CAN_SWITCH = 0;
	
	private static final int CHANNEL_SWITCHING_DELAY = 1000;
	private static final int CHANNEL_SWITCHING_PERIOD = 1000;

	private Timer timer;
	private TimerTask switchChannel;
	
	private int psi;
	private int channel;

	
	public Ap(String clients, int port) throws SocketException{
		super(clients, port);
		this.psi = CAN_SWITCH;
		this.channel = 1; //TODO: 1?
		
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
		this.timer.schedule(switchChannel, CHANNEL_SWITCHING_DELAY, CHANNEL_SWITCHING_PERIOD);
	}
	
	/**
	 * Corresponds to the part where the AP functions as a client.
	 */
	private void listen(){
		msg = receiveMessage();
		if( msg.startsWith("#lock") ) { 
			if(canBeLocked()){
				psi++; //lock
				replyWithChannel();
			}
		}
		else if( msg.startsWith("#unlock") ){
			psi--;
		}
		else if ( msg.startsWith("#c") ){
			//TODO: we tried to lock client and client replied "OK, here´s the channel I'm at."
			//TODO: Ex: #c11, #c6, #c1
		}
		else{
			System.err.println("Stray message: " + msg);
		}
	}	
	
	private void startPhase1(){
		this.psi = BUSY_SWITCHING;
		sendBroadcast("#lock");
	}
	
	private void startPhase2(){
		waitForReplies();
		if(allChildrenAreLocked()){
			tryToSwitchChannel();
		}
		unlockAllChildren();
	}

	private void waitForReplies(){
		//TODO: find a way to block the AP until all clients respond.
		//TODO: should it really block? or should it keep listening to the medium and receiving locking requests from other APs?
	}

	private void replyWithChannel(){
		String toSend = "#" + channel; 
		sendMessage(toSend, dtgReceive.getAddress().getHostAddress(), dtgReceive.getPort());
	}
	
	private void tryToSwitchChannel(){
		//TODO: the channel switching logic
	}
	
	private void unlockAllChildren(){
		sendBroadcast("#unlock");
		//TODO: send this message only to the APs that were previously locked, to reduce network overhead
	}
	
	private boolean allChildrenAreLocked(){
		//TODO: check the response of the children to know if they were able to lock.
		return false;
	}
	
	private boolean canBeLocked(){
		return !(this.psi == BUSY_SWITCHING);
	}
}