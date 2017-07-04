package org.gsonformat.plugin.popup.actions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.gsonformat.plugin.ui.JsonEditDialog;
import org.gsonformat.plugin.utils.Logger;
import org.gsonformat.plugin.utils.PluginUtils;
import org.gsonformat.plugin.wizards.JavaWizardPage;
import org.gsonformat.plugin.wizards.NewTypeCreationWizard;

/**
 * 
 * 包浏览器中java项目鼠标右键菜单事件
 * 
 * @author wenxunyu@126.com
 * @version [2017年6月20日]
 * @see [相关类/方法]
 * @since JDK7.0
 */
public class PopupAction implements IObjectActionDelegate {
	private Shell shell;
	private IWorkbenchPart workbench;

	/**
	 * Constructor for Action1.
	 */
	public PopupAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		this.workbench = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	@Override
	public void run(IAction action) {
		ISelectionProvider isp = workbench.getSite().getSelectionProvider();
		ISelection selection = isp.getSelection();
		if (selection == null || selection.isEmpty()) {
			PluginUtils.tip(shell, "请选择一个项目");
			return;
		}
		if (selection instanceof IStructuredSelection) {
			/**
			 * <pre>
			 * |Demo┐     --------------org.eclipse.jdt.internal.core.JavaProject
			 * |	├src┐ ------org.eclipse.jdt.internal.core.PackageFragmentRoot
			 * |	│   com.wen.xun┐  ----------------------------PackageFragment
			 * |	│			   ├A.java  ----------------------CompilationUnit
			 * |	│			   └B.java┐ ----------------------CompilationUnit
			 * |	│					  B┐  -------------------------SourceType
			 * |	│					   ├public int a;  -----------SourceField
			 * |	│					   └public void set(int a); -SourceMethod
			 * |	Library┐
			 * |			rt.jar┐ -----------------------JarPackageFragmentRoot
			 * |				  java.lang┐ ------------------JarPackageFragment
			 * |						   String.class ----------------ClassFile
			 * </pre>
			 * 
			 * @see http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Fguide%2Fjdt_int_model.htm&cp=3_0_0_0
			 */

			IStructuredSelection issl = (IStructuredSelection) selection;
			NewTypeWizardPage page = new JavaWizardPage(issl);

			NewTypeCreationWizard createJavaWizard = new NewTypeCreationWizard(page, true);
			createJavaWizard.init(PlatformUI.getWorkbench(), issl);
			WizardDialog dialog = new WizardDialog(workbench.getSite().getShell(), createJavaWizard);
			if (dialog.open() == WizardDialog.OK) {
				IJavaElement javaElement = createJavaWizard.getCreatedElement();
				if (javaElement instanceof SourceType) {
					javaElement = javaElement.getParent();
				}
				if (!(javaElement instanceof ICompilationUnit)) {
					PluginUtils.tip(shell, javaElement.getElementName() + "不可用");
					return;
				}
				ICompilationUnit originalUnit = (ICompilationUnit) javaElement;
				try {
					originalUnit = originalUnit.getWorkingCopy(null);
				} catch (JavaModelException e) {
					Logger.error(e.getMessage());
				}
				new JsonEditDialog(shell, originalUnit, false).open();
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
