/*文 件 名:  TestCaseWizardPage.java
 * 修 改 人:  wenxunyu@126.com
 * 修改时间:  2017年6月30日
 */
package org.gsonformat.plugin.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author wenxunyu@126.com
 * @version [2017年6月30日]
 * @see [相关类/方法]
 * @since JDK7.0
 */
public class JavaWizardPage extends NewTypeWizardPage {

	public JavaWizardPage(IStructuredSelection selection) {
		super(true, "GsonFormat");
		setTitle(NewWizardMessages.NewClassWizardPage_title);
		setDescription(NewWizardMessages.NewClassWizardPage_description);
		init(selection);
	}

	/**
	 * The wizard managing this wizard page must call this method during
	 * initialization with a corresponding selection.
	 */
	public void init(IStructuredSelection selection) {
		IJavaElement jelem = getInitialJavaElement(selection);
		initContainerPage(jelem);
		initTypePage(jelem);
		doStatusUpdate();
	}

	private void doStatusUpdate() {
		// define the components for which a status is desired
		IStatus[] status = new IStatus[] { fContainerStatus,
				isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus, fTypeNameStatus, };
		updateStatus(status);
	}

	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		doStatusUpdate();
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		int nColumns = 4;
		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// Create the standard input fields
		// createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		createSeparator(composite, nColumns);
		createTypeNameControls(composite, nColumns);
		// createSuperClassControls(composite, nColumns);
		setControl(composite);
		// setSuperClass("java.lang.Object", false);
		setErrorMessage("ErrorMessage");
	}
}
