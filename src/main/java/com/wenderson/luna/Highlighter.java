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

			groups.put("KEYWORD", "red");

			groups.put("SEMICOLON", "grey");

			groups.put("STRING", "green");

			groups.put("COMMENT", "lightGrey");

			groups.put("NUMBER", "darkBlue");

			groups.put("CLASS", "purple");

			groups.put("CHARS", "orange");

			groups.put("BOOLEAN", "lightBlue");

			groups.put("SINGLEQUOTESTRING", "green");

			groups.put("FUNCTION", "darkPink");

			groups.put("CONSTANT", "yellow");

			groups.put("ANNOTATION", "brown");
		}

		if (programmingLanguage.equals("CSS")) {
			var selectorPattern = "(?<![A-Z])([a-z]\\w+|[a-z])(?=(\\s\\{))";

			var keywordPattern = "black|silver|gray|white|maroon|red|purple|fuchsia|green|lime|olive|yellow|navy|blue|teal|aqua|aliceblue|antiquewhite|aqua|aquamarine|azure|beige|bisque|black|blanchedalmond|blue|blueviolet|brown|burlywood|cadetblue|chartreuse|chocolate|coral|cornflowerblue|cornsilk|crimson|cyan|darkblue|darkcyan|darkgoldenrod|darkgray|darkgreen|darkgrey|darkkhaki|darkmagenta|darkolivegreen|darkorange|darkorchid|darkred|darksalmon|darkseagreen|darkslateblue|darkslategray|darkslategrey|darkturquoise|darkviolet|deeppink|deepskyblue|dimgray|dimgrey|dodgerblue|firebrick|floralwhite|forestgreen|fuchsia|gainsboro|ghostwhite|gold|goldenrod|gray|green|greenyellow|grey|honeydew|hotpink|indianred|indigo|ivory|khaki|lavender|lavenderblush|lawngreen|lemonchiffon|lightblue|lightcoral|lightcyan|lightgoldenrodyellow|lightgray|lightgreen|lightgrey|lightpink|lightsalmon|lightseagreen|lightskyblue|lightslategray|lightslategrey|lightsteelblue|lightyellow|lime|limegreen|linen|magenta|maroon|mediumaquamarine|mediumblue|mediumorchid|mediumpurple|mediumseagreen|mediumslateblue|mediumspringgreen|mediumturquoise|mediumvioletred|midnightblue|mintcream|mistyrose|moccasin|navajowhite|navy|oldlace|olive|olivedrab|orange|orangered|orchid|palegoldenrod|palegreen|paleturquoise|palevioletred|papayawhip|peachpuff|peru|pink|plum|powderblue|purple|red|rosybrown|royalblue|saddlebrown|salmon|sandybrown|seagreen|seashell|sienna|silver|skyblue|slateblue|slategray|slategrey|snow|springgreen|steelblue|tan|teal|thistle|tomato|turquoise|violet|wheat|white|whitesmoke|yellow|yellowgreen";

			pattern = Pattern.compile("(?<SELECTOR>" + selectorPattern + ")|(?<KEYWORD>" + keywordPattern + ")");

			groups.clear();

			groups.put("SELECTOR", "purple");

			groups.put("KEYWORD", "lightBlue");
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