package fff.triplef.udpchat.client.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import fff.triplef.udpchat.client.gui.ClientGUI;
import fff.triplef.udpchat.client.gui.ReceiveMessageThread;
import fff.triplef.udpchat.client.gui.WriteChat;
import fff.triplef.udpchat.exception.UDPChatException;
import fff.triplef.udpchat.message.Message;

public class Client {
	private DatagramSocket clientSocket = null;
	private String host = null;
	private int ip = 0;

	public Client() {
		super();
	}

	public Client(String host, int ip) {
		this.host = host;
		this.ip = ip;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			UDPChatException.behandleException(null, e);
		}
	}

	public void sendMessage(Message msg) throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream);
		oo.writeObject(msg);
		oo.close();
		byte[] bt = bStream.toByteArray();
		DatagramPacket sendPacket = new DatagramPacket(bt, bt.length,
				InetAddress.getByName(host), ip);
		clientSocket.send(sendPacket);
	}

	public void receiveMessage(JTextPane textPane,
			DefaultListModel<String> listModel, ClientGUI clientGUI,
			JButton btnLogin, JLabel lblActualUser, JButton btnSend,
			JButton btnImage, JTextField textFieldUsername,
			JTextArea textAreaMessage, JList list) throws IOException,
			ClassNotFoundException {
		WriteChat wc = new WriteChat(textPane);
		while (true) {
			byte[] receiveData = new byte[65000];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			clientSocket.receive(receivePacket);
			ObjectInputStream iStream = new ObjectInputStream(
					new ByteArrayInputStream(receiveData));
			Message msg = (Message) iStream.readObject();
			iStream.close();
			switch (msg.getId()) {
			case Message.ID_MESSAGE:
				wc.write("<font color='gray'>" + msg.getPublic() + "</font>"
						+ "<b>" + msg.getSender() + "</b>: " + msg.getMessage()
						+ "\n");
				break;
			case Message.ID_IMAGE:
				wc.write("<font color='gray'>" + msg.getPublic() + "</font>"
						+ "<b>" + msg.getSender() + ":\n</b>");
				Style style = textPane.getStyledDocument().addStyle(
						"StyleName", null);
				StyleConstants.setIcon(style, msg.getImage());
				try {
					textPane.getStyledDocument().insertString(
							textPane.getStyledDocument().getLength(),
							"Nothing", style);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				wc.write("\n");
				break;
			case Message.ID_LOGIN:
				if (msg.getSender().equals(clientGUI.actualUsername)) {
					lblActualUser.setText(msg.getSender());
				} else {
					listModel.addElement(msg.getSender());
				}
				wc.write("<b><font color='gray'>" + msg.getSender()
						+ " logged in\n </font></b>");
				if (clientGUI.actualUsername.equals(msg.getSender())) {
					listModel.add(0, "(All)");
					list.setSelectedIndex(0);
				}
				break;
			case Message.ID_LOGOUT:
				wc.write("<b><font color='gray'>" + msg.getSender()
						+ " logged out \n</font></b>");
				if (msg.getSender().equals(list.getSelectedValue())) {
					list.setSelectedIndex(0);
					clientGUI.setActualReciver(Message.ID_ALL);
					if (listModel.getSize() > 1)
						wc.write("<b><font color='blue'>You joined the public chat \n</font></b>");
				}
				listModel.removeElement(msg.getSender());
				break;
			case Message.ID_ONLINE:
				listModel.addElement(msg.getSender());
				break;
			case Message.ID_USERNAME_ALREADY_IN_USE:
				btnLogin.setText("Login");
				btnSend.setEnabled(false);
				btnImage.setEnabled(false);
				textAreaMessage.setEnabled(false);
				textFieldUsername.setEnabled(true);
				textFieldUsername.setText("");
				textFieldUsername.requestFocus();
				JOptionPane.showMessageDialog(clientGUI,
						"Username is already in use", clientGUI.getTitle(),
						JOptionPane.INFORMATION_MESSAGE);
				break;
			default:
				break;
			}
		}
	}

	public void close(ReceiveMessageThread rmt) {
		clientSocket.close();
	}
}