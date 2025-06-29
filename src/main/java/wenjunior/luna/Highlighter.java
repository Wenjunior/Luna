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
		this.groups.clear();

		if (language.equals(SupportedLanguages.PLAIN_TEXT)) {
			this.pattern = Pattern.compile("");
		}

		if (language.equals(SupportedLanguages.JAVA)) {
			final String KEYWORD_PATTERN = "\\b(abstract|assert|break|case|catch|class|const|continue|default|do|else|enum|exports|extends|final|finally|for|goto|if|implements|import|instanceof|interface|module|native|new|package|private|protected|public|requires|return|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|var|void|volatile|while)\\b";

			final String SEMICOLON_PATTERN = ";";

			final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

			final String COMMENT_PATTERN = "//[^\n]*|/\\*(.|\\R)*?\\*/|/\\*[^\\v]*|^\\h*\\*([^\\v]*|/)";

			final String NUMBER_PATTERN = "(?<![a-zA-Z]+)[0-9](?!=[a-zA-Z]+)";

			final String CONSTANT_PATTERN = "(?-i)[A-Z]+(?![a-z])";

			final String CLASS_PATTERN = "(?<![a-z])[A-Z]+[a-z]\\w+";

			final String SPECIAL_CHAR_PATTERN = "=|\\+|-|\\*|/|!|&|\\||:|>|<|\\?";

			final String PRIMITIVE_TYPE_PATTERN = "\\b(byte|short|int|long|float|double|char|boolean)\\b";

			final String NULL_PATTERN = "\\bnull\\b";

			final String BOOLEAN_PATTERN = "\\b(true|false)\\b";

			final String SINGLE_QUOTE_STRING_PATTERN = "'([0-9a-zA-Z]|\r|\n|\t|\f|\\v)'";

			final String FUNCTION_PATTERN = "[a-z]\\w+(?=\\()";

			final String ANNOTATION_PATTERN = "@[A-z]+";

			final String PACKAGE_NAME_PATTERN = "(?<!this\\.)(?<=(\\.|package |requires |exports ))[a-zA-Z]+(?=(;| \\{))";

			this.pattern = Pattern.compile(
				"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
					+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
						+ "|(?<STRING>" + STRING_PATTERN + ")"
							+ "|(?<COMMENT>" + COMMENT_PATTERN + ")"
								+ "|(?<NUMBER>" + NUMBER_PATTERN + ")"
									+ "|(?<CLASS>" + CLASS_PATTERN + ")"
										+ "|(?<CONSTANT>" + CONSTANT_PATTERN + ")"
											+ "|(?<SPECIALCHAR>" + SPECIAL_CHAR_PATTERN + ")"
												+ "|(?<PRIMITIVETYPE>" + PRIMITIVE_TYPE_PATTERN + ")"
													+ "|(?<NULL>" + NULL_PATTERN + ")"
														+ "|(?<BOOLEAN>" + BOOLEAN_PATTERN + ")"
															+ "|(?<SINGLEQUOTESTRING>" + SINGLE_QUOTE_STRING_PATTERN + ")"
																+ "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
																	+ "|(?<ANNOTATION>" + ANNOTATION_PATTERN + ")"
																		+ "|(?<PACKAGENAME>" + PACKAGE_NAME_PATTERN + ")"
			);

			this.groups.put("KEYWORD", "red");

			this.groups.put("SEMICOLON", "grey");

			this.groups.put("STRING", "yellow");

			this.groups.put("COMMENT", "comment");

			this.groups.put("NUMBER", "pink");

			this.groups.put("CLASS", "purple");

			this.groups.put("CONSTANT", "orange");

			this.groups.put("SPECIALCHAR", "orange");

			this.groups.put("PRIMITIVETYPE", "cyan");

			this.groups.put("NULL", "cyan");

			this.groups.put("BOOLEAN", "cyan");

			this.groups.put("SINGLEQUOTESTRING", "yellow");

			this.groups.put("FUNCTION", "green");

			this.groups.put("ANNOTATION", "pink");

			this.groups.put("PACKAGENAME", "orange");
		}

		if (language.equals(SupportedLanguages.XML)) {
			final String SPECIAL_CHAR_PATTERN = "<|>|\\?|/";

			final String TAG_PATTERN = "(?<=(<|<\\/|<\\?))[a-zA-Z.]+(?=(>|\\?>))|(?<=(<|<\\?))[a-zA-Z]+ ";

			final String OPTION_PATTERN = "[a-zA-Z:]+(?==)";

			final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

			this.pattern = Pattern.compile(
				"(?<SPECIALCHAR>" + SPECIAL_CHAR_PATTERN + ")"
					+ "|(?<TAG>" + TAG_PATTERN + ")"
						+ "|(?<OPTION>" + OPTION_PATTERN + ")"
							+ "|(?<STRING>" + STRING_PATTERN + ")"
			);

			this.groups.put("SPECIALCHAR", "purple");

			this.groups.put("TAG", "pink");

			this.groups.put("OPTION", "green");

			this.groups.put("STRING", "yellow");
		}

		if (language.equals(SupportedLanguages.CSS)) {
			final String SELECTOR_PATTERN = "[a-z-]+(?= \\{)";

			final String PROPERTY_PATTERN = "[a-z-]+(?=:)";

			final String COLOR_PATTERN = "\\b(aliceblue|antiquewhite|aqua|aquamarine|azure|beige|bisque|black|blanchedalmond|blue|blueviolet|brown|burlywood|cadetblue|chartreuse|chocolate|coral|cornflowerblue|cornsilk|crimson|cyan|darkblue|darkcyan|darkgoldenrod|darkgray|darkgreen|darkgrey|darkkhaki|darkmagenta|darkolivegreen|darkorange|darkorchid|darkred|darksalmon|darkseagreen|darkslateblue|darkslategray|darkslategrey|darkturquoise|darkviolet|deeppink|deepskyblue|dimgray|dimgrey|dodgerblue|firebrick|floralwhite|forestgreen|fuchsia|gainsboro|ghostwhite|gold|goldenrod|gray|green|greenyellow|grey|honeydew|hotpink|indianred|indigo|ivory|khaki|lavender|lavenderblush|lawngreen|lemonchiffon|lightblue|lightcoral|lightcyan|lightgoldenrodyellow|lightgray|lightgreen|lightgrey|lightpink|lightsalmon|lightseagreen|lightskyblue|lightslategray|lightslategrey|lightsteelblue|lightyellow|lime|limegreen|linen|magenta|maroon|mediumaquamarine|mediumblue|mediumorchid|mediumpurple|mediumseagreen|mediumslateblue|mediumspringgreen|mediumturquoise|mediumvioletred|midnightblue|mintcream|mistyrose|moccasin|navajowhite|navy|oldlace|olive|olivedrab|orange|orangered|orchid|palegoldenrod|palegreen|paleturquoise|palevioletred|papayawhip|peachpuff|peru|pink|plum|powderblue|purple|red|rosybrown|royalblue|saddlebrown|salmon|sandybrown|seagreen|seashell|sienna|silver|skyblue|slateblue|slategray|slategrey|snow|springgreen|steelblue|tan|teal|thistle|tomato|turquoise|violet|wheat|white|whitesmoke|yellow|yellowgreen)\\b";

			final String SEMICOLON_PATTERN = ";";

			this.pattern = Pattern.compile(
				"(?<SELECTOR>" + SELECTOR_PATTERN + ")"
					+ "|(?<PROPERTY>" + PROPERTY_PATTERN + ")"
						+ "|(?<COLOR>" + COLOR_PATTERN + ")"
							+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
			);

			this.groups.put("SELECTOR", "purple");

			this.groups.put("PROPERTY", "purple");

			this.groups.put("COLOR", "cyan");

			this.groups.put("SEMICOLON", "grey");
		}
	}

	public StyleSpans<Collection<String>> highlightSyntax(String text) {
		Matcher matcher = this.pattern.matcher(text);

		int lastKeywordEnd = 0;

		StyleSpansBuilder<Collection<String>> styleSpansBuilder = new StyleSpansBuilder<>();

		String styleClass = null;

		while (matcher.find()) {
			for (String group : this.groups.keySet()) {
				if (matcher.group(group) != null) {
					styleClass = this.groups.get(group);
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