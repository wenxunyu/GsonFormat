package org.gsonformat.plugin.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class ToolBarAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public ToolBarAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see http://www.cnblogs.com/dorothychai/p/3430451.html
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		MessageDialog.openInformation(window.getShell(), "GsonFormat Plug-in", "Hello, Eclipse world");

		String path = getProjectPath();
		System.out.println("SampleAction.run(0) " + path);
		IProject myProject = getCurrentProject();
		if (myProject != null) {
			path = path + myProject.getFullPath();
		}
		System.out.println("SampleAction.run(1) " + path);
	}

	public static String getProjectPath() {
		String path = null;
		path = Platform.getLocation().toString();
		return path;
	}

	/**
	 * IWorkbenchWindow window =
	 * PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	 * 
	 * IFile file = project.getProject().getFile(“/src/A.java”);
	 * 
	 * IDE.openEditor(window.getActivePage(), file);
	 * 
	 * @return [参数说明]
	 * @return IProject [返回类型说明]
	 * @exception throws
	 *                [违例类型] [违例说明]
	 * @see [类、类#方法、类#成员]
	 */
	public static IProject getCurrentProject() {
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();
		IProject project = null;
		if (selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			System.out.println("SampleAction.getCurrentProject() element is null+ " + (element == null));
			if (element == null) {
				// IViewPart viewPart =
				// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				// .findView("org.eclipse.ui.navigator.ProjectExplorer");
				// IStructuredSelection sl = (IStructuredSelection)
				// viewPart.getSite().getSelectionProvider().getSelection();
				// element = sl.getFirstElement();
				IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				System.out.println("SampleAction.getCurrentProject() element is part+ " + (part == null));
			} else {
				System.out.println("SampleAction.getCurrentProject()" + element.getClass());
			}

			if (element instanceof IResource) {
				project = ((IResource) element).getProject();
			} else if (element instanceof IJavaElement) {
				IJavaProject jProject = ((IJavaElement) element).getJavaProject();
				project = jProject.getProject();
			}

		} else if (selection instanceof ITextSelection) {
			// 1.根据当前编辑器获取工程
			IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (part != null) {
				Object object = part.getEditorInput().getAdapter(IFile.class);
				if (object != null) {
					project = ((IFile) object).getProject();
				}
			}
		} else {
			String title = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart()
					.getTitle();
			System.out.println("SampleAction.getCurrentProject()" + title);
		}
		return project;
	}

	public void aaa() {

	}

	private void createJavaElementsFrom(IProject myProject, IFolder myFolder, IFile myFile) {
		IJavaProject myJavaProject = JavaCore.create(myProject);
		if (myJavaProject == null)
			// the project is not configured for Java (has no Java nature)
			return;

		// get a package fragment or package fragment root
		IJavaElement myPackageFragment = JavaCore.create(myFolder);

		// get a .java (compilation unit), .class (class file), or
		// .jar (package fragment root)
		IJavaElement myJavaFile = JavaCore.create(myFile);
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}