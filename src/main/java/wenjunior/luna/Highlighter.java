package wenjunior.luna;

import java.util.*;
import java.util.regex.*;
import org.fxmisc.richtext.model.*;

public class Highlighter {
	private Pattern pattern = Pattern.compile("");

	private HashMap<String, String> groups = new HashMap<>();

	public void setSyntax(String programmingLanguage) {
		groups.clear();

		if (programmingLanguage.equals("Plain Text")) {
			pattern = Pattern.compile("");
		}

		if (programmingLanguage.equals("Java")) {
			var keywordPattern = "\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while|var|module|requires|exports)\\b";

			var semicolonPattern = "\\;";

			var stringPattern = "\"([^\"\\\\]|\\\\.)*\"";

			var commentPattern = "//[^\n]*|/\\*(.|\\R)*?\\*/|/\\*[^\\v]*|^\\h*\\*([^\\v]*|/)";

			var numberPattern = "[0-9]";

			var classPattern = "(?<=\\.)[A-Z]\\w+(?=\\;)|[A-Z]\\w+(?=\\[)|[A-Z]\\w+(?=\\<)|(?<=class\\s)[A-Z]\\w+|(?<=new\\s)[A-Z]\\w+|(?<=extends\\s)[A-Z]\\w+|(?<=implements\\s)[A-Z]\\w+|(?<![a-z]\\w+)[A-Z]\\w+(?=\\.)|[A-Z]\\w+(?=\\s[a-z])";

			var specialCharPattern = "=|\\+|-|\\*|\\/|!|&|\\|:|\\>|\\<|\\?";

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
				+ "|(?<SPECIALCHAR>" + specialCharPattern + ")"
				+ "|(?<BOOLEAN>" + booleanPattern + ")"
				+ "|(?<SINGLEQUOTESTRING>" + singleQuoteStringPattern + ")"
				+ "|(?<FUNCTION>" + functionPattern + ")"
				+ "|(?<CONSTANT>" + constantPattern + ")"
				+ "|(?<ANNOTATION>" + annotationPattern + ")"
			);

			groups.put("KEYWORD", "red");

			groups.put("SEMICOLON", "grey");

			groups.put("STRING", "green");

			groups.put("COMMENT", "light-grey");

			groups.put("NUMBER", "dark-blue");

			groups.put("CLASS", "purple");

			groups.put("SPECIALCHAR", "orange");

			groups.put("BOOLEAN", "light-blue");

			groups.put("SINGLEQUOTESTRING", "green");

			groups.put("FUNCTION", "pink");

			groups.put("CONSTANT", "yellow");

			groups.put("ANNOTATION", "brown");
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