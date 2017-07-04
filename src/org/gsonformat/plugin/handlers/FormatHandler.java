package org.gsonformat.plugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.gsonformat.plugin.EditorPartException;
import org.gsonformat.plugin.ui.JsonEditDialog;
import org.gsonformat.plugin.utils.PluginUtils;

/**
 * 
 * 
 * @author wenxunyu@126.com
 * @version [2017年6月20日]
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 * @since JDK7.0
 */
public class FormatHandler extends AbstractHandler {
	private IWorkbenchWindow window;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IEditorPart part = window.getActivePage().getActiveEditor();
		ICompilationUnit originalUnit = null;
		try {
			originalUnit = PluginUtils.getCompilationUnit(part);
			originalUnit = originalUnit.getWorkingCopy(null);
		} catch (EditorPartException e) {
			PluginUtils.tip(window.getShell(), e.getMessage());
			return null;
		} catch (JavaModelException e) {
			PluginUtils.tip(window.getShell(), e.getMessage());
			return null;
		}
		new JsonEditDialog(window.getShell(), originalUnit, false).open();
		return null;
	}
}
