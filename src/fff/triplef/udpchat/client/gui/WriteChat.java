package fff.triplef.udpchat.client.gui;

import java.io.IOException;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;

public class WriteChat
{
	private JTextPane textPaneChat = null;

	public WriteChat(JTextPane textPaneChat) {
		this.textPaneChat = textPaneChat;
	}

	public void write(String text) {
		HTMLDocument doc = (HTMLDocument) this.textPaneChat.getStyledDocument();
		try {
			doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),
					text.replace("\n", "<br>"));
		} catch (BadLocationException | IOException e) {
			e.printStackTrace();
		}
	}
}