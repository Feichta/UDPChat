package fff.triplef.udpchat.client.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;

import fff.triplef.udpchat.client.client.Client;
import fff.triplef.udpchat.exception.UDPChatException;
import fff.triplef.udpchat.message.Message;
import fff.triplef.udpchat.server.gui.Clipboard;

public class ClientGUI extends JFrame
{
	// GUI
	protected JTextField textFieldUsername;
	protected JButton btnLogin;
	protected JButton btnImage;
	protected JTextPane textPaneChat;
	protected WriteChat wc;
	protected JTextArea textAreaMessage;
	protected JList list;
	protected JButton btnSend;
	protected JLabel lblActualuser;
	protected JScrollPane scrollPaneMessage;
	protected JScrollPane scrollPaneChat;
	private JLabel lblLogo;
	private JScrollPane scrollPaneList;
	protected DefaultListModel<String> listModel = new DefaultListModel();

	// Title
	protected static final String TITLE = "UDP Chat";

	// Client
	protected Client client = null;

	// actual username
	public String actualUsername = null;

	// actual receiver
	protected String actualReceiver = null;

	// Thread
	protected ReceiveMessageThread receiveMsgThread = null;

	public static void main(String[] args) {
		new ClientGUI();
	}

	// LookAndFeel
	public ClientGUI() {
		// Thread.setDefaultUncaughtExceptionHandler(new RuntimeExceptionHandler());
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			UDPChatException.behandleException(this, e);
		}
		// ServerConnection
		String hostName = null;
		int ip = 0;
		boolean invalidData = false;
		do {
			invalidData = false;
			final JTextField textFieldHostName = new JTextField();
			JTextField textFieldPort = new JTextField();
			if (new Clipboard().paste() != null) {
				String[] cb = new Clipboard().paste().split(":");
				textFieldHostName.setText(cb[0]);
				textFieldPort.setText(cb[1]);
			}
			Object[] message = { "Hostname or IP", textFieldHostName, "Port",
					textFieldPort };
			int option = JOptionPane.showConfirmDialog(null, message,
					"Type in server characteristics", JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION) {
				if ((textFieldHostName.getText().length() > 0)
						&& (textFieldPort.getText().length() > 0)) {
					hostName = textFieldHostName.getText();
					char[] chars = textFieldPort.getText().toCharArray();
					for (char c : chars) {
						if (!Character.isDigit(c)) {
							invalidData = true;
							break;
						}
					}
					if (!invalidData) {
						ip = Integer.parseInt(textFieldPort.getText());
					}
				} else {
					invalidData = true;
				}
			} else {
				System.exit(0);
			}
		} while (invalidData);

