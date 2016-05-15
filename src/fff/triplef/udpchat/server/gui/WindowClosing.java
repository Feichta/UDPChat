package fff.triplef.udpchat.server.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import fff.triplef.udpchat.server.server.Server;

public class WindowClosing extends WindowAdapter
{
	private Server server = null;

	public WindowClosing(Server server) {
		this.server = server;
	}

	public void windowClosing(WindowEvent e) {
		if (server.isStarted()) {
			server.stop();
		}
		System.exit(0);
	}
}