package fff.triplef.udpchat.server.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Enumeration;
import java.util.Hashtable;

import fff.triplef.udpchat.exception.UDPChatException;
import fff.triplef.udpchat.message.Message;

public class Server
{
	private Hashtable<String, InetSocketAddress> clients = null;
	private DatagramSocket serverSocket = null;

	public Server() {
		super();
	}

	private void sendMessage(boolean publiC, Message msg, InetSocketAddress isa)
			throws IOException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(bStream);
		msg.setPublic(publiC);
		oo.writeObject(msg);
		oo.close();
		byte[] bt = bStream.toByteArray();
		DatagramPacket sendPacket = new DatagramPacket(bt, bt.length,
				isa.getAddress(), isa.getPort());
		serverSocket.send(sendPacket);
	}

	public void receiveMessage() throws IOException, ClassNotFoundException {
		Message msg = null;
		while (true) {
			byte[] receiveData = new byte[65000];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			serverSocket.receive(receivePacket);
			ObjectInputStream iStream = new ObjectInputStream(
					new ByteArrayInputStream(receiveData));
			msg = (Message) iStream.readObject();
			iStream.close();
			switch (msg.getId()) {
				case Message.ID_MESSAGE:
					iterateClientsAndSendMessage(msg, Message.ID_MESSAGE);
					break;
				case Message.ID_IMAGE:
					iterateClientsAndSendMessage(msg, Message.ID_IMAGE);
					break;
				case Message.ID_LOGIN:
					if (containsClient(msg.getSender())) {
						InetSocketAddress isa = new InetSocketAddress(
								receivePacket.getAddress(), receivePacket.getPort());
						Message msgTemp = new Message(null, null,
								Message.ID_USERNAME_ALREADY_IN_USE, null, null);
						sendMessage(false, msgTemp, isa);
					} else {
						insertClient(receivePacket, msg.getSender());
						iterateClientsAndSendMessage(msg, Message.ID_LOGIN);
						sendOtherClientsToClient(msg.getSender());
					}
					break;
				case Message.ID_LOGOUT:
					iterateClientsAndSendMessage(msg, Message.ID_LOGOUT);
					try {
						deleteClient(msg.getSender());
					} catch (Exception e) {
					}
					break;
				default:
					break;
			}
		}
	}

	private void iterateClientsAndSendMessage(Message msg, int id)
			throws IOException {
		if (msg.getReceiver().equals(Message.ID_ALL)) {
			Enumeration<String> usernames = clients.keys();
			while (usernames.hasMoreElements()) {
				String username = usernames.nextElement();
				InetSocketAddress isa = clients.get(username);
				Message msgTemp = new Message(msg.getSender(), Message.ID_ALL, id,
						msg.getMessage(), msg.getImage());
				sendMessage(true, msgTemp, isa);
			}
		} else {
			InetSocketAddress[] isas = new InetSocketAddress[2];
			isas[0] = clients.get(msg.getSender());
			isas[1] = clients.get(msg.getReceiver());
			for (InetSocketAddress isa : isas) {
				sendMessage(false, msg, isa);
			}
		}
	}

	private void sendOtherClientsToClient(String receiver) throws IOException {
		InetSocketAddress isa = clients.get(receiver);
		Enumeration<String> e = clients.keys();
		while (e.hasMoreElements()) {
			String username = e.nextElement();
			if (username != receiver) {
				Message msg = new Message(username, null, Message.ID_ONLINE, null, null);
				sendMessage(false, msg, isa);
			}
		}
	}

	private boolean containsClient(String username) {
		return clients.containsKey(username);
	}

	private void insertClient(DatagramPacket dp, String username) {
		clients.put(username, new InetSocketAddress(dp.getAddress(), dp.getPort()));
	}

	private void deleteClient(String username) {
		clients.remove(username);
	}

	public void start(int port) {
		clients = new Hashtable<>();
		try {
			serverSocket = new DatagramSocket(port);
			receiveMessage();
		} catch (ClassNotFoundException | IOException e) {
			UDPChatException.behandleException(null, e);
		}
	}

	public void stop() {
		if (serverSocket.isBound() || serverSocket.isConnected()) {
			serverSocket.close();
		}
		serverSocket = null;
	}

	public boolean isStarted() {
		boolean ret = false;
		if (serverSocket != null) {
			ret = serverSocket.isConnected() || serverSocket.isBound();
		}
		return ret;
	}
}