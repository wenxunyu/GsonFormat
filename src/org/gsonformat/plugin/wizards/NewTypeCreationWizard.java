/*文 件 名:  NewTypeCreationWizard.java
 * 修 改 人:  wenxunyu@126.com
 * 修改时间:  2017年6月30日
 */
package org.gsonformat.plugin.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;

/**
 * <一句话功能简述>
 * 
 * @author wenxunyu@126.com
 * @version [2017年6月30日]
 * @see [相关类/方法]
 * @since JDK7.0
 */
public class NewTypeCreationWizard extends NewElementWizard {

	private NewTypeWizardPage fPage;
	private boolean fOpenEditorOnFinish;

	public NewTypeCreationWizard(NewTypeWizardPage page, boolean openEditorOnFinish) {
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWCLASS);
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		// setWindowTitle(NewWizardMessages.NewClassCreationWizard_title);
		setWindowTitle("Gson To New Java");
		fPage = page;
		fOpenEditorOnFinish = openEditorOnFinish;
	}

	public NewTypeCreationWizard() {
		this(null, true);
	}

	/*
	 * @see Wizard#createPages
	 */
	@Override
	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage = new JavaWizardPage(getSelection());
			fPage.setWizard(this);
		}
		addPage(fPage);
	}

	@Override
	protected boolean canRunForked() {
		return !fPage.isEnclosingTypeSelected();
	}

	@Override
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createType(monitor); // use the full progress monitor
	}

	@Override
	public boolean performFinish() {
		warnAboutTypeCommentDeprecation();
		boolean res = super.performFinish();
		if (res) {
			IResource resource = fPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				if (fOpenEditorOnFinish) {
					openResource((IFile) resource);
				}
			}
		}
		return res;
	}

	@Override
	public IJavaElement getCreatedElement() {
		return fPage.getCreatedType();
	}

}
