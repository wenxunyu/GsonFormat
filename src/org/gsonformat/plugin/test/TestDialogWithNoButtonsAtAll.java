/*文 件 名:  TestDialogWithNoButtonsAtAll.java
 * 修 改 人:  wenxunyu@126.com
 * 修改时间:  2017年6月29日
 */
package org.gsonformat.plugin.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A test harness class to test the dialog with no default buttons
 * 
 * @author Debadatta Mishra(PIKU)
 * @see http://www.javaworld.com/article/2073186/custom-jface-dialog-creation.html
 */
public class TestDialogWithNoButtonsAtAll {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display, SWT.BORDER);
		// Create a button in the blank shell
		Button openBtn = new Button(shell, SWT.PUSH);
		String dialogBtnMsg = "Open a new Dialog with no X button";
		openBtn.setText(dialogBtnMsg);
		openBtn.setBounds(10, 10, dialogBtnMsg.length() * 8, 30);
		// Listener for the open Button
		openBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				new DialogWithNoButtonsAtAll(shell).open();
			}
		});

		// Create a close button
		Button closeBtn = new Button(shell, SWT.PUSH);
		String closeBtnString = "Close shell";
		closeBtn.setText(closeBtnString);
		closeBtn.setBounds(10, 50, closeBtnString.length() * 8, 30);
		// Listener for the close button
		closeBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				shell.dispose();
			}
		});

		shell.setSize(400, 200);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
