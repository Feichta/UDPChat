package fff.triplef.udpchat.client.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import fff.triplef.udpchat.exception.UDPChatException;
import fff.triplef.udpchat.message.Message;

public class WindowClosing extends WindowAdapter
{
	ClientGUI clientGUI = null;

	public WindowClosing(ClientGUI clientGUI) {
		this.clientGUI = clientGUI;
	}

	public void windowClosing(WindowEvent e) {
		if(clientGUI.actualUsername != null) {
		try {
			clientGUI.client.sendMessage(new Message(clientGUI.actualUsername,
					Message.ID_ALL, Message.ID_LOGOUT, null, null));
		} catch (IOException e1) {
			UDPChatException.behandleException(clientGUI, e1);
			System.out.println("kköj");
		}
		}
		clientGUI.client.close(clientGUI.receiveMsgThread);
		System.exit(0);
	}
}