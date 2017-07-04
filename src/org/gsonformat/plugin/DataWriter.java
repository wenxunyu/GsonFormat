package org.gsonformat.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.gsonformat.plugin.config.Config;
import org.gsonformat.plugin.entiry.GlobalInfo;
import org.gsonformat.plugin.entiry.JavaEntity;
import org.gsonformat.plugin.entiry.JavaFieldEntity;
import org.gsonformat.plugin.utils.TextUtils;
import org.gsonformat.plugin.utils.Tools;
import org.jdesktop.swingx.ux.IField;
import org.jdesktop.swingx.ux.IJava;

public class DataWriter {
	private JavaEntity javaEntity;
	private GlobalInfo info;

	public DataWriter(JavaEntity javaEntity, GlobalInfo info) {
		super();
		this.javaEntity = javaEntity;
		this.info = info;
	}

	public void execute() {
		createJavaFile(javaEntity);
	}

	private void createJavaFile(JavaEntity java) {

		IType mainType = Tools.getMainType(info.workingCopy);
		try {
			int index = 0;
			for (IField field : java.getFields()) {// 创建字段
				if (!field.isGenerate()) {
					continue;
				}
				JavaFieldEntity javaField = (JavaFieldEntity) field;
				if (index == 0 && !TextUtils.isEmpty(java.getExtra())) {
					StringBuilder sb = new StringBuilder();
					sb.append(java.getExtra()).append("\n");
					sb.append(javaField.createField());
					mainType.createField(sb.toString(), null, true, null);
				} else {
					mainType.createField(javaField.createField(), null, true, null);
				}
				index++;
			}

			if (Config.getInstant().isFieldPrivateMode()) {// 创建SetAndGet方法
				for (IField field : java.getFields()) {
					if (!field.isGenerate()) {
						continue;
					}
					JavaFieldEntity javaField = (JavaFieldEntity) field;
					mainType.createMethod(javaField.createSetAndGetMethod(), null, true, null);
				}
			}
			boolean split = Config.getInstant().isSplitGenerate();
			if (split) {// 拆分成多个文件
				createLikedJava(mainType.getPackageFragment(), javaEntity);
			} else {// 单一文件
				if (javaEntity.getInnerClasss().isEmpty()) {
					return;
				}
				mainType.createType(innerClass(javaEntity, "\t").toString(), null, true, null);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		} finally {
			try {
				info.workingCopy.commitWorkingCopy(false, null);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			try {
				info.workingCopy.discardWorkingCopy();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see http://blog.csdn.net/zyf814/article/details/8443177
	 */
	private void createLikedJava(final IPackageFragment ipf, final JavaEntity javaEntity) {
		try {
			JavaCore.run(new IWorkspaceRunnable() {
				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
					Map<String, StringBuilder> maps = createJava(javaEntity, null);
					final List<IJavaElement> javaElements = new ArrayList<IJavaElement>(maps.size());
					for (Entry<String, StringBuilder> map : maps.entrySet()) {
						try {
							ICompilationUnit unit = ipf.createCompilationUnit(map.getKey() + ".java",
									map.getValue().toString(), false, null);
							unit.createPackageDeclaration(ipf.getElementName(), null);
							javaElements.add(unit);
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							for (IJavaElement iJavaElement : javaElements) {
								try {
									JavaUI.openInEditor(iJavaElement);
								} catch (PartInitException e) {
									e.printStackTrace();
								} catch (JavaModelException e) {
									e.printStackTrace();
								}
							}
						}
					});

				}
			}, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private StringBuilder innerClass(JavaEntity classEntity, String tab) {
		StringBuilder sb = new StringBuilder();
		String newtab = tab;
		for (IJava javaClass : classEntity.getInnerClasss()) {
			if (!javaClass.isGenerate()) {
				continue;
			}
			JavaEntity javaEntity = (JavaEntity) javaClass;
			sb.append(tab).append("public static class ").append(javaEntity.getClassName()).append(" {\n");
			sb.append(javaEntity.toSrc(newtab + "\t"));
			sb.append(tab).append("}\n\n");
			sb.append(innerClass(javaEntity, newtab));
		}
		return sb;
	}

	private Map<String, StringBuilder> createJava(JavaEntity javaEntity, Map<String, StringBuilder> map) {
		if (map == null) {
			map = new HashMap<String, StringBuilder>();
		}
		for (IJava ijava : javaEntity.getInnerClasss()) {
			if (!ijava.isGenerate()) {
				continue;
			}
			JavaEntity childEntity = (JavaEntity) ijava;
			StringBuilder sb = new StringBuilder();
			sb.append("public class ").append(childEntity.getClassName()).append(" {\n");
			sb.append(javaEntity.toSrc("\t"));
			sb.append("}\n\n");
			map.put(childEntity.getClassName(), sb);
			createJava(childEntity, map);
		}
		return map;

	}

	/**
	 * 如果包不存在的话就创建
	 * 
	 * <pre>
	 * if (monitor == null) {
	 * 	monitor = new NullProgressMonitor();
	 * }
	 * 
	 * IPackageFragmentRoot packageFragmentRoot = null;
	 * 
	 * try {
	 * 	for (IPackageFragmentRoot root : project.getPackageFragmentRoots()) {
	 * 		if (root.getKind() == 1) {
	 * 			packageFragmentRoot = root;
	 * 			break;
	 * 		}
	 * 	}
	 * } catch (JavaModelException e) {
	 * 	Logger.error("查找PackageFragmentRoot错误！", e);
	 * }
	 * 
	 * mkPackages(packageFragmentRoot, servicePackage, monitor);
	 * 
	 * </pre>
	 * 
	 * @param root
	 * @param packageFragment
	 * @param monitor
	 * @see http://surenpi.com/2016/03/04/eclipse_code_generator/
	 */
	private void mkPackages(IPackageFragmentRoot root, String packageFragment, IProgressMonitor monitor) {
		if (root == null) {
			return;
		}
		monitor.subTask("make sure the package is exists! package : " + packageFragment);
		IPackageFragment pk = root.getPackageFragment(packageFragment);
		if (pk == null || !pk.exists()) {
			try {
				pk = root.createPackageFragment(packageFragment, true, monitor);
				pk.save(monitor, true);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	public static ICompilationUnit createCompilationUnit(IPackageFragment packageFragment, String type, String typeName,
			String superCls, String... superInters) {
		try {
			ICompilationUnit unit = packageFragment.createCompilationUnit(typeName + ".java", "", false, null);
			unit.becomeWorkingCopy(null);
			IBuffer buffer = unit.getBuffer();

			String simpleTypeStub = constructSimpleTypeStub(type, typeName, superCls, superInters);
			String lineDelimiter = System.getProperty("line.separator", "\n");
			String cuContent = constructCUContent(unit, simpleTypeStub, lineDelimiter);
			buffer.setContents(cuContent);
			return unit;
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String constructSimpleTypeStub(String type, String typeName, String superCls, String... superInters) {
		StringBuffer buf = new StringBuffer("public ");
		buf.append(type);
		buf.append(" ");
		buf.append(typeName);
		if (!TextUtils.isEmpty(superCls)) {
			buf.append(" extends ");
			buf.append(superCls);
		}
		if (superInters != null && superInters.length > 0) {
			buf.append(" implements");
			for (String superInter : superInters) {
				buf.append(" ");
				buf.append(superInter);
			}
		}
		buf.append("{\n}");
		return buf.toString();
	}

	public static String constructCUContent(ICompilationUnit cu, String typeContent, String lineDelimiter)
			throws CoreException {
		String fileComment = "";// getFileComment(cu, lineDelimiter);
		String typeComment = "";// getTypeComment(cu, lineDelimiter);
		IPackageFragment pack = (IPackageFragment) cu.getParent();
		String content = CodeGeneration.getCompilationUnitContent(cu, fileComment, typeComment, typeContent,
				lineDelimiter);
		if (content != null) {
			ASTParser parser = ASTParser.newParser(8);
			parser.setProject(cu.getJavaProject());
			parser.setSource(content.toCharArray());
			CompilationUnit unit = (CompilationUnit) parser.createAST(null);
			if (((pack.isDefaultPackage()) || (unit.getPackage() != null)) && (!unit.types().isEmpty())) {
				return content;
			}
		}
		StringBuffer buf = new StringBuffer();
		if (!pack.isDefaultPackage()) {
			buf.append("package ").append(pack.getElementName()).append(';');
		}
		buf.append(lineDelimiter).append(lineDelimiter);
		if (typeComment != null) {
			buf.append(typeComment).append(lineDelimiter);
		}
		buf.append(typeContent);
		return buf.toString();
	}

	/**
	 * 提交修改内容
	 * 
	 * @param cUnit
	 * @param force
	 * @param monitor
	 * @throws JavaModelException
	 */
	public static void commitCompilationUnit(ICompilationUnit cUnit, boolean force, IProgressMonitor monitor)
			throws JavaModelException {
		cUnit.reconcile(0, false, null, monitor);
		cUnit.commitWorkingCopy(force, monitor);
	}
}
