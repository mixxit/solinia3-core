package com.solinia.solinia.Interfaces;

public interface IChannelManager {

	void sendToLocalChannel(ISoliniaPlayer source, String message);

	void sendToGlobalChannel(ISoliniaPlayer source, String message);

	void sendToLocalChannelDecorated(ISoliniaPlayer source, String message);

	void sendToGlobalChannelDecorated(ISoliniaPlayer source, String message);

}
