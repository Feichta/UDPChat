package fff.triplef.udpchat.message;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class Message implements Serializable
{
	// receiver
	public static final String ID_ALL = "(All)";

	// id
	public static final int ID_MESSAGE = 0;
	public static final int ID_LOGIN = 1;
	public static final int ID_LOGOUT = 2;
	public static final int ID_ONLINE = 3;
	public static final int ID_USERNAME_ALREADY_IN_USE = 4;
	public static final int ID_IMAGE = 5;

	private String sender = null;
	private String receiver = null;
	private int id = 0;
	private String message = null;
	private ImageIcon image = null;
	
	private boolean publiC = false;

	public Message() {
		super();
	}

	public Message(String sender, String receiver, int id, String message,
			ImageIcon image) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.id = id;
		this.message = message;
		this.image = image;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	public String toString() {
		return sender + ";" + receiver + ";" + id + ";" + message;
	}

	public void setPublic(boolean publiC) {
		this.publiC = publiC;
	}

	public String getPublic() {
		if (this.publiC) {
			return "[Public] ";
		} else {
			return "[Private] ";
		}
	}
}