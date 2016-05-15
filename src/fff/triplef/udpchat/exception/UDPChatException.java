package fff.triplef.udpchat.exception;

import java.awt.Component;

import javax.swing.JOptionPane;

public class UDPChatException
{
	public static void behandleException(Component component, Exception e) {
		String message = "An error has occurred in class " + e.getClass().getName()
				+ ". \n" + e.getMessage();
		JOptionPane.showMessageDialog(component, message, e.getClass().getName(),
				JOptionPane.ERROR_MESSAGE);
	}
}