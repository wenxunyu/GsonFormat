/*文 件 名:  JsonDialog.java
 * 修 改 人:  wenxunyu@126.com
 * 修改时间:  2017年6月22日
 */
package org.gsonformat.plugin.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.gsonformat.plugin.ConvertBridge;
import org.gsonformat.plugin.config.Config;
import org.gsonformat.plugin.utils.Logger;
import org.gsonformat.plugin.utils.TextUtils;
import org.gsonformat.plugin.utils.Tools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Json编辑面板
 * 
 * @author wenxunyu@126.com
 * @version [2017年6月22日]
 * @since JDK7.0
 */
public class JsonUI extends ApplicationWindow {
	private Text textPackageName;
	private Text textClassName;
	private Text textContent;
	private ICompilationUnit workingCopy;
	private String clsName, packageName;
	private boolean modify;

	public JsonUI(Shell parentShell, ICompilationUnit originalUnit) {
		this(parentShell, originalUnit, false);
	}

	public JsonUI(Shell parentShell, ICompilationUnit originalUnit, boolean modify) {
		super(parentShell);
		this.workingCopy = originalUnit;
		this.modify = modify;
	}

	public void run() {
		open();
		setBlockOnOpen(true);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("GsonFormat");
		shell.setSize(700, 600);
	}

	/**
	 * 创建Dialog中的界面内容
	 * 
	 * @param parent
	 * @return
	 */
	@Override
	protected Control createContents(Composite parent) {
		clsName = Tools.getClassName(workingCopy.getElementName());
		IPackageDeclaration packageDeclaration = null;
		try {
			packageDeclaration = workingCopy.getPackageDeclarations()[0];
			packageName = packageDeclaration.getElementName();
		} catch (JavaModelException e) {
			Logger.error(e.getMessage());
		}

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Composite topPane = new Composite(composite, SWT.NONE);
		topPane.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		GridLayout gl_topPane = new GridLayout(7, false);
		gl_topPane.marginRight = 5;
		topPane.setLayout(gl_topPane);
		Label className = new Label(topPane, SWT.NONE);
		className.setText("类名");
		textClassName = new Text(topPane, SWT.BORDER);
		textClassName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		textClassName.setText(clsName);
		textClassName.setEnabled(modify);
		textClassName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (classNameQualified(textClassName.getText())) {
					return;// 验证合法
				}
				textClassName.setText(clsName);
				// 输入类名不合法，重置
			}
		});
		Label packageButton = new Label(topPane, SWT.NONE);
		packageButton.setText("包名");

		textPackageName = new Text(topPane, SWT.BORDER);
		textPackageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		if (packageDeclaration != null) {
			textPackageName.setText(packageName);
		} else {
			textPackageName.setText(Config.getInstant().getPackageName());
		}
		textPackageName.setEnabled(modify);
		textPackageName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (packageNameQualified(textPackageName.getText())) {
					return;// 验证合法
				}
				textPackageName.setText(packageName);
				// 输入包名不合法，重置
			}
		});

		Button format = new Button(topPane, SWT.NONE);
		format.setText("格式化");

		// http://blog.csdn.net/i_love_home/article/details/20871185
		// 多行文本框，可自动换行 | 垂直滚动条
		textContent = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		textContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite bottomPane = new Composite(composite, SWT.NONE);
		bottomPane.setLayout(new GridLayout(4, false));
		bottomPane.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button btnSetting = new Button(bottomPane, SWT.CENTER);
		btnSetting.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnSetting.setText(" 设置  ");
		btnSetting.setToolTipText("暂时不支持设置");
		btnSetting.setEnabled(false);

		Label temp = new Label(bottomPane, SWT.NONE);
		temp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnCancel = new Button(bottomPane, SWT.CENTER);
		btnCancel.setToolTipText("关闭界面");
		btnCancel.setText(" 取消  ");
		btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button btnConfirm = new Button(bottomPane, SWT.CENTER);
		btnConfirm.setText(" 确认  ");
		btnConfirm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		initMenu();

		format.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String json = textContent.getText().trim();
				if (json.startsWith("{")) {
					try {
						JSONObject jsonObject = new JSONObject(json);
						json = jsonObject.toString(4);
					} catch (JSONException exception) {
						Tools.tip(getShell(), "Json格式有误", exception.getMessage());
						return;
					}
				} else if (json.startsWith("[")) {
					// 会出现异常
					try {
						JSONArray jsonArray = new JSONArray(json);
						json = jsonArray.toString(4);
					} catch (JSONException exception) {
						Tools.tip(getShell(), "Json格式有误", exception.getMessage());
						return;
					}
				} else {
					Tools.tip(getShell(), "提示", "不是JSON字符串");
					return;
				}
				textContent.setText(json);
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				confirm();
			}
		});
		return parent;
	}

	private void confirm() {
		String pkg = textPackageName.getText().trim();
		String cls = textClassName.getText().trim();

		if (modify && (classNameQualified(pkg) || classNameQualified(cls))) {
			Tools.tip(getShell(), "提示", "请检查包名或者类名不合法");
			return;
		}
		String jsonStr = textContent.getText().trim();
		if (TextUtils.isEmpty(jsonStr)) {
			return;
		}
		new ConvertBridge(null, workingCopy, pkg, cls, modify, jsonStr).run();
	}

	private Pattern patternPackageName = Pattern.compile("([_a-z][_a-z0-9]*([.][_a-z][_a-z0-9]*)*)");
	private Pattern patternClassName = Pattern.compile("([_A-Z][_a-zA-Z0-9]*)");

	/** 验证类名是否合法 */
	protected boolean classNameQualified(String javaName) {
		Matcher matcher = patternClassName.matcher(javaName);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	/** 验证包名是否合法 */
	protected boolean packageNameQualified(String pagName) {
		Matcher matcher = patternPackageName.matcher(pagName);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	private void initMenu() {
		Menu menu = new Menu(textContent);
		textContent.setMenu(menu);

		MenuItem miPaste = new MenuItem(menu, SWT.NONE);

		miPaste.setText("粘贴");
		MenuItem miCopy = new MenuItem(menu, SWT.NONE);
		miCopy.setText("复制");
		MenuItem miCut = new MenuItem(menu, SWT.NONE);
		miCut.setText("剪切");

		MenuItem miSelected = new MenuItem(menu, SWT.NONE);
		miSelected.setText("全选");

		MenuItem miClear = new MenuItem(menu, SWT.NONE);
		miClear.setText("清空");

		miPaste.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				textContent.paste();
			}
		});
		miCopy.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (textContent.getSelectionCount() > 0) {
					textContent.copy();
				}
			}
		});
		miCut.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (textContent.getSelectionCount() > 0) {
					textContent.cut();
				}
			}
		});
		miSelected.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				textContent.selectAll();
			}
		});
		miClear.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				textContent.setText("");
			}
		});
	}

	/**
	 * 重载这个方法可以改变窗口的默认式样 SWT.RESIZE：窗口可以拖动边框改变大小 SWT.MAX： 窗口可以最大化
	 */
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
}
