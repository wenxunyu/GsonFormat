/*文 件 名:  EditorAction.java
 * 版    权:  Songge Co., Ltd. Copyright 2017年6月20日,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  wenxunyu@126.com
 * 修改时间:  2017年6月20日
 */
package org.gsonformat.plugin.popup.actions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.gsonformat.plugin.EditorPartException;
import org.gsonformat.plugin.ui.JsonEditDialog;
import org.gsonformat.plugin.utils.PluginUtils;

/**
 * java编辑器中鼠标右键菜单事件
 * 
 * @author wenxunyu@126.com
 * @version [2017年6月20日]
 * @see http://help.eclipse.org/kepler/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/workbench_cmd_expressions.htm
 * @since JDK8.0
 */
public class EditorAction implements IEditorActionDelegate {
	private ISelection selection = null;
	private Shell shell;

	@Override
	public void run(IAction action) {
		IEditorPart part = getEditor();
		ICompilationUnit originalUnit = null;
		try {
			originalUnit = PluginUtils.getCompilationUnit(part);
			originalUnit = originalUnit.getWorkingCopy(null);
		} catch (EditorPartException e) {
			PluginUtils.tip(shell, e.getMessage());
			return;
		} catch (JavaModelException e) {
			PluginUtils.tip(shell, e.getMessage());
			return;
		}
		new JsonEditDialog(shell, originalUnit, false).open();
	}

	public IEditorPart getEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	protected void selection() {
		if (selection != null && !selection.isEmpty()) {
			TextSelection is = (TextSelection) selection;
			PluginUtils.tip(shell, "选中的文本是：" + is.getText());
		} else {
			PluginUtils.tip(shell, "没有选中文本");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor == null || targetEditor.getSite() == null) {
			return;
		}
		shell = targetEditor.getSite().getShell();
	}

	/**
	 * <pre>
	 * IMember member= ...; 
	 * int memberType= member.getElementType();
	 * if(memberType == IJavaElement.METHOD) {//是否为方法
	 * 		int flags=member.getFlags();
	 *		boolean isPublicMethod = Flags.isPublic(flags);//是否为public修饰符 //操作Method
	 * }
	 * 举一反三，我们通过Flags类，可以知晓IMember的信息。
	 * 
	 * 
	 * 2.使用JDT API中的AST JDT会把Java代码编译成AST(Abstract Syntax
	 * Tree,抽象语法树)，这样复杂的Java代码就变成了相对简单的树状结构，我们就可以通过AST来遍历Java代码，从而解析代码或者对代码进行修改，Eclipse中的Java代码重构就是基于AST来进行的。
	 * 在Eclipse中AST被称为CompilationUnit,对应的接口就是ICompilationUnit,通过Java代码来生成CompilationUnit最简单的方法就是使用IPackageFragment接口中的方法
	 * createCompilationUnit。指定编译单元的名称和内容，于是在包中创建了编译单元，并返回新的ICompilationUnit。我们还可以从头创建一CompilationUnit，
	 * 即生成一个不依赖于Java代码的CompilationUnit，然后在这个CompilationUnit上添加类、添加方法、添加代码，然后调用JDT的AST解析器将CompilationUnit输出成Java代码。
	 * 这种方式是最严谨的方式，但是当要生成的代码比较复杂的时候程序就变得臃肿无比，而且只能生成Java代码，不能生成XML配置文件等格式。
	 * </pre>
	 */

}
