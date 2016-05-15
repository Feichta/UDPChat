package fff.triplef.udpchat.client.gui;

import java.io.IOException;

import fff.triplef.udpchat.exception.UDPChatException;

public class ReceiveMessageThread extends Thread
{
	// GUI
	ClientGUI clientGUI = null;

	public ReceiveMessageThread(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
		this.start();
	}

	@Override
	public void run() {
		try {
			clientGUI.client.receiveMessage(clientGUI.textPaneChat,
					clientGUI.listModel, clientGUI, clientGUI.btnLogin,
					clientGUI.lblActualuser, clientGUI.btnSend, clientGUI.btnImage,
					clientGUI.textFieldUsername, clientGUI.textAreaMessage,
					clientGUI.list);
		} catch (IOException | ClassNotFoundException e) {
			UDPChatException.behandleException(clientGUI, e);
		}
	}
}