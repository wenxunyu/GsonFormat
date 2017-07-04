/*文 件 名:  ConvertBridge.java
 * 修 改 人:  wenxunyu@126.com
 * 修改时间:  2017年6月24日
 */
package org.gsonformat.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.jface.dialogs.Dialog;
import org.gsonformat.plugin.config.Config;
import org.gsonformat.plugin.entiry.GlobalInfo;
import org.gsonformat.plugin.entiry.IterableFieldEntity;
import org.gsonformat.plugin.entiry.JavaEntity;
import org.gsonformat.plugin.entiry.JavaFieldEntity;
import org.gsonformat.plugin.ui.FieldsEditDialog;
import org.gsonformat.plugin.utils.CheckUtil;
import org.gsonformat.plugin.utils.DataType;
import org.gsonformat.plugin.utils.TextUtils;
import org.gsonformat.plugin.utils.Tools;
import org.jdesktop.swingx.ux.IField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 把 json 转成 实体类
 * 
 * @author wenxunyu@126.com
 * @version [2017年6月24日]
 * @see https://github.com/zzz40500/GsonFormat
 * @see http://max.book118.com/html/2017/0213/90939698.shtm
 * @see http://alvinalexander.com/java/jwarehouse/eclipse/org.eclipse.jdt.core.tests.model/src/org/eclipse/jdt/core/tests/model/CreateMembersTests.java.shtml
 * @since JDK7.0
 */
public class ConvertBridge {
	private HashMap<String, JavaEntity> declareClass;
	private HashMap<String, IField> declareFields;
	private Dialog ejDialog;
	private GlobalInfo info;
	private String jsonStr;
	private JavaEntity generateClassEntity;

	private StringBuilder fullFilterRegex = null;
	private StringBuilder briefFilterRegex = null;
	private String filterRegex = null;

	public ConvertBridge(Dialog dialog, ICompilationUnit workingCopy, String packageName, String generateClassName,
			boolean modify, String jsonStr) {
		this.ejDialog = dialog;
		this.jsonStr = jsonStr;
		this.info = new GlobalInfo(packageName, generateClassName, workingCopy, modify);
		declareClass = new HashMap<String, JavaEntity>();
		declareFields = new HashMap<String, IField>();
		generateClassEntity = new JavaEntity();
		generateClassEntity.setClassName(generateClassName);
		generateClassEntity.setPackName(packageName);

		fullFilterRegex = new StringBuilder();
		briefFilterRegex = new StringBuilder();

		CheckUtil.getInstant().cleanDeclareData();
		String[] arg = Config.getInstant().getAnnotationStr().replace("{filed}", "(\\w+)").split("\\.");

		for (int i = 0; i < arg.length; i++) {
			String s = arg[i];
			if (i == arg.length - 1) {
				briefFilterRegex.append(s);
				fullFilterRegex.append(s);
				Matcher matcher = Pattern.compile("\\w+").matcher(s);
				if (matcher.find()) {
					filterRegex = matcher.group();
				}
			} else {
				fullFilterRegex.append(s).append("\\s*\\.\\s*");
			}
		}
	}

	public void run() {
		JSONObject json = null;
		try {
			json = parseJSONObject(jsonStr);
		} catch (Exception e) {
			String jsonTS = removeComment(jsonStr);
			jsonTS = jsonTS.replaceAll("^.*?\\{", "{");
			try {
				json = parseJSONObject(jsonTS);
			} catch (Exception exception) {
				Tools.tip(ejDialog.getShell(), "Json格式有误", exception.getMessage());
				return;
			}
		}
		if (json != null) {
			// 1.获得当前类实体中的全部类
			JavaEntity classEntity = collectClassAttribute(info.workingCopy, Config.getInstant().isReuseEntity());
			// 2.取出字段，防止重复
			if (classEntity != null) {
				for (IField item : classEntity.getFields()) {
					declareFields.put(item.getKey(), item);
					CheckUtil.getInstant().addDeclareFieldName(item.getKey());
				}
			}
			// 3.如果拆分，收集包下面所有的类
			collectPackAllClassName();
			parseJson(json);
		} else {
			Tools.tip(ejDialog.getShell(), "Json格式有误", "请检查Json数据是否正确");
		}
	}

