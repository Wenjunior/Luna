package com.wenderson.luna;

import java.util.*;
import java.util.regex.*;
import org.fxmisc.richtext.model.*;

public class Highlighter {
	private Pattern pattern = Pattern.compile("");;

	private HashMap<String, String> groups = new HashMap<>();

	public void setSyntax(String programmingLanguage) {
		if (programmingLanguage.equals("Plain text")) {
			pattern = Pattern.compile("");

			groups.clear();
		}

		if (programmingLanguage.equals("Java")) {
			var keywords = new String[] {
				"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
				"continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
				"for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
				"new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
				"switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
				"var", "module", "requires", "exports"
			};

			var keywordPattern = "\\b(" + String.join("|", keywords) + ")\\b";

			var semicolonPattern = "\\;";

			var stringPattern = "\"([^\"\\\\]|\\\\.)*\"";

			var commentPattern = "//[^\n]*|/\\*(.|\\R)*?\\*/|/\\*[^\\v]*|^\\h*\\*([^\\v]*|/)";

			var numberPattern = "[0-9]";

			var classPattern = "(?<=\\.)[A-Z]\\w+(?=\\;)|[A-Z]\\w+(?=\\[)|[A-Z]\\w+(?=\\<)|(?<=class\\s)[A-Z]\\w+|(?<=new\\s)[A-Z]\\w+|(?<=extends\\s)[A-Z]\\w+|(?<=implements\\s)[A-Z]\\w+|(?<![a-z]\\w+)[A-Z]\\w+(?=\\.)|[A-Z]\\w+(?=\\s[a-z])";

			var charPattern = "=|\\+|-|\\*|\\/|!|&|\\|:|\\>|\\<|\\?";

			var booleanPattern = "true|false";

			var singleQuoteStringPattern = "'(.*?)'";

			var functionPattern = "[a-z]\\w+(?=\\()";

			var constantPattern = "(?-i)[A-Z]+(?![a-z])";

			var annotationPattern = "@([A-Z]\\w+|[A-Z])";

			pattern = Pattern.compile(
				"(?<KEYWORD>" + keywordPattern + ")"
				+ "|(?<SEMICOLON>" + semicolonPattern + ")"
				+ "|(?<STRING>" + stringPattern + ")"
				+ "|(?<COMMENT>" + commentPattern + ")"
				+ "|(?<NUMBER>" + numberPattern + ")"
				+ "|(?<CLASS>" + classPattern + ")"
				+ "|(?<CHARS>" + charPattern + ")"
				+ "|(?<BOOLEAN>" + booleanPattern + ")"
				+ "|(?<SINGLEQUOTESTRING>" + singleQuoteStringPattern + ")"
				+ "|(?<FUNCTION>" + functionPattern + ")"
				+ "|(?<CONSTANT>" + constantPattern + ")"
				+ "|(?<ANNOTATION>" + annotationPattern + ")"
			);

			groups.clear();

			groups.put("KEYWORD", "keyword");

			groups.put("SEMICOLON", "semicolon");

			groups.put("STRING", "string");

			groups.put("COMMENT", "comment");

			groups.put("NUMBER", "number");

			groups.put("CLASS", "class");

			groups.put("CHARS", "chars");

			groups.put("BOOLEAN", "boolean");

			groups.put("SINGLEQUOTESTRING", "singleQuoteString");

			groups.put("FUNCTION", "function");

			groups.put("CONSTANT", "constant");

			groups.put("ANNOTATION", "annotation");
		}
	}

	public StyleSpans<Collection<String>> highlightSyntax(String text) {
		var matcher = pattern.matcher(text);

		var lastKeywordEnd = 0;

		var styleSpansBuilder = new StyleSpansBuilder<Collection<String>>();

		String styleClass = null;

		while (matcher.find()) {
			for (var group : groups.keySet()) {
				if (matcher.group(group) != null) {
					styleClass = groups.get(group);
				}
			}

			styleSpansBuilder.add(Collections.emptyList(), matcher.start() - lastKeywordEnd);

			styleSpansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());

			lastKeywordEnd = matcher.end();
		}

		styleSpansBuilder.add(Collections.emptyList(), text.length() - lastKeywordEnd);

		return styleSpansBuilder.create();
	}
}