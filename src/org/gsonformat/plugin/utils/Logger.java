/*文 件 名:  Logger.java
 * 修 改 人:  wenxunyu@126.com
 * 修改时间:  2017年6月24日
 */
package org.gsonformat.plugin.utils;

/**
 * 日志
 * 
 * @author wenxunyu@126.com
 * @version [2017年6月24日]
 * @see [相关类/方法]
 * @since JDK7.0
 */
public class Logger {
	public static void error(String error) {
		ConsoleFactory.println(error);
	}

	public static void printlnTime(String arg) {
		long time = System.currentTimeMillis();
		ConsoleFactory.println(arg != null ? arg + ":" + time : "" + time);
	}
}
