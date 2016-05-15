package fff.triplef.udpchat.server.gui;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;

public class Clipboard {
	public static final String key = "u%98secr3t&";

	public Clipboard() {
		super();
	}

	public void copy(String ip, String port) {
		String address = ip + key + port;
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
			if (paste.toString().contains(Clipboard.key)) {
				String address = paste.toString().replace(Clipboard.key, ":");
				if (address != null && address.length() != 0) {
					try {
						URI uri = new URI("my://" + address);
						if (uri.getHost() != null
								&& !uri.getHost().contains("http")
								&& uri.getPort() != -1) {
							return uri.getHost() + key + uri.getPort();
						}
					} catch (Exception e) {
					}
				}
			}
		}
		return null;
	}
}