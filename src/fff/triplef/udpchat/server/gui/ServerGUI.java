package fff.triplef.udpchat.server.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import fff.triplef.udpchat.exception.UDPChatException;
import fff.triplef.udpchat.server.server.Server;
import fff.triplef.udpchat.server.server.ServerThread;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ServerGUI extends JFrame
{
	// GUI
	private JLabel lblHost;
	private JLabel lblPort;
	private JSpinner spinner;
	private JTextField textFieldHostName;
	private JToggleButton tglbtnStartServer;
	private JLabel lblServerState;

	// Server
	private Server server = null;

	// Thread
	ServerThread serverThread = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ServerGUI();
	}

	public ServerGUI() {
		// Thread.setDefaultUncaughtExceptionHandler(new RuntimeExceptionHandler());
		server = new Server();
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
		setTitle("UDP Server");
		setSize(430, 250);
		setLocationRelativeTo(null);
		try {
			setIconImage(ImageIO.read(ServerGUI.class
					.getResource("/fff/triplef/udpchat/client/img/icon.png")));
		} catch (IOException e2) {
		}
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);

		getContentPane().setLayout(null);

		lblHost = new JLabel("Host:");
		lblHost.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblHost.setBounds(10, 15, 62, 42);
		getContentPane().add(lblHost);

		lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblPort.setBounds(10, 67, 62, 42);
		getContentPane().add(lblPort);

		textFieldHostName = new JTextField("");
		textFieldHostName.setFont(new Font("Tahoma", Font.PLAIN, 18));
		setHostName();
		textFieldHostName.setEditable(false);
		textFieldHostName.setBounds(84, 16, 250, 42);
		getContentPane().add(textFieldHostName);

		spinner = new JSpinner();
		spinner.setFont(new Font("Tahoma", Font.PLAIN, 18));
		spinner.setModel(new SpinnerNumberModel(50000, 49152, 65535, 1));
		spinner.setBounds(87, 68, 125, 42);
		getContentPane().add(spinner);

		tglbtnStartServer = new JToggleButton("");
		tglbtnStartServer.setIcon(new ImageIcon(ServerGUI.class
				.getResource("/fff/triplef/udpchat/server/img/off.png")));
		tglbtnStartServer.setBounds(316, 67, 84, 42);
		tglbtnStartServer.addItemListener(new ItemListener()
		{

			@SuppressWarnings("deprecation")
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					serverThread = new ServerThread(server, (int) (spinner.getValue()));
					new Clipboard().copy(textFieldHostName.getText().split("/")[1],
							spinner.getValue().toString());
					tglbtnStartServer.setIcon(new ImageIcon(ServerGUI.class
							.getResource("/fff/triplef/udpchat/server/img/on.png")));
					lblServerState.setText("Server running...");
					spinner.setEnabled(false);
					lblServerState.setForeground(Color.GREEN);
				} else
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						if (server.isStarted()) {
							server.stop();
						}
						serverThread.stop();
						tglbtnStartServer.setIcon(new ImageIcon(ServerGUI.class
								.getResource("/fff/triplef/udpchat/server/img/off.png")));
						lblServerState.setText("Server stopped");
						spinner.setEnabled(true);
						lblServerState.setForeground(Color.RED);
					}
			}
		});
		getContentPane().add(tglbtnStartServer);

		lblServerState = new JLabel("Server not running", JLabel.CENTER);
		lblServerState.setFont(new Font("Segoe UI", Font.PLAIN, 42));
		lblServerState.setForeground(Color.RED);
		lblServerState.setBounds(10, 126, 390, 68);
		getContentPane().add(lblServerState);

		JButton btnCopy = new JButton("");
		btnCopy.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				new Clipboard().copy(textFieldHostName.getText().split("/")[1], spinner
						.getValue().toString());
			}
		});
		btnCopy.setIcon(new ImageIcon(ServerGUI.class
				.getResource("/fff/triplef/udpchat/server/img/copy.png")));
		btnCopy.setBounds(358, 15, 42, 42);
		getContentPane().add(btnCopy);

		addWindowListener(new WindowClosing(server));

		setVisible(true);
	}

	private void setHostName() {
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			UDPChatException.behandleException(this, e);
		}
		if (ip != null) {
			textFieldHostName.setText(String.valueOf(ip));
		} else {
			textFieldHostName.setText("Can not find hostname");
		}
	}
}