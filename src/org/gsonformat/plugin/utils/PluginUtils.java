/*文 件 名:  PluginUtils.java
 * 修 改 人:  wenxunyu@126.com
 * 修改时间:  2017年6月20日
 */
package org.gsonformat.plugin.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.gsonformat.plugin.EditorPartException;

/**
 * 插件功能相关的工具类
 * 
 * @author wenxunyu@126.com
 * @version [2017年6月20日]
 * @since JDK7.0
 */
public final class PluginUtils {
	private PluginUtils() {
	}

	public static void tip(Shell parent, String message) {
		MessageDialog.openInformation(parent, "GsonFormat", message);
	}

	public static ICompilationUnit getCompilationUnit(IEditorPart part) throws EditorPartException {
		if (part == null) {
			throw new EditorPartException("请打开一个Java Editor");
		}
		IFile iFile = part.getEditorInput().getAdapter(IFile.class);
		if (iFile == null || !"java".equals(iFile.getFileExtension())) {
			throw new EditorPartException("请打开一个Java文件");
		}
	
		IJavaElement javaElement = JavaCore.create(iFile);
		if (!(javaElement instanceof ICompilationUnit)) {
			throw new EditorPartException("请检查此java文件");
		}
		ICompilationUnit originalUnit = (ICompilationUnit) javaElement;
		IType itype = Tools.getMainType(originalUnit);
		if (!itype.exists()) {
			throw new EditorPartException("请检查此java文件");
		}
		try {
			if (!itype.isClass()) {
				throw new EditorPartException("请使用java类");
			}
		} catch (JavaModelException e) {
			throw new EditorPartException("请使用java类");
		}
		return originalUnit;
	}

}
