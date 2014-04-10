package projetoAp;

public class Ap extends Host{

	public Ap(String clients, int port){
		super(clients, port);
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
