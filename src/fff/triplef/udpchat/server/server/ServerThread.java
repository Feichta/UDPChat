package fff.triplef.udpchat.server.server;

public class ServerThread extends Thread
{
	private Server server = null;
	private int port = 0;

	public ServerThread(Server server, int port) {
		this.server = server;
		this.port = port;
		this.start();
	}

	@Override
	public void run() {
		this.server.start(port);
	}
}