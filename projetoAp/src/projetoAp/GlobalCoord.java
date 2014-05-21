package projetoAp;

public class GlobalCoord implements ChannelUpdateStrategy {

	private int[] possibleChannels;
	private InterferenceModel interferenceModel;
	
	public GlobalCoord(int[] possibleChannels){
		this.possibleChannels = possibleChannels;
		
		this.interferenceModel = new InterferenceModel();
	}	
	
	@Override
	public int updateChannel(int channel, int[] clientResponse) {
		int minInterferenceChannel=0;
		double minInterference = Double.MAX_VALUE;
		
		for(int possibleChannel : possibleChannels){
			double maxInterferenceInChannel = getMaxInterference(possibleChannel, clientResponse); 
			if(maxInterferenceInChannel < minInterference){
				minInterference = maxInterferenceInChannel;
				minInterferenceChannel = possibleChannel;
			}
		}
		return minInterferenceChannel;
	}
	
	private double getMaxInterference(int possibleChannel, int[] clientResponse){
		double maxInterferenceInChannel = 0.0;
		
		for(int response : clientResponse){
			maxInterferenceInChannel = Math.max(maxInterferenceInChannel, interferenceModel.get(Math.abs(possibleChannel-response)));
		}
		
		return maxInterferenceInChannel;
	}
}
