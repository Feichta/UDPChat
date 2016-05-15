package fff.triplef.udpchat.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;

public class RuntimeExceptionHandler implements UncaughtExceptionHandler {
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		String message = "An errorrrr has occurred in class "
				+ e.getClass().getName() + ". \n" + e.getMessage();
		JOptionPane.showMessageDialog(null, message, e.getClass().getName(),
				JOptionPane.ERROR_MESSAGE);
	}
}