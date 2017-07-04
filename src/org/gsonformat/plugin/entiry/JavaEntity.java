package org.gsonformat.plugin.entiry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.gsonformat.plugin.config.Config;
import org.gsonformat.plugin.utils.CheckUtil;
import org.gsonformat.plugin.utils.TextUtils;
import org.jdesktop.swingx.ux.IField;
import org.jdesktop.swingx.ux.IJava;
import org.json.JSONObject;

public class JavaEntity implements IJava {
	private String fieldTypeSuffix;
	private String className;
	private String packName;
	private Set<String> imports = new HashSet<String>();
	private List<IField> fields = new ArrayList<IField>();
	private List<IJava> innerClasss = new ArrayList<IJava>();
	/** 存储 comment */
	private String extra;
	private boolean generate = true;
	private IType iType;

	public IType getiType() {
		return iType;
	}

	public void setiType(IType iType) {
		this.iType = iType;
	}

	public boolean isGenerate() {
		return generate;
	}

	public void setGenerate(boolean generate) {
		this.generate = generate;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public void addAllFields(List<IField> fieldEntitys) {
		this.fields.addAll(fieldEntitys);
	}

	public void addField(IField fieldEntity) {
		this.fields.add(fieldEntity);
	}

	public void addInnerClass(IJava classEntity) {
		this.innerClasss.add(classEntity);
	}

	@Override
	public void addInnerClasss(List<IJava> classEntitys) {
		this.innerClasss.addAll(classEntitys);
	}

	public List<IJava> getInnerClasss() {
		return innerClasss;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public String getFieldTypeSuffix() {
		return fieldTypeSuffix;
	}

	public void setFieldTypeSuffix(String fieldTypeSuffix) {
		this.fieldTypeSuffix = fieldTypeSuffix;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = CheckUtil.getInstant().handleArg(className);
	}

	public List<IField> getFields() {
		return fields;
	}

	/**
	 * 如果添加的包名不在集合中返回true
	 * 
	 * @see HashSet#add(Object)
	 */
	@Override
	public boolean addImport(String importStr) {
		return imports.add(importStr);
	}

	public boolean addImport(Set<String> imports) {
		return this.imports.addAll(imports);
	}

	@Override
	public Set<String> getImport() {
		return imports;
	}

	public String getQualifiedName() {
		String fullClassName;
		if (!TextUtils.isEmpty(packName)) {
			fullClassName = packName + "." + className;
		} else {
			fullClassName = className;
		}
		return fullClassName;
	}

	public boolean isSame(JSONObject o) {
		if (o == null) {
			return false;
		}
		boolean same = true;
		for (String key : o.keySet()) {
			same = false;
			for (IField field : fields) {
				if (field.getKey().equals(key)) {
					if (field.isSameType(o.get(key))) {
						same = true;
					}
					break;
				}
			}
			if (!same) {
				break;
			}
		}
		return same;
	}

	@Override
	public void setSelect(boolean select) {
		setGenerate(select);
	}

	@Override
	public String getCellTitle(int index) {
		String result = "";
		switch (index) {
		case 0:
			result = getClassName();
			break;
		case 3:
			result = getClassName();
			break;
		}
		return result;
	}

	@Override
	public void setValueAt(int column, String text) {
		switch (column) {
		case 2:
			break;
		case 3:
			String result;
			if (!TextUtils.isEmpty(fieldTypeSuffix)) {
				result = fieldTypeSuffix + "." + text;
			} else {
				result = text;
			}
			if (CheckUtil.getInstant().containsDeclareClassName(result)) {
				return;
			}
			CheckUtil.getInstant().removeDeclareClassName(getQualifiedName());
			setClassName(text);
			break;
		}
	}

	@Override
	public boolean isLock() {
		return false;
	}

	@Override
	public void setLock(boolean lock) {
		// TODO
	}

	public void mergeImport() {
		for (IJava iJava : innerClasss) {
			addImport(iJava.getImport());
		}
	}

	public String toSrc() {
		return toSrc("");
	}

	public String toSrc(String tab) {
		StringBuilder sb = new StringBuilder();
		if (!TextUtils.isEmpty(getExtra())) {
			sb.append("\n").append(getExtra()).append("\n");
		}
		for (IField field : getFields()) {
			if (!field.isGenerate()) {
				continue;
			}
			JavaFieldEntity javaField = (JavaFieldEntity) field;
			sb.append(javaField.createField(tab));
		}
		if (Config.getInstant().isFieldPrivateMode()) {
			for (IField field : getFields()) {
				if (!field.isGenerate()) {
					continue;
				}
				JavaFieldEntity javaField = (JavaFieldEntity) field;
				sb.append(javaField.createSetAndGetMethod(tab));
			}
		}
		return sb.toString();
	}
}
