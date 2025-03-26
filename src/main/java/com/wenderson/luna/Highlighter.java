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
		"switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
	};

	private static String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";

	private static String PAREN_PATTERN = "\\(|\\)";

	private static String BRACE_PATTERN = "\\{|\\}";

	private static String BRACKET_PATTERN = "\\[|\\]";

	private static String SEMICOLON_PATTERN = "\\;";

	private static String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

	private static String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/" + "|" + "/\\*[^\\v]*" + "|" + "^\\h*\\*([^\\v]*|/)";

	private static Pattern PATTERN = Pattern.compile(
		"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
		+ "|(?<PAREN>" + PAREN_PATTERN + ")"
		+ "|(?<BRACE>" + BRACE_PATTERN + ")"
		+ "|(?<BRACKET>" + BRACKET_PATTERN + ")"
		+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
		+ "|(?<STRING>" + STRING_PATTERN + ")"
		+ "|(?<COMMENT>" + COMMENT_PATTERN + ")"
	);

	static StyleSpans<Collection<String>> highlightSyntax(String text) {
		var matcher = PATTERN.matcher(text);

		var lastKeywordEnd = 0;

		var styleSpansBuilder = new StyleSpansBuilder<Collection<String>>();

		while(matcher.find()) {
			var styleClass = matcher.group("KEYWORD") != null ? "keyword" :
				matcher.group("PAREN") != null ? "paren" :
				matcher.group("BRACE") != null ? "brace" :
				matcher.group("BRACKET") != null ? "bracket" :
				matcher.group("SEMICOLON") != null ? "semicolon" :
				matcher.group("STRING") != null ? "string" :
				matcher.group("COMMENT") != null ? "comment" :
				null; assert styleClass != null;

			styleSpansBuilder.add(Collections.emptyList(), matcher.start() - lastKeywordEnd);

			styleSpansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());

			lastKeywordEnd = matcher.end();
		}

		styleSpansBuilder.add(Collections.emptyList(), text.length() - lastKeywordEnd);

		return styleSpansBuilder.create();
	}
}