/*文 件 名:  DialogWithNoButtonsAtAll.java
 * 修 改 人:  wenxunyu@126.com
 * 修改时间:  2017年6月29日
 */
package org.gsonformat.plugin.test;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This class is used to create a jface dialog box with no default buttons. Run
 * it to see the effect.
 * 
 * @author Debadatta Mishra(PIKU)
 * @see http://www.javaworld.com/article/2073186/custom-jface-dialog-creation.html
 */
public final class DialogWithNoButtonsAtAll extends Dialog {
	/**
	 * Default constructor
	 * 
	 * @param shell
	 *            of type {@link Shell}
	 * @author Debadatta Mishra(PIKU)
	 */
	public DialogWithNoButtonsAtAll(Shell shell) {
		super(shell);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#setShellStyle(int)
	 */
	protected void setShellStyle(int arg0) {
		// Use the following not to show the default close X button in the title
		// bar
		super.setShellStyle(SWT.TITLE | SWT.SHEET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets
	 * .Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		/*
		 * Create the dialog area where you can place the UI components
		 */
		Composite composite = (Composite) super.createDialogArea(parent);
		// Set the shell message
		composite.getShell().setText("A dialog box with no buttons at all press 'ESC' to close");
		try {
			composite.setLayout(new FormLayout());
			{
				// Place all your UI Components
				/*
				 * I have created the dummy UI components so that you will feel
				 * comfortable
				 */
				// Create a Label
				createLabel(composite);
				// Create a Text field
				createTextField(composite);
				// Create a push button
				createButton(composite);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Set the size of the parent shell
		composite.getShell().setSize(300, 100);
		// Set the dialog position in the middle of the monitor
		setDialogLocation();
		return composite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.
	 * Composite, int, java.lang.String, boolean)
	 */
	protected Button createButton(Composite arg0, int arg1, String arg2, boolean arg3) {
		// Retrun null so that no default buttons like 'OK' and 'Cancel' will be
		// created
		return null;
	}

	// ~~ UI creation methods

	/**
	 * Method to create a Label
	 * 
	 * @param composite
	 *            of type {@link Composite}
	 * @author Debadatta Mishra(PIKU)
	 */
	private void createLabel(Composite composite) {
		Label label = new Label(composite, SWT.None);
		label.setText("Label 1");
		FormData lblData = new FormData();
		lblData.width = 40;
		lblData.height = 20;
		lblData.left = new FormAttachment(0, 1000, 6);// x co-ordinate
		lblData.top = new FormAttachment(0, 1000, 17);// y co-ordinate
		label.setLayoutData(lblData);
	}

	/**
	 * Method to create a text field
	 * 
	 * @param composite
	 *            of type {@link Composite}
	 * @author Debadatta Mishra(PIKU)
	 */
	private void createTextField(Composite composite) {
		Text text = new Text(composite, SWT.None);
		text.setText("Some text data");
		FormData txtData = new FormData();
		txtData.width = 100;
		txtData.height = 20;
		txtData.left = new FormAttachment(0, 1000, 50);// x co-ordinate
		txtData.top = new FormAttachment(0, 1000, 17);// y co-ordinate
		text.setLayoutData(txtData);
	}

	/**
	 * Method to create a push button
	 * 
	 * @param composite
	 *            of type {@link Composite}
	 * @author Debadatta Mishra(PIKU)
	 */
	private void createButton(Composite composite) {
		Button btn = new Button(composite, SWT.PUSH);
		btn.setText("Press to close");
		FormData btnData = new FormData();
		btnData.width = 90;
		btnData.height = 20;
		btnData.left = new FormAttachment(0, 1000, 100);// x co-ordinate
		btnData.top = new FormAttachment(0, 1000, 40);// y co-ordinate
		btn.setLayoutData(btnData);
		// Write listener for button
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				close();
			}
		});
	}

	// ~~ Utility methods

	/**
	 * Method used to set the dialog in the centre of the monitor
	 * 
	 * @author Debadatta Mishra(PIKU)
	 */
	private void setDialogLocation() {
		Rectangle monitorArea = getShell().getDisplay().getPrimaryMonitor().getBounds();
		Rectangle shellArea = getShell().getBounds();
		int x = monitorArea.x + (monitorArea.width - shellArea.width) / 2;
		int y = monitorArea.y + (monitorArea.height - shellArea.height) / 2;
		getShell().setLocation(x, y);
	}
}
