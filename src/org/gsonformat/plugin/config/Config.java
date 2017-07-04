package org.gsonformat.plugin.config;

/**
 * Created by dim on 15/5/31.
 */
public class Config {

	private static Config config;

	private boolean fieldPrivateMode = true;
	private boolean generateComments = true;
	private boolean useSerializedName = false;
	private boolean objectFromData = false;
	private boolean objectFromData1 = false;
	private boolean arrayFromData = false;
	private boolean arrayFromData1 = false;
	private boolean reuseEntity = true;
	private boolean virgoMode = true; // 处女座模式
	private boolean useFieldNamePrefix = false;// 只有序列化为true的时候才有前缀
	private boolean splitGenerate = false;

	private boolean copyright = true;// 版权声明

	private String objectFromDataStr;
	private String objectFromDataStr1;
	private String arrayFromDataStr;
	private String arrayFromData1Str;
	private String annotationStr = Constant.gsonAnnotation; // 注解语句
	private String filedNamePreFixStr = "m"; // 字段前缀
	private String entityPackName;// 创建实体类的包名.
	private String suffixStr = "Ben";// 实体后缀

	private String copyrightStr = "/** Copyright GsonToJavaSource,  All rights reserved */\n";
	private String className = "GsonToJava";// 默认类名
	private String packageName = "wen.xun.yu";// 默认包名
	private String classComment = "/**\n* @author wenxunyu \n* @version [0.1.0]\n*/";
	public static String annotationSimpleName = "Gson";

	public boolean isCopyright() {
		return copyright;
	}

	public void setCopyright(boolean copyright) {
		this.copyright = copyright;
	}

	public String getCopyrightStr() {
		return copyrightStr;
	}

	public void setCopyrightStr(String copyrightStr) {
		this.copyrightStr = copyrightStr;
	}

	public String getClassComment() {
		return classComment;
	}

	public void setClassComment(String classComment) {
		this.classComment = classComment;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	private Config() {

	}

	public void save() {

	}

	public static Config getInstant() {
		if (config == null) {
			config = new Config();
		}
		return config;
	}

	public boolean isUseFieldNamePrefix() {
		return useFieldNamePrefix;
	}

	public void setUseFieldNamePrefix(boolean useFieldNamePrefix) {
		this.useFieldNamePrefix = useFieldNamePrefix;
	}

	public boolean isObjectFromData() {
		return objectFromData;
	}

	public String getEntityPackName() {
		return entityPackName;
	}

	public String geFullNameAnnotation() {

		if (annotationStr.equals(Constant.gsonAnnotation)) {
			return Constant.gsonFullNameAnnotation;
		}
		if (annotationStr.equals(Constant.jackAnnotation)) {
			return Constant.jackFullNameAnnotation;
		}
		if (annotationStr.equals(Constant.fastAnnotation)) {
			return Constant.fastFullNameAnnotation;
		}
		if (annotationStr.equals(Constant.loganSquareAnnotation)) {
			return Constant.loganSquareFullNameAnnotation;
		}
		return annotationStr.replaceAll("\\(", "(").replaceAll("\\)", ")").replaceAll("\\s\\*", "");
	}

	public boolean isGenerateComments() {
		return generateComments;
	}

	public void setGenerateComments(boolean generateComments) {
		this.generateComments = generateComments;
	}

	public void setEntityPackName(String entityPackName) {
		this.entityPackName = entityPackName;
	}

	public boolean isVirgoMode() {
		return virgoMode;
	}

	public void setVirgoMode(boolean virgoMode) {
		this.virgoMode = virgoMode;
	}

	public String getFiledNamePreFixStr() {
		return filedNamePreFixStr;
	}

	public void setFiledNamePreFixStr(String filedNamePreFixStr) {
		this.filedNamePreFixStr = filedNamePreFixStr;
	}

	public String getAnnotationStr() {
		return annotationStr;
	}

	public void setAnnotationStr(String annotationStr) {
		this.annotationStr = annotationStr;
	}

	public void setObjectFromData(boolean objectFromData) {
		this.objectFromData = objectFromData;
	}

	public boolean isObjectFromData1() {
		return objectFromData1;
	}

	public void setObjectFromData1(boolean objectFromData2) {
		this.objectFromData1 = objectFromData2;
	}

	public boolean isArrayFromData() {
		return arrayFromData;
	}

	public void setArrayFromData(boolean arrayFromData) {
		this.arrayFromData = arrayFromData;
	}

	public boolean isArrayFromData1() {
		return arrayFromData1;
	}

	public void setArrayFromData1(boolean arrayFromData1) {
		this.arrayFromData1 = arrayFromData1;
	}

	public void setObjectFromDataStr(String objectFromDataStr) {
		this.objectFromDataStr = objectFromDataStr;
	}

	public void setObjectFromDataStr1(String objectFromDataStr1) {
		this.objectFromDataStr1 = objectFromDataStr1;
	}

	public void setArrayFromDataStr(String arrayFromDataStr) {
		this.arrayFromDataStr = arrayFromDataStr;
	}

	public void setArrayFromData1Str(String arrayFromData1Str) {
		this.arrayFromData1Str = arrayFromData1Str;
	}

	public String getObjectFromDataStr() {
		return objectFromDataStr;
	}

	public String getObjectFromDataStr1() {
		return objectFromDataStr1;
	}

	public String getArrayFromDataStr() {
		return arrayFromDataStr;
	}

	public String getArrayFromData1Str() {
		return arrayFromData1Str;
	}

	public String getSuffixStr() {
		return suffixStr;
	}

	public void setSuffixStr(String suffixStr) {
		this.suffixStr = suffixStr;
	}

	public boolean isReuseEntity() {
		return reuseEntity;
	}

	public void setReuseEntity(boolean reuseEntity) {
		this.reuseEntity = reuseEntity;
	}

	public boolean isUseSerializedName() {
		return useSerializedName;
	}

	public void setUseSerializedName(boolean useSerializedName) {
		this.useSerializedName = useSerializedName;
	}

	public boolean isFieldPrivateMode() {
		return fieldPrivateMode;
	}

	public void setFieldPrivateMode(boolean fieldPrivateMode) {
		this.fieldPrivateMode = fieldPrivateMode;
	}

	public void saveObjectFromDataStr(String objectFromDataStr) {
		this.objectFromDataStr = objectFromDataStr;
	}

	public void saveObjectFromDataStr1(String objectFromDataStr1) {
		this.objectFromDataStr1 = objectFromDataStr1;
	}

	public void saveArrayFromDataStr(String arrayFromDataStr) {
		this.arrayFromDataStr = arrayFromDataStr;
	}

	public void saveArrayFromData1Str(String arrayFromData1Str) {
		this.arrayFromData1Str = arrayFromData1Str;
	}

	public boolean isSplitGenerate() {
		return splitGenerate;
	}

	public void setSplitGenerate(boolean splitGenerate) {
		this.splitGenerate = splitGenerate;
	}

}
