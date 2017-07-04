/*文 件 名:  ConsoleFactory.java
 * 修 改 人:  wenxunyu@126.com
 * 修改时间:  2017年6月21日
 */
package org.gsonformat.plugin.utils;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * 获取控制台
 * 
 * @author wenxunyu@126.com
 * @version [2017年6月21日]
 * @see http://blog.csdn.net/li_canhui/article/details/6967901
 * @since JDK7.0
 */
public class ConsoleFactory {

	private static MessageConsole console = new MessageConsole("", null);
	static boolean exists = false;

	/**
	 * 描述:打开控制台
	 */
	public void openConsole() {
		showConsole();
	}

	/** */
	/**
	 * 描述:显示控制台
	 */
	private static void showConsole() {
		if (console != null) {
			// 得到默认控制台管理器
			IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();

			// 得到所有的控制台实例
			IConsole[] existing = manager.getConsoles();
			exists = false;
			// 新创建的MessageConsole实例不存在就加入到控制台管理器，并显示出来
			for (int i = 0; i < existing.length; i++) {
				if (console == existing[i])
					exists = true;
			}
			if (!exists) {
				manager.addConsoles(new IConsole[] { console });
			}

			// console.activate();

		}
	}

	/** */
	/**
	 * 描述:关闭控制台
	 */
	public static void closeConsole() {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		if (console != null) {
			manager.removeConsoles(new IConsole[] { console });
		}
	}

	/**
	 * 获取控制台
	 * 
	 * @return
	 */
	public static MessageConsole getConsole() {

		showConsole();

		return console;
	}

	/**
	 * 向控制台打印一条信息，并激活控制台。
	 * 
	 * @param message
	 * @param activate
	 *            是否激活控制台
	 */
	public static void println(String string) {
		println(string, true);
	}

	public static void println(String message, boolean activate) {
		MessageConsoleStream printer = ConsoleFactory.getConsole().newMessageStream();
		printer.setActivateOnWrite(activate);
		printer.println(message);
	}

}
