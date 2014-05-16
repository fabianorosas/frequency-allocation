package projetoAp;

public class ChannelUpdater {
	private ChannelUpdateStrategy updateStrategy;
	
	public ChannelUpdater(ChannelUpdateStrategy updateStrategy){
		this.updateStrategy = updateStrategy;
	}
	
	public int updateChannel(int channel, int[] clientResponse){
		return updateStrategy.updateChannel(channel, clientResponse);
	}
}
