package org.gsonformat.plugin.entiry;

import org.eclipse.jdt.core.ICompilationUnit;

public class GlobalInfo {
	public String packageName;
	public String generateClassName;
	public ICompilationUnit workingCopy;
	public boolean modify;
	public GlobalInfo(String packageName, String generateClassName, ICompilationUnit workingCopy, boolean modify) {
		this.packageName = packageName;
		this.generateClassName = generateClassName;
		this.workingCopy = workingCopy;
		this.modify = modify;
	}
	

}
