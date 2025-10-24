#include "Highlighter.hpp"

Highlighter::Highlighter(QTextDocument *parent) : QSyntaxHighlighter(parent) {
	// Keywords

	HighlightingRule rule;

	QTextCharFormat keywordFormat;

	QColor red(255, 85, 86);

	keywordFormat.setForeground(red);

	keywordFormat.setFontWeight(QFont::Bold);

	rule.pattern = QRegularExpression(QStringLiteral("\\b(override|for|alignas|alignof|and|and_eq|asm|atomic_cancel|atomic_commit|atomic_noexcept|auto|bitand|bitor|break|case|catch|class|compl|concept|const|consteval|constexpr|constinit|const_cast|continue|contract_assert|co_await|co_return|co_yield|decltype|default|delete|do|double|dynamic_cast|else|enum|explicit|export|extern|forv|friend|goto|if|inline|mutable|namespace|new|noexcept|not|not_eq|nullptrv|operator|or|or_eq|private|protected|public|reflexpr|register|reinterpret_cast|requires|return|signed|sizeof|static|static_assert|static_cast|struct|switch|synchronized|template|this|thread_local|throw|try|typedef|typeid|typename|union|unsigned|using|virtual|volatile|wchar_t|while|xor|xor_eq)\\b"));

	rule.format = keywordFormat;

	highlightingRules.append(rule);

	// Directives

	QTextCharFormat directiveFormat;

	directiveFormat.setForeground(red);

	rule.pattern = QRegularExpression(QStringLiteral("#\\b(include|define|undef|elif|else|endif|ifdef|ifndef|if|error|warning|pragma|line)\\b"));

	rule.format = directiveFormat;

	highlightingRules.append(rule);

	// Primitive types

	QTextCharFormat primitiveTypeFormat;

	QColor cyan(139, 233, 253);

	primitiveTypeFormat.setForeground(cyan);

	rule.pattern = QRegularExpression(QStringLiteral("\\b(int|float|double|char|bool|short|long|void|char8_t|char16_t|char32_t)\\b"));

	rule.format = primitiveTypeFormat;

	highlightingRules.append(rule);

	// Booleans

	QTextCharFormat booleanFormat;

	booleanFormat.setForeground(cyan);

	rule.pattern = QRegularExpression(QStringLiteral("\\b(true|false)\\b"));

	rule.format = booleanFormat;

	highlightingRules.append(rule);

	// Functions

	QTextCharFormat functionFormat;

	QColor green(80, 250, 123);

	functionFormat.setForeground(green);

	rule.pattern = QRegularExpression(QStringLiteral("[0-9a-zA-Z_]+(?=\\()"));

	rule.format = functionFormat;

	highlightingRules.append(rule);

	// Integer literals

	QTextCharFormat integerLiteralsFormat;

	QColor pink(255, 121, 198);

	integerLiteralsFormat.setForeground(pink);

	rule.pattern = QRegularExpression(QStringLiteral("0(x|b)[0-9a-zA-Z]+"));

	rule.format = integerLiteralsFormat;

	highlightingRules.append(rule);

	// Classes

	QTextCharFormat classAndEnumFormat;

	QColor purple(189, 147, 249);

	classAndEnumFormat.setForeground(purple);

	rule.pattern = QRegularExpression(QStringLiteral("(?<![a-z0-9:])[A-Z][0-9a-zA-Z]+"));

	rule.format = classAndEnumFormat;

	highlightingRules.append(rule);

	// Includes

	QTextCharFormat includeFormat;

	QColor orange(255, 184, 108);

	includeFormat.setForeground(orange);

	rule.pattern = QRegularExpression(QStringLiteral("(?<=#include )<(.*)>"));

	rule.format = includeFormat;

	highlightingRules.append(rule);

	// Macros

	QTextCharFormat macroFormat;

	classAndEnumFormat.setForeground(orange);

	rule.pattern = QRegularExpression(QStringLiteral("\\b[A-Z_]+\\b"));

	rule.format = macroFormat;

	highlightingRules.append(rule);

	// Null values

	QTextCharFormat nullFormat;

	nullFormat.setForeground(cyan);

	rule.pattern = QRegularExpression(QStringLiteral("\\b(nullptr|NULL)\\b"));

	rule.format = nullFormat;

	highlightingRules.append(rule);

	// Operators

	QTextCharFormat operatorFormat;

	operatorFormat.setForeground(orange);

	rule.pattern = QRegularExpression(QStringLiteral("\\+|-|\\*|\\/|=|!|&|\\||<|>|:"));

	rule.format = operatorFormat;

	highlightingRules.append(rule);

	// Semicolons

	QTextCharFormat semicolonFormat;

	semicolonFormat.setForeground(Qt::lightGray);

	rule.pattern = QRegularExpression(QStringLiteral(";"));

	rule.format = semicolonFormat;

	highlightingRules.append(rule);

	// Numbers

	QTextCharFormat numberFormat;

	numberFormat.setForeground(pink);

	rule.pattern = QRegularExpression(QStringLiteral("\\b[0-9]+\\b"));

	rule.format = numberFormat;

	highlightingRules.append(rule);

	// Strings

	QTextCharFormat stringFormat;

	QColor yellow(241, 250, 140);

	stringFormat.setForeground(yellow);

	rule.pattern = QRegularExpression(QStringLiteral("\"([^\"\\\\]|\\\\.)*\"|'.'"));

	rule.format = stringFormat;

	highlightingRules.append(rule);

	// Single line comments

	QTextCharFormat singleLineCommentFormat;

	singleLineCommentFormat.setForeground(Qt::lightGray);

	rule.pattern = QRegularExpression(QStringLiteral("//[^\n]*"));

	rule.format = singleLineCommentFormat;

	highlightingRules.append(rule);

	// Multi line comments

	multiLineCommentFormat.setForeground(Qt::lightGray);

	commentStartExpression = QRegularExpression(QStringLiteral("/\\*"));

	commentEndExpression = QRegularExpression(QStringLiteral("\\*/"));
}

void Highlighter::highlightBlock(const QString &text) {
	for (const HighlightingRule &rule : std::as_const(highlightingRules)) {
		QRegularExpressionMatchIterator matchIterator = rule.pattern.globalMatch(text);

		while (matchIterator.hasNext()) {
			QRegularExpressionMatch match = matchIterator.next();

			setFormat(match.capturedStart(), match.capturedLength(), rule.format);
		}
	}

	setCurrentBlockState(0);

	int startIndex = 0;

	if (previousBlockState() != 1) {
		startIndex = text.indexOf(commentStartExpression);
	}

	while (startIndex >= 0) {
		QRegularExpressionMatch match = commentEndExpression.match(text, startIndex);

		int endIndex = match.capturedStart();

		int commentLength = 0;

		if (endIndex == -1) {
			setCurrentBlockState(1);

			commentLength = text.length() - startIndex;
		} else {
			commentLength = endIndex - startIndex + match.capturedLength();
		}

		setFormat(startIndex, commentLength, multiLineCommentFormat);

		startIndex = text.indexOf(commentStartExpression, startIndex + commentLength);
	}
}