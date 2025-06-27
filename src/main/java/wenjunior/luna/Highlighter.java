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

	public void setSyntax(SupportedLanguages language) {
		groups.clear();

		if (language.equals(SupportedLanguages.PLAIN_TEXT)) {
			pattern = Pattern.compile("");
		}

		if (language.equals(SupportedLanguages.JAVA)) {
			final String KEYWORD_PATTERN = "\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|exports|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|module|native|new|package|private|protected|public|requires|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|var|void|volatile|while)\\b";

			final String SEMICOLON_PATTERN = ";";

			final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

			final String COMMENT_PATTERN = "//[^\n]*|/\\*(.|\\R)*?\\*/|/\\*[^\\v]*|^\\h*\\*([^\\v]*|/)";

			final String NUMBER_PATTERN = "(?<![a-z]|[A-Z])[0-9](?!=([a-z]|[A-Z]))";

			final String CONSTANT_PATTERN = "(?-i)[A-Z]+(?![a-z])";

			final String CLASS_PATTERN = "(?<![a-z])([A-Z]\\w+|[A-Z])";

			final String SPECIAL_CHAR_PATTERN = "\\b(=|\\+|-|\\*|\\/|!|&|\\|:|\\>|\\<|\\?)\\b";

			final String PRIMITIVE_TYPE_PATTERN = "\\b(short|int|long|float|double|char|bool)\\b";

			final String NULL_PATTERN = "\\bnull\\b";

			final String BOOLEAN_PATTERN = "\\b(true|false)\\b";

			final String SINGLE_QUOTE_STRING_PATTERN = "'(.*?)'";

			final String FUNCTION_PATTERN = "[a-z]\\w+(?=\\()";

			final String ANNOTATION_PATTERN = "@([A-Z]\\w+|[A-Z])";

			final String PACKAGE_NAME_PATTERN = "(?<=\\.)([a-z]\\w+|[a-z])(?=;)";

			pattern = Pattern.compile(
				"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
					+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
						+ "|(?<STRING>" + STRING_PATTERN + ")"
							+ "|(?<COMMENT>" + COMMENT_PATTERN + ")"
								+ "|(?<NUMBER>" + NUMBER_PATTERN + ")"
									+ "|(?<CONSTANT>" + CONSTANT_PATTERN + ")"
										+ "|(?<CLASS>" + CLASS_PATTERN + ")"
											+ "|(?<SPECIALCHAR>" + SPECIAL_CHAR_PATTERN + ")"
												+ "|(?<PRIMITIVETYPE>" + PRIMITIVE_TYPE_PATTERN + ")"
													+ "|(?<NULL>" + NULL_PATTERN + ")"
														+ "|(?<BOOLEAN>" + BOOLEAN_PATTERN + ")"
															+ "|(?<SINGLEQUOTESTRING>" + SINGLE_QUOTE_STRING_PATTERN + ")"
																+ "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
																	+ "|(?<ANNOTATION>" + ANNOTATION_PATTERN + ")"
																		+ "|(?<PACKAGENAME>" + PACKAGE_NAME_PATTERN + ")"
			);

			groups.put("KEYWORD", "red");

			groups.put("SEMICOLON", "grey");

			groups.put("STRING", "yellow");

			groups.put("COMMENT", "comment");

			groups.put("NUMBER", "pink");

			groups.put("CONSTANT", "orange");

			groups.put("CLASS", "purple");

			groups.put("SPECIALCHAR", "orange");

			groups.put("PRIMITIVETYPE", "cyan");

			groups.put("NULL", "cyan");

			groups.put("BOOLEAN", "cyan");

			groups.put("SINGLEQUOTESTRING", "yellow");

			groups.put("FUNCTION", "green");

			groups.put("ANNOTATION", "pink");

			groups.put("PACKAGENAME", "orange");
		}

		if (language.equals(SupportedLanguages.XML)) {
			final String TAG_PATTERN = "(?<=(<|<\\/))([a-z]\\w+|[a-z]|[A-Z]\\w+|[A-Z])";

			final String OPTION_PATTERN = "([a-z]\\w+|[a-z]|[A-Z]\\w+|[A-Z])(?==)";

			final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

			pattern = Pattern.compile(
				"(?<TAG>" + TAG_PATTERN + ")"
					+ "|(?<OPTION>" + OPTION_PATTERN + ")"
						+ "|(?<STRING>" + STRING_PATTERN + ")"
			);

			groups.put("TAG", "pink");

			groups.put("OPTION", "green");

			groups.put("STRING", "yellow");
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