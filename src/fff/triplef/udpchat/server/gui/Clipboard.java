package fff.triplef.udpchat.server.gui;

import java.awt.datatransfer.*;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URI;

public class Clipboard
{

	public Clipboard() {
		super();
	}

	public void copy(String ip, String port) {
		String address = ip + ":" + port;
		StringSelection stringSelection = new StringSelection(address);
		java.awt.datatransfer.Clipboard clpbrd = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}

	public String paste() {
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		Object paste = null;
		try {
			paste = defaultToolkit.getSystemClipboard().getData(
					DataFlavor.stringFlavor);
		} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
		}
		if (paste != null && paste instanceof String) {
			String address = paste.toString();
			if (address != null && address.length() != 0) {
				try {
					URI uri = new URI("my://" + address);
					if (uri.getHost() != null || uri.getPort() != -1) {
						return uri.getHost() + ":" + uri.getPort();
					}
				} catch (Exception e) {
				}
			}
		}
		return null;
	}
}