	private JavaEntity collectClassAttribute(ICompilationUnit workingCopy, boolean collectInnerClass) {
		if (workingCopy == null) {
			return null;
		}
		IType mainType = workingCopy.getType(Tools.getClassName(workingCopy.getElementName()));
		if (!mainType.exists()) {
			return null;
		}
		JavaEntity innerClass = new JavaEntity();
		innerClass.setLock(true);
		declareClass.put(mainType.getFullyQualifiedName(), innerClass);
		CheckUtil.getInstant().addDeclareClassName(mainType.getFullyQualifiedName());
		innerClass.setClassName(mainType.getElementName());
		innerClass.addAllFields(collectDeclareFields(mainType));
		innerClass.setiType(mainType);
		innerClass.setPackName(mainType.getPackageFragment().getElementName());
		if (collectInnerClass) {
			recursionInnerClass(innerClass);
		}
		return innerClass;
	}

	private void recursionInnerClass(JavaEntity innerClass) {
		IType parent = innerClass.getiType();
		if (parent == null) {
			return;
		}
		try {
			IType[] childs = parent.getTypes();
			for (IType iType : childs) {
				JavaEntity item = new JavaEntity();
				item.setLock(true);
				if (declareClass.containsKey(iType.getFullyQualifiedName())) {
					continue;
				}
				declareClass.put(iType.getFullyQualifiedName(), item);
				CheckUtil.getInstant().addDeclareClassName(iType.getFullyQualifiedName());
				item.setClassName(iType.getElementName());
				item.addAllFields(collectDeclareFields(iType));
				item.setiType(iType);
				item.setPackName(iType.getPackageFragment().getElementName());
				recursionInnerClass(item);
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
			// TODO if this element does not exist or if an exception occurs
			// while accessing its corresponding resource.
		}

	}

	private void collectPackAllClassName() {
		IType mainType = info.workingCopy.getType(Tools.getClassName(info.workingCopy.getElementName()));
		if (!mainType.exists()) {
			return;
		}
		IPackageFragment ipf = mainType.getPackageFragment();
		IJavaElement[] jes = null;
		try {
			jes = ipf.getChildren();
			for (IJavaElement i : jes) {
				CheckUtil.getInstant()
						.addDeclareClassName(ipf.getElementName() + "." + Tools.getClassName(i.getElementName()));
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	private List<IField> collectDeclareFields(IType iType) {
		ArrayList<IField> filterFieldList = new ArrayList<IField>();
		if (iType == null || !iType.exists()) {
			return filterFieldList;
		}

		try {
			CompilationUnit compilationUnit = getCompilationUnit(iType.getSource());
			List<TypeDeclaration> nodes = compilationUnit.types();
			TypeDeclaration clazzNode = nodes.get(0);
			FieldDeclaration[] fields = clazzNode.getFields();
			for (FieldDeclaration field : fields) {
				List<VariableDeclarationFragment> fragments = field.fragments();
				// 默认同一个类型只声明一个字段。public int a,b;这种情况只取a丢弃b。
				VariableDeclarationFragment vd = fragments.get(0);
				String fileName = vd.getName().toString();
				if (filterRegex != null) {
					List<IExtendedModifier> modifiers = field.modifiers();
					for (IExtendedModifier iem : modifiers) {
						if (iem.isAnnotation() && iem.toString().contains(filterRegex)) {
							String fieldAnnotation = iem.toString().trim();
							Pattern pattern = Pattern.compile(fullFilterRegex.toString());
							Matcher matcher = pattern.matcher(fieldAnnotation);
							if (matcher.find()) {
								fileName = matcher.group(1);
							}
							pattern = Pattern.compile(briefFilterRegex.toString());
							matcher = pattern.matcher(fieldAnnotation);
							if (matcher.find()) {
								fileName = matcher.group(1);
							}
						}
					}
				}
				JavaFieldEntity fieldEntity = evalFieldEntity(null, field.getType());
				fieldEntity.setKey(fileName);
				fieldEntity.setFieldName(fileName);
				filterFieldList.add(fieldEntity);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filterFieldList;
	}

	/**
	 * @see http://max.book118.com/html/2017/0213/90939698.shtm
	 */
	private JavaFieldEntity evalFieldEntity(JavaFieldEntity fieldEntity, Type type) {
		if (type.isPrimitiveType()) {// java基本数据类型
			if (fieldEntity == null) {
				fieldEntity = new JavaFieldEntity();
			}
			PrimitiveType p = (PrimitiveType) type;
			fieldEntity.setType(p.getPrimitiveTypeCode().toString());
		} else if (type.isArrayType()) {// 数组：此处把数组当成集合处理
			if (fieldEntity == null) {
				fieldEntity = new IterableFieldEntity();
			}
			ArrayType a = (ArrayType) type;
			IterableFieldEntity iterableFieldEntity = (IterableFieldEntity) fieldEntity;
			iterableFieldEntity.setDeep(iterableFieldEntity.getDeep() + 1);
			// TODO 此处需要要验证
			return evalFieldEntity(fieldEntity, a.getElementType());
		} else if (type.isSimpleType()) {// 对象
			if (fieldEntity == null) {
				fieldEntity = new JavaFieldEntity();
			}
			SimpleType s = (SimpleType) type;
			fieldEntity.setType(s.getName().getFullyQualifiedName());
		} else if (type.isParameterizedType()) {
			if (fieldEntity == null) {
				fieldEntity = new IterableFieldEntity();
			}
			ParameterizedType p = (ParameterizedType) type;
			IterableFieldEntity iterableFieldEntity = (IterableFieldEntity) fieldEntity;
			iterableFieldEntity.setDeep(iterableFieldEntity.getDeep() + 1);
			List<Type> parameters = p.typeArguments();
			if (parameters.size() > 0) {
				Type parameter = parameters.get(0);
				if (parameter.isWildcardType()) {
					WildcardType wtype = (WildcardType) parameter;
					Type bound = wtype.getBound();
					evalFieldEntity(fieldEntity, bound);
				} else {
					fieldEntity.setType(parameter.toString());
				}
			}
			return fieldEntity;
		}
		if (fieldEntity == null) {
			fieldEntity = new IterableFieldEntity();
		}
		return fieldEntity;
	}

	private CompilationUnit getCompilationUnit(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(source.toCharArray());
		CompilationUnit node = (CompilationUnit) parser.createAST(null);
		return node;
	}

	private void parseJson(JSONObject json) {
		List<String> generateFiled = collectGenerateFiled(json);
		if (Config.getInstant().isVirgoMode()) {
			handleVirgoMode(json, generateFiled, generateClassEntity);
		} else {
			handleNormal(json, generateFiled, generateClassEntity);
		}
		CheckUtil.getInstant().cleanDeclareData();
	}

	// TODO 这里耗时
	private void handleVirgoMode(JSONObject json, List<String> generateFiled, JavaEntity parentClass) {
		generateClassEntity.addAllFields(createFields(json, generateFiled, parentClass, "\t"));
		FieldsEditDialog fieldsEditDialog = new FieldsEditDialog(generateClassEntity, ejDialog, info);
		fieldsEditDialog.setVisible(true);
	}

	private void handleNormal(JSONObject json, List<String> generateFiled, JavaEntity parentClass) {
		generateClassEntity.addAllFields(createFields(json, generateFiled, parentClass, "\t"));
		new DataWriter(generateClassEntity, info).execute();
	}

	private List<IField> createFields(JSONObject json, List<String> fieldList, JavaEntity parentClass, String tab) {

		List<IField> fieldEntityList = new ArrayList<IField>();
		List<String> listEntityList = new ArrayList<String>();
		// 是否写入注释
		boolean writeExtra = Config.getInstant().isGenerateComments();

		for (int i = 0; i < fieldList.size(); i++) {
			String key = fieldList.get(i);
			Object value = json.get(key);
			if (value instanceof JSONArray) {
				listEntityList.add(key);
				continue;
			}
			JavaFieldEntity fieldEntity = createField(parentClass, key, value);
			fieldEntityList.add(fieldEntity);
			if (writeExtra) {
				writeExtra = false;
				parentClass.setExtra(Tools.createCommentString(json, fieldList, tab));
			}
		}

		for (int i = 0; i < listEntityList.size(); i++) {
			String key = listEntityList.get(i);
			Object type = json.get(key);
			JavaFieldEntity fieldEntity = createField(parentClass, key, type);
			fieldEntityList.add(fieldEntity);
		}

		return fieldEntityList;
	}

	private List<String> collectGenerateFiled(JSONObject json) {
		Set<String> keySet = json.keySet();
		List<String> fieldList = new ArrayList<String>();
		for (String key : keySet) {
			if (!existDeclareField(key, json)) {
				fieldList.add(key);
			}
		}
		return fieldList;
	}

	private JavaFieldEntity createField(JavaEntity parentClass, String key, Object type) {
		// 过滤 不符合规则的key
		String fieldName = CheckUtil.getInstant().handleArg(key);
		if (Config.getInstant().isUseSerializedName()) {
			fieldName = Tools.captureStringLeaveUnderscore(convertSerializedName(fieldName));
		}
		fieldName = handleDeclareFieldName(fieldName, "");

		JavaFieldEntity fieldEntity = typeByValue(parentClass, key, type);
		fieldEntity.setFieldName(fieldName);
		return fieldEntity;
	}

	private String convertSerializedName(String fieldName) {
		if (Config.getInstant().isUseFieldNamePrefix()
				&& !TextUtils.isEmpty(Config.getInstant().getFiledNamePreFixStr())) {
			fieldName = Config.getInstant().getFiledNamePreFixStr() + "_" + fieldName;
		}
		return fieldName;
	}

	private JavaFieldEntity typeByValue(JavaEntity parentClass, String key, Object type) {
		JavaFieldEntity result;
		if (type instanceof JSONObject) {
			JavaEntity classEntity = existDeclareClass((JSONObject) type);
			if (classEntity == null) {
				JavaFieldEntity fieldEntity = new JavaFieldEntity();
				String subClassName = createSubClassName(key, type);
				JavaEntity innerClassEntity = createInnerClass(subClassName, (JSONObject) type, parentClass);
				fieldEntity.setKey(key);
				fieldEntity.setType(innerClassEntity.getClassName());
				fieldEntity.setTargetClass(innerClassEntity);
				result = fieldEntity;
			} else {
				JavaFieldEntity fieldEntity = new JavaFieldEntity();
				fieldEntity.setKey(key);
				fieldEntity.setType(classEntity.getClassName());
				fieldEntity.setTargetClass(classEntity);
				result = fieldEntity;
			}
		} else if (type instanceof JSONArray) {
			result = handleJSONArray(parentClass, (JSONArray) type, key, 1);
		} else {
			JavaFieldEntity fieldEntity = new JavaFieldEntity();
			fieldEntity.setKey(key);
			fieldEntity.setType(DataType.typeOfObject(type).getValue());
			result = fieldEntity;
			if (type != null) {
				result.setValue(type.toString());
			}
		}
		result.setKey(key);
		return result;
	}

	private JavaFieldEntity handleJSONArray(JavaEntity parentClass, JSONArray jsonArray, String key, int deep) {

		JavaFieldEntity fieldEntity;
		if (jsonArray.length() > 0) {
			Object item = jsonArray.get(0);
			if (item instanceof JSONObject) {
				item = getJsonObject(jsonArray);
			}
			fieldEntity = listTypeByValue(parentClass, key, item, deep);
		} else {
			fieldEntity = new IterableFieldEntity();
			fieldEntity.setKey(key);
			fieldEntity.setType("?");
			((IterableFieldEntity) fieldEntity).setDeep(deep);
		}
		return fieldEntity;
	}

	private JavaEntity existDeclareClass(JSONObject jsonObject) {
		for (JavaEntity classEntity : declareClass.values()) {
			Iterator<String> keys = jsonObject.keys();
			boolean had = false;
			while (keys.hasNext()) {
				String key = keys.next();
				Object value = jsonObject.get(key);
				had = false;
				for (IField fieldEntity : classEntity.getFields()) {
					if (fieldEntity.getKey().equals(key) && DataType.isSameDataType(
							DataType.typeOfString(fieldEntity.getType()), DataType.typeOfObject(value))) {
						had = true;
						break;
					}
				}
				if (!had) {
					break;
				}
			}
			if (had) {
				return classEntity;
			}
		}
		return null;
	}

	private boolean existDeclareField(String key, JSONObject json) {
		IField fieldEntity = declareFields.get(key);
		if (fieldEntity == null) {
			return false;
		}
		return fieldEntity.isSameType(json.get(key));
	}

	private JSONObject parseJSONObject(String jsonStr) throws JSONException {
		if (jsonStr.startsWith("{")) {
			return new JSONObject(jsonStr);
		} else if (jsonStr.startsWith("[")) {
			JSONArray jsonArray = new JSONArray(jsonStr);
			if (jsonArray.length() > 0 && jsonArray.get(0) instanceof JSONObject) {
				return getJsonObject(jsonArray);
			}
		}
		return null;
	}

	private JSONObject getJsonObject(JSONArray jsonArray) {
		JSONObject resultJSON = jsonArray.getJSONObject(0);
		for (int i = 1; i < jsonArray.length(); i++) {
			Object value = jsonArray.get(i);
			if (!(value instanceof JSONObject)) {
				break;
			}
			JSONObject json = (JSONObject) value;
			for (String key : json.keySet()) {
				if (!resultJSON.keySet().contains(key)) {
					resultJSON.put(key, json.get(key));
				}
			}
		}
		return resultJSON;
	}

	/**
	 * @param className
	 * @param json
	 * @param parentClass
	 * @return
	 */
	private JavaEntity createInnerClass(String className, JSONObject json, JavaEntity parentClass) {
		JavaEntity subClassEntity = new JavaEntity();
		Set<String> set = json.keySet();
		List<String> list = new ArrayList<String>(set);
		List<IField> fields = createFields(json, list, subClassEntity, "\t\t");
		subClassEntity.addAllFields(fields);

		if (Config.getInstant().isSplitGenerate()) {
			subClassEntity.setPackName(info.packageName);
		} else {
			subClassEntity.setPackName(parentClass.getQualifiedName());
		}
		subClassEntity.setClassName(className);
		if (handleDeclareClassName(subClassEntity, "")) {
			CheckUtil.getInstant().addDeclareClassName(subClassEntity.getQualifiedName());
		}
		if (Config.getInstant().isReuseEntity()) {
			declareClass.put(subClassEntity.getQualifiedName(), subClassEntity);
		}
		parentClass.addInnerClass(subClassEntity);
		return subClassEntity;
	}

	private String createSubClassName(String key, Object o) {
		String name = "";
		if (o instanceof JSONObject) {
			if (TextUtils.isEmpty(key)) {
				return key;
			}
			String[] strings = key.split("_");
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < strings.length; i++) {
				stringBuilder.append(Tools.captureName(strings[i]));
			}
			name = stringBuilder.toString() + Config.getInstant().getSuffixStr();
		}
		return name;

	}

	private boolean handleDeclareClassName(JavaEntity classEntity, String appendName) {
		classEntity.setClassName(classEntity.getClassName() + appendName);
		if (CheckUtil.getInstant().containsDeclareClassName(classEntity.getQualifiedName())) {
			return handleDeclareClassName(classEntity, "X");
		}
		return true;
	}

	private String handleDeclareFieldName(String fieldName, String appendName) {
		fieldName += appendName;
		if (CheckUtil.getInstant().containsDeclareFieldName(fieldName)) {
			return handleDeclareFieldName(fieldName, "X");
		}
		return fieldName;
	}

	private JavaFieldEntity listTypeByValue(JavaEntity parentClass, String key, Object type, int deep) {

		JavaFieldEntity item = null;
		if (type instanceof JSONObject) {
			JavaEntity classEntity = existDeclareClass((JSONObject) type);
			if (classEntity == null) {
				IterableFieldEntity iterableFieldEntity = new IterableFieldEntity();
				JavaEntity innerClassEntity = createInnerClass(createSubClassName(key, type), (JSONObject) type,
						parentClass);
				iterableFieldEntity.setKey(key);
				iterableFieldEntity.setDeep(deep);
				iterableFieldEntity.setTargetClass(innerClassEntity);
				item = iterableFieldEntity;
			} else {
				IterableFieldEntity fieldEntity = new IterableFieldEntity();
				fieldEntity.setKey(key);
				fieldEntity.setTargetClass(classEntity);
				fieldEntity.setType(classEntity.getQualifiedName());
				fieldEntity.setDeep(deep);
				item = fieldEntity;
			}

		} else if (type instanceof JSONArray) {
			JavaFieldEntity fieldEntity = handleJSONArray(parentClass, (JSONArray) type, key, ++deep);
			fieldEntity.setKey(key);
			item = fieldEntity;
		} else {
			IterableFieldEntity fieldEntity = new IterableFieldEntity();
			fieldEntity.setKey(key);
			fieldEntity.setType(type.getClass().getSimpleName());
			fieldEntity.setDeep(deep);
			item = fieldEntity;
		}
		return item;
	}

	/**
	 * 过滤掉// 和/** 注释
	 *
	 * @param str
	 * @return
	 */
	public String removeComment(String str) {
		String temp = str.replaceAll("/\\*" + "[\\S\\s]*?" + "\\*/", "");
		return temp.replaceAll("//[\\S\\s]*?\n", "");
	}
}
