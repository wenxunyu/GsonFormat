package org.gsonformat.plugin.entiry;

import org.gsonformat.plugin.config.Config;
import org.gsonformat.plugin.config.Constant;
import org.gsonformat.plugin.utils.CheckUtil;
import org.gsonformat.plugin.utils.DataType;
import org.gsonformat.plugin.utils.TextUtils;
import org.gsonformat.plugin.utils.Tools;
import org.jdesktop.swingx.ux.IField;
import org.jdesktop.swingx.ux.IJava;
import org.json.JSONObject;

/**
 * Created by dim on 2015/7/15.
 */
public class JavaFieldEntity implements IField {

	protected String key;
	protected String type; // 类型
	protected String fieldName; // 生成的名字
	protected String value; // 值
	protected IJava targetClass; // 依赖的实体类
	protected boolean generate = true;

	public String createField() {
		return createField("");
	}

	public String createField(String tab) {
		StringBuilder sb = new StringBuilder();
		if (Config.getInstant().isUseSerializedName() || !fieldName.equals(getKey())) {
			sb.append(tab).append(Constant.gsonFullNameAnnotation.replaceAll("\\{filed\\}", getKey()))
					.append(Constant.NEXT_LINE);
		}
		sb.append(tab);
		if (Config.getInstant().isFieldPrivateMode()) {
			sb.append("private");
		} else {
			sb.append("public");
		}
		sb.append(Constant.SPACE).append(targetType()).append(Constant.SPACE);
		sb.append(fieldName).append(Constant.END);
		return sb.toString();
	}

	protected String targetType() {
		return getRealType();
	}

	public String createSetAndGetMethod() {
		return createSetAndGetMethod("");
	}

	public String createSetAndGetMethod(String tab) {
		Config cf = Config.getInstant();
		String typeStr = targetType();
		String _fieldName = getGenerateFieldName();
		String parameter = _fieldName;

		if (cf.isUseSerializedName() && cf.isUseFieldNamePrefix() && !TextUtils.isEmpty(cf.getFiledNamePreFixStr())) {
			String temp = _fieldName.replaceFirst("^" + cf.getFiledNamePreFixStr(), "");
			if (!TextUtils.isEmpty(temp)) {
				_fieldName = temp;
				parameter = Tools.captureFirstToLowerCase(temp);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(tab).append("public").append(" void ").append("set").append(Tools.captureName(_fieldName))
				.append("(" + typeStr + " " + parameter + "){\n");
		sb.append(tab).append("\t").append("this." + getGenerateFieldName() + " = " + parameter + ";\n");
		sb.append(tab).append("}\n\n");

		if ("boolean".equals(typeStr) || "Boolean".equals(typeStr)) {
			sb.append(tab).append("public ").append(targetType()).append(" is").append(Tools.captureName(_fieldName))
					.append("(){\n");
			sb.append(tab).append("\t").append("return " + getGenerateFieldName() + ";\n");
			sb.append(tab).append("}\n\n");
		} else {
			sb.append(tab).append("public ").append(targetType()).append(" get").append(Tools.captureName(_fieldName))
					.append("(){\n");
			sb.append(tab).append("\t").append("return " + getGenerateFieldName() + ";\n");
			sb.append(tab).append("}\n\n");
		}

		return sb.toString();
	}

	public IJava getTargetClass() {
		return targetClass;
	}

	public void setTargetClass(IJava targetClass) {
		this.targetClass = targetClass;
	}

	public boolean isGenerate() {
		return generate;
	}

	public void setGenerate(boolean generate) {
		this.generate = generate;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getGenerateFieldName() {
		return CheckUtil.getInstant().handleArg(fieldName);
	}

	public void setFieldName(String fieldName) {
		if (TextUtils.isEmpty(fieldName)) {
			return;
		}
		this.fieldName = fieldName;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public String getRealType() {
		if (targetClass != null) {
			return targetClass.getClassName();
		}
		return type;
	}

	public String getBriefType() {
		if (targetClass != null) {
			return targetClass.getClassName();
		}
		int i = type.indexOf(".");
		if (i > 0) {
			return type.substring(i);
		}
		return type;
	}

	public String getFullNameType() {
		if (targetClass != null) {
			return targetClass.getQualifiedName();
		}
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void checkAndSetType(String text) {
		if (type != null && CheckUtil.getInstant().checkSimpleType(type.trim())) {
			// 基本类型
			if (CheckUtil.getInstant().checkSimpleType(text.trim())) {
				this.type = text.trim();
			}
		} else {
			// 实体类:
			if (targetClass != null && !targetClass.isLock()) {
				if (!TextUtils.isEmpty(text)) {
					targetClass.setClassName(text);
				}
			}
		}
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	@Override
	public void setSelect(boolean select) {
		setGenerate(select);
	}

	public boolean isSameType(Object o) {
		if (o instanceof JSONObject) {
			if (targetClass != null) {
				return targetClass.isSame((JSONObject) o);
			}
		} else {
			return DataType.isSameDataType(DataType.typeOfString(type), DataType.typeOfObject(o));
		}
		return false;
	}

	@Override
	public String getCellTitle(int index) {
		String result = "";
		switch (index) {
		case 0:
			result = getKey();
			break;
		case 1:
			result = getValue();
			break;
		case 2:
			result = getBriefType();
			break;
		case 3:
			result = getFieldName();
			break;
		}
		return result;
	}

	@Override
	public void setValueAt(int column, String text) {
		switch (column) {
		case 2:
			checkAndSetType(text);
			break;
		case 3:
			if (CheckUtil.getInstant().containsDeclareFieldName(text)) {
				return;
			}
			CheckUtil.getInstant().removeDeclareFieldName(getFieldName());
			setFieldName(text);
			break;
		}
	}
}