		// ClientWindow
		client = new Client(hostName, ip);
		setTitle(TITLE);
		try {
			setIconImage(ImageIO.read(ClientGUI.class
					.getResource("/fff/triplef/udpchat/client/img/icon.png")));
		} catch (IOException e2) {
		}
		setSize(532, 406);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);

		getContentPane().setLayout(null);

		// Logo
		lblLogo = new JLabel("");
		lblLogo.setBounds(10, 16, 90, 28);
		lblLogo.setIcon(new ImageIcon(ClientGUI.class
				.getResource("/fff/triplef/udpchat/client/img/logo.png")));
		getContentPane().add(lblLogo);
		// Login Textfield
		textFieldUsername = new JTextField();
		textFieldUsername.setBounds(110, 18, 174, 28);
		getContentPane().add(textFieldUsername);
		textFieldUsername.setColumns(10);
		textFieldUsername.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				if (btnLogin.getText().equals("Login")) {
					login();
				}
			}
		});
		// LoginButton
		btnLogin = new JButton("Login");
		btnLogin.setMnemonic('l');
		btnLogin.setBounds(299, 18, 89, 28);
		btnLogin.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ev) {
				if (btnLogin.getText().equals("Login")) {
					login();
				} else {
					if (btnLogin.getText().equals("Logout")) {
						try {
							client.sendMessage(new Message(actualUsername, Message.ID_ALL,
									Message.ID_LOGOUT, null, null));
						} catch (IOException e1) {
							UDPChatException.behandleException(ClientGUI.this, e1);
						}
						actualUsername = null;
						btnLogin.setText("Login");
						textFieldUsername.setText("");
						textPaneChat.setText("");
						textAreaMessage.setText("");
						lblActualuser.setText("");
						btnSend.setEnabled(false);
						btnImage.setEnabled(false);
						textAreaMessage.setEnabled(false);
						textFieldUsername.setEnabled(true);
						listModel.removeAllElements();
						textFieldUsername.requestFocus();
					}
				}
			}
		});
		getContentPane().add(btnLogin);
		// User
		lblActualuser = new JLabel("", JLabel.CENTER);
		lblActualuser.setForeground(new Color(44, 63, 146));
		lblActualuser.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblActualuser.setBounds(393, 18, 122, 28);
		getContentPane().add(lblActualuser);
		// Chat
		scrollPaneChat = new JScrollPane();
		scrollPaneChat.setBounds(10, 47, 373, 234);
		getContentPane().add(scrollPaneChat);

		textPaneChat = new JTextPane();
		textPaneChat.setContentType("text/html");
		textPaneChat.getDocument().putProperty(
				DefaultEditorKit.EndOfLineStringProperty, "<br/>\n");
		textPaneChat.setFocusable(false);
		textPaneChat.setEditable(false);
		wc = new WriteChat(textPaneChat);
		scrollPaneChat.setViewportView(textPaneChat);
		DefaultCaret caret = (DefaultCaret) textPaneChat.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// List
		scrollPaneList = new JScrollPane();
		scrollPaneList
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneList.setBounds(393, 54, 122, 227);
		getContentPane().add(scrollPaneList);

		list = new JList();
		scrollPaneList.setViewportView(list);
		list.setModel(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					actualReceiver = list.getSelectedValue().toString();
					if (actualReceiver.equals(Message.ID_ALL)) {
						wc.write("<b><font color='blue'>You joined the public chat \n</font></b>");
					} else {
						wc.write("<b><font color='blue'>You joined the conversation with "
								+ actualReceiver + "\n </font></b>");
					}
				}
				textAreaMessage.requestFocus();
				textAreaMessage.setCaretPosition(textAreaMessage.getDocument()
						.getLength());
			}
		});
		// Message
		scrollPaneMessage = new JScrollPane();
		scrollPaneMessage.setBounds(10, 286, 373, 72);
		getContentPane().add(scrollPaneMessage);

		textAreaMessage = new JTextArea();
		textAreaMessage.setLineWrap(true);
		scrollPaneMessage.setViewportView(textAreaMessage);
		textAreaMessage.setEnabled(false);
		textAreaMessage.setColumns(10);

		// EnterSenden ShiftEnter neue Zeile
		InputMap input = textAreaMessage.getInputMap();
		KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
		KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
		input.put(shiftEnter, "insert-break");
		input.put(enter, "text-submit");
		ActionMap actions = textAreaMessage.getActionMap();
		actions.put("text-submit", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e) {
				if (textAreaMessage.getText() != null
						&& textAreaMessage.getText().length() != 0) {
					Message msg = new Message(actualUsername, actualReceiver,
							Message.ID_MESSAGE, textAreaMessage.getText(), null);
					try {
						client.sendMessage(msg);
						textAreaMessage.setText("");
					} catch (IOException e1) {
						UDPChatException.behandleException(ClientGUI.this, e1);
					}
				}
			}
		});
		// Button Send
		btnSend = new JButton("");
		btnSend.setIcon(new ImageIcon(ClientGUI.class
				.getResource("/fff/triplef/udpchat/client/img/send.png")));
		btnSend.setBounds(393, 300, 42, 42);
		btnSend.setEnabled(false);
		btnSend.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				if (textAreaMessage.getText() != null
						&& textAreaMessage.getText().length() != 0) {
					Message msg = new Message(actualUsername, actualReceiver,
							Message.ID_MESSAGE, textAreaMessage.getText(), null);
					try {
						client.sendMessage(msg);
						textAreaMessage.setText("");
					} catch (IOException e1) {
						UDPChatException.behandleException(ClientGUI.this, e1);
					}
				}
				textAreaMessage.requestFocus();
				textAreaMessage.setCaretPosition(textAreaMessage.getDocument()
						.getLength());
			}
		});
		getContentPane().add(btnSend);

		// SendImage
		btnImage = new JButton("");
		btnImage.setEnabled(false);
		btnImage.setIcon(new ImageIcon(ClientGUI.class
				.getResource("/fff/triplef/udpchat/client/img/image.png")));
		btnImage.setSelectedIcon(new ImageIcon(ClientGUI.class
				.getResource("/fff/triplef/udpchat/client/img/image.png")));
		btnImage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.home")
						+ "\\Pictures"));
				fc.setMultiSelectionEnabled(false);
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new FileFilter()
				{
					public String getDescription() {
						return "Images (.gif .jpeg .jpg)";
					}

					public boolean accept(File f) {
						if (f.isDirectory()) {
							return true;
						}
						String extension = Utils.getExtension(f);
						if (extension != null) {
							if ((extension.equals(Utils.gif) || extension.equals(Utils.jpeg) || extension
									.equals(Utils.jpg)) && f.length() < 6000) {
								return true;
							} else {
								return false;
							}
						}
						return true;
					}
				});
				int ok = fc.showOpenDialog(ClientGUI.this);
				if (ok == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					ImageIcon ii = null;
					try {
						ii = new ImageIcon(ImageIO.read(file));
					} catch (IOException e1) {
						UDPChatException.behandleException(ClientGUI.this, e1);
					}
					if (ii != null) {
						Message msg = new Message(actualUsername, actualReceiver,
								Message.ID_IMAGE, null, ii);
						try {
							client.sendMessage(msg);
						} catch (IOException e1) {
							UDPChatException.behandleException(ClientGUI.this, e1);
						}
					}
				}
			}
		});
		btnImage.setBounds(450, 300, 42, 42);
		getContentPane().add(btnImage);
		// Random
		actualReceiver = Message.ID_ALL;

		receiveMsgThread = new ReceiveMessageThread(this);

		addWindowListener(new WindowClosing(this));

		setVisible(true);
	}

	private void login() {
		if (textFieldUsername.getText() == null
				|| textFieldUsername.getText().length() == 0) {
			JOptionPane.showMessageDialog(ClientGUI.this, "Please insert a username",
					ClientGUI.this.getTitle(), JOptionPane.INFORMATION_MESSAGE);
			textAreaMessage.requestFocus();
			textAreaMessage.setCaretPosition(textAreaMessage.getDocument()
					.getLength());
		} else {
			if (validateUsername(textFieldUsername.getText())) {
				actualUsername = textFieldUsername.getText();
				try {
					client.sendMessage(new Message(actualUsername, Message.ID_ALL,
							Message.ID_LOGIN, null, null));
				} catch (IOException e1) {
					UDPChatException.behandleException(ClientGUI.this, e1);
				}
				btnLogin.setText("Logout");
				textPaneChat.setText("");
				textAreaMessage.setText("");
				btnSend.setEnabled(true);
				btnImage.setEnabled(true);
				textAreaMessage.setEnabled(true);
				textFieldUsername.setEnabled(false);
				textAreaMessage.requestFocus();
				textAreaMessage.setCaretPosition(textAreaMessage.getDocument()
						.getLength());
			} else {
				JOptionPane.showMessageDialog(ClientGUI.this,
						"This is not a valid username", ClientGUI.this.getTitle(),
						JOptionPane.ERROR_MESSAGE);
				textFieldUsername.requestFocus();
			}
		}
	}

	public boolean validateUsername(String s) {
		boolean ret = true;
		char[] chars = s.toCharArray();
		for (char c : chars) {
			if (!Character.isLetterOrDigit(c)) {
				ret = false;
				break;
			}
		}
		return ret;
	}

	public void setActualReciver(String id) {
		this.actualReceiver = id;
	}
}