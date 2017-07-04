package org.jdesktop.swingx.ux;

import java.util.List;
import java.util.Set;

import org.json.JSONObject;

public interface IJava extends Selector, CellProvider {

	boolean addImport(String importStr);

	Set<String> getImport();

	boolean isGenerate();

	void setGenerate(boolean generate);

	String getExtra();

	void setExtra(String extra);

	void addAllFields(List<IField> fieldEntitys);

	void addField(IField fieldEntity);

	List<IField> getFields();

	void addInnerClass(IJava classEntity);

	void addInnerClasss(List<IJava> classEntitys);

	List<IJava> getInnerClasss();

	String getPackName();

	void setPackName(String packName);

	String getFieldTypeSuffix();

	void setFieldTypeSuffix(String fieldTypeSuffix);

	String getClassName();

	void setClassName(String className);

	String getQualifiedName();

	boolean isSame(JSONObject o);

	boolean isLock();

	void setLock(boolean lock);
}
