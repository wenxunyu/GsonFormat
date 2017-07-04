package org.gsonformat.plugin.utils;

public final class TextUtils {
	/**
	 * Returns true if the parameter is null or of zero length
	 */
	public static boolean isEmpty(final CharSequence s) {
		if (s == null) {
			return true;
		}
		return s.length() == 0;
	}

	/**
	 * Returns true if the parameter is null or contains only whitespace
	 */
	public static boolean isBlank(final CharSequence s) {
		if (s == null) {
			return true;
		}
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isWhitespace(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @since 4.4
	 */
	public static boolean containsBlanks(final CharSequence s) {
		if (s == null) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			if (Character.isWhitespace(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}

}
