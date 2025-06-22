package wenjunior.luna;

import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class Highlighter {
	private Pattern pattern = Pattern.compile("");

	private HashMap<String, String> groups = new HashMap<>();

	public void setSyntax(String programmingLanguage) {
		groups.clear();

		if (programmingLanguage.equals("Plain Text")) {
			pattern = Pattern.compile("");
		}

		if (programmingLanguage.equals("Java")) {
			String keywordPattern = "\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|exports|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|module|native|new|package|private|protected|public|requires|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|var|void|volatile|while)\\b";

			String semicolonPattern = "\\;";

			String stringPattern = "\"([^\"\\\\]|\\\\.)*\"";

			String commentPattern = "//[^\n]*|/\\*(.|\\R)*?\\*/|/\\*[^\\v]*|^\\h*\\*([^\\v]*|/)";

			String numberPattern = "[0-9]";

			String classPattern = "(?<![a-z])[A-Z]([a-z]\\w+|[a-z])";

			String specialCharPattern = "=|\\+|-|\\*|\\/|!|&|\\|:|\\>|\\<|\\?";

			String primitiveTypesAndNullAndBooleanPattern = "\\b(short|int|long|float|double|char|null|bool|true|false)\\b";

			String singleQuoteStringPattern = "'(.*?)'";

			String functionPattern = "[a-z]\\w+(?=\\()";

			String constantPattern = "(?-i)[A-Z]+(?![a-z])";

			String annotationPattern = "@([A-Z]\\w+|[A-Z])";

			pattern = Pattern.compile(
				"(?<KEYWORD>" + keywordPattern + ")"
				+ "|(?<SEMICOLON>" + semicolonPattern + ")"
				+ "|(?<STRING>" + stringPattern + ")"
				+ "|(?<COMMENT>" + commentPattern + ")"
				+ "|(?<NUMBER>" + numberPattern + ")"
				+ "|(?<CLASS>" + classPattern + ")"
				+ "|(?<SPECIALCHAR>" + specialCharPattern + ")"
				+ "|(?<PRIMITIVETYPESANDNULLANDBOOLEAN>" + primitiveTypesAndNullAndBooleanPattern + ")"
				+ "|(?<SINGLEQUOTESTRING>" + singleQuoteStringPattern + ")"
				+ "|(?<FUNCTION>" + functionPattern + ")"
				+ "|(?<CONSTANT>" + constantPattern + ")"
				+ "|(?<ANNOTATION>" + annotationPattern + ")"
			);

			groups.put("KEYWORD", "red");

			groups.put("SEMICOLON", "comment");

			groups.put("STRING", "yellow");

			groups.put("COMMENT", "comment");

			groups.put("NUMBER", "pink");

			groups.put("CLASS", "purple");

			groups.put("SPECIALCHAR", "orange");

			groups.put("PRIMITIVETYPESANDNULLANDBOOLEAN", "cyan");

			groups.put("SINGLEQUOTESTRING", "yellow");

			groups.put("FUNCTION", "green");

			groups.put("CONSTANT", "orange");

			groups.put("ANNOTATION", "pink");
		}
	}

	public StyleSpans<Collection<String>> highlightSyntax(String text) {
		Matcher matcher = pattern.matcher(text);

		int lastKeywordEnd = 0;

		StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();

		String styleClass = null;

		while (matcher.find()) {
			for (String group : groups.keySet()) {
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