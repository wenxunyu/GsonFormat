package org.jdesktop.swingx.ux;

public interface IField extends Selector, CellProvider {

	public IJava getTargetClass();

	public void setTargetClass(IJava targetClass);

	public boolean isGenerate();

	public void setGenerate(boolean generate);

	public String getFieldName();

	public String getGenerateFieldName();

	public void setFieldName(String fieldName);

	public void setValue(String value);

	public void setKey(String key);

	public String getType();

	public String getRealType();

	public String getBriefType();

	public String getFullNameType();

	public void setType(String type);

	public void checkAndSetType(String text);

	public String getKey();

	public String getValue();

	public boolean isSameType(Object o);

}
