package com.wenderson.luna;

import java.util.*;
import java.util.regex.*;
import org.fxmisc.richtext.model.*;

public class Highlighter {
	private static String[] KEYWORDS = new String[] {
		"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
		"continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
		"for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
		"new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
		"switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
		"var", "module", "requires", "exports"
	};

	private static String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";

	private static String SEMICOLON_PATTERN = "\\;";

	private static String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

	private static String COMMENT_PATTERN = "//[^\n]*|/\\*(.|\\R)*?\\*/|/\\*[^\\v]*|^\\h*\\*([^\\v]*|/)";

	private static String NUMBER_PATTERN = "[0-9]";

	private static String CLASS_PATTERN = "(?<=\\.)[A-Z]\\w+(?=\\;)|[A-Z]\\w+(?=\\[\\])|(?<=class\\s)[A-Z]\\w+|(?<=new\\s)[A-Z]\\w+|(?<=extends\\s)[A-Z]\\w+|(?<![a-z]\\w+)[A-Z]\\w+(?=\\.)|[A-Z]\\w+(?=\\s[a-z])";

	private static String CHARS_PATTERN = "=|\\+|-|\\*|\\/|!|&|\\|@|:|\\>|\\<|@";

	private static String BOOLEAN_PATTERN = "true|false";

	private static String SINGLE_QUOTE_STRING = "'(.*?)'";

	private static Pattern PATTERN = Pattern.compile(
		"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
		+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
		+ "|(?<STRING>" + STRING_PATTERN + ")"
		+ "|(?<COMMENT>" + COMMENT_PATTERN + ")"
		+ "|(?<NUMBER>" + NUMBER_PATTERN + ")"
		+ "|(?<CLASS>" + CLASS_PATTERN + ")"
		+ "|(?<CHARS>" + CHARS_PATTERN + ")"
		+ "|(?<BOOLEAN>" + BOOLEAN_PATTERN + ")"
		+ "|(?<SINGLEQUOTESTRING>" + SINGLE_QUOTE_STRING + ")" // O nome do grupo não pode conter underline.
	);

	private String programmingLanguage = "Plain text";

	public void setSyntax(String programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}

	public StyleSpans<Collection<String>> highlightSyntax(String text) {
		var matcher = PATTERN.matcher(text);

		var lastKeywordEnd = 0;

		var styleSpansBuilder = new StyleSpansBuilder<Collection<String>>();

		if (this.programmingLanguage.equals("Java")) {
			while (matcher.find()) {
				var styleClass = matcher.group("KEYWORD") != null ? "keyword" :
					matcher.group("SEMICOLON") != null ? "semicolon" :
						matcher.group("STRING") != null ? "string" :
							matcher.group("COMMENT") != null ? "comment" :
								matcher.group("NUMBER") != null ? "number" :
									matcher.group("CLASS") != null ? "class" :
										matcher.group("CHARS") != null ? "chars" :
											matcher.group("BOOLEAN") != null ? "boolean" :
												matcher.group("SINGLEQUOTESTRING") != null ? "single_quote_string" :
												null; assert styleClass != null;

				styleSpansBuilder.add(Collections.emptyList(), matcher.start() - lastKeywordEnd);

				styleSpansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());

				lastKeywordEnd = matcher.end();
			}
		}

		styleSpansBuilder.add(Collections.emptyList(), text.length() - lastKeywordEnd);

		return styleSpansBuilder.create();
	}
}