package fff.triplef.udpchat.server.gui;

import java.awt.datatransfer.*;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URI;

public class Clipboard {

	public static final String key = "u%98secr3t&";

	public Clipboard() {
		super();
	}

	public void copy(String ip, String port) {
		String address = ip + ":" + port + key;
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
		System.out.println("1");
		if (paste != null && paste instanceof String) {
			System.out.println("2");
			String address = paste.toString().replace(Clipboard.key, "");
			System.out.println("adress" + address);
			System.out.println("ölkj");
			if (address != null && address.length() != 0) {
				System.out.println("ölkjizio");
				try {
					URI uri = new URI("my://" + address);
					System.out.println("tzuio" + uri.getHost() + uri.getPort());
					if (uri.getHost() != null || uri.getPort() != -1) {
						System.out.println("jlk");
						return uri.getHost() + ":" + uri.getPort() + key;
					}
				} catch (Exception e) {
				}
			}
		}
		return null;
	}
}