package projetoAp;

public class Ap extends Host{
	
	private static final int BUSY_SWITCHING = -1;
	private static final int CAN_SWITCH = 0;
	
	private int psi;
	
	public Ap(String clients, int port){
		super(clients, port);
		this.psi = CAN_SWITCH;
	}

	public void startPhase1(){
		lockAll();
	}
	
	public void startPhase2(){
		waitForReplies();
		if(wereChildrenLocked()){
			switchChannel();
		}
		unlockAll();
	}
	
	public void switchChannel(){
		
	}
	
	public void lockAll(){
		
	}
	
	public void unlockAll(){
		
	}
	
	public void waitForReplies(){
		
	}
	
	public boolean wereChildrenLocked(){
		return false;
	}
	
	public boolean canBeLocked(){
		return !(this.psi == BUSY_SWITCHING);
	}
	
	@Override
	public void ouvirMeio() {
		while(true){
			/*
			msg = receiveMessage();
			if( msg.startsWith("1#") ) { //client connection
				App.showMessage(dtgReceive.getAddress().getHostAddress() + ":" + dtgReceive.getPort() + " connected!");
				clientList = clientList.concat(dtgReceive.getAddress().getHostAddress() + "#" + dtgReceive.getPort() + "#");
				sendBroadcast("2#" + clientList);
				App.updateList(clientList);
			}
			else if( msg.startsWith("3#") ){ //message exchange
				if(msg.substring(2).startsWith(brdcst)){
					sendBroadcast("4#" + msg.substring(2));
				}
				else{
					String[] msgSplit = msg.split("#");
					String toSend = "4#" + dtgReceive.getAddress().getHostAddress() + "#" + dtgReceive.getPort() + "#" + msgSplit[3].trim(); 
					sendMessage(toSend, msgSplit[1], Integer.parseInt(msgSplit[2]));
				}
				App.showMessage(msg.substring(2));
			}
			else if( msg.startsWith("5#") ){ //client disconnection
				App.showMessage(dtgReceive.getAddress().getHostAddress() + ":" + dtgReceive.getPort() + " disconnected!");
				String removeHost = dtgReceive.getAddress().getHostAddress() + "#" + dtgReceive.getPort() + "#";
				clientList = clientList.replaceFirst(Pattern.quote(removeHost), "");
				sendBroadcast("2#" + clientList);
				App.updateList(clientList);
			}
			else{
				App.lblStatusServer.setText("STRAY MESSAGE: " + msg);
			}
			*/
		}
	}
}
