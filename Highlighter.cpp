#include "Highlighter.hpp"

Highlighter::Highlighter(QTextDocument *parent) : QSyntaxHighlighter(parent) {
	HighlightingRule rule;

	QTextCharFormat keywordFormat;

	QColor red(255, 85, 85);

	keywordFormat.setForeground(red);

	keywordFormat.setFontWeight(QFont::Bold);

	rule.pattern = QRegularExpression(QStringLiteral("\\balignas\\b|\\balignof\\b|\\band\\b|\\band_eq\\b|\\basm\\b|\\batomic_cancel\\b|\\batomic_commit\\b|\\batomic_noexcept\\b|\\bauto\\b|\\bbitand\\b|\\bbitor\\b|\\bbool\\b|\\bbreak\\b|\\bcase\\b|\\bcatch\\b|\\bchar\\b|\\bchar8_t\\b|\\bchar16_t\\b|\\bchar32_t\\b|\\bclass\\b|\\bcompl\\b|\\bconcept\\b|\\bconst\\b|\\bconsteval\\b|\\bconstexpr\\b|\\bconstinit\\b|\\bconst_cast\\b|\\bcontinue\\b|\\bcontract_assert\\b|\\bco_await\\b|\\bco_return\\b|\\bco_yield\\b|\\bdecltype\\b|\\bdefault\\b|\\bdelete\\b|\\bdo\\b|\\bdouble\\b|\\bdynamic_cast\\b|\\belse\\b|\\benum\\b|\\bexplicit\\b|\\bexport\\b|\\bextern\\b|\\bfalse\\b|\\bfloat\\b|\\bforv\\b|\\bfriend\\b|\\bgoto\\b|\\bif\\b|\\binline\\b|\\bint\\b|\\blong\\b|\\bmutable\\b|\\bnamespace\\b|\\bnew\\b|\\bnoexcept\\b|\\bnot\\b|\\bnot_eq\\b|\\bnullptrv\\b|\\boperator\\b|\\bor\\b|\\bor_eq\\b|\\bprivate\\b|\\bprotected\\b|\\bpublic\\b|\\breflexpr\\b|\\bregister\\b|\\breinterpret_cast\\b|\\brequires\\b|\\breturn\\b|\\bshort\\b|\\bsigned\\b|\\bsizeof\\b|\\bstatic\\b|\\bstatic_assert\\b|\\bstatic_cast\\b|\\bstruct\\b|\\bswitch\\b|\\bsynchronized\\b|\\btemplate\\b|\\bthis\\b|\\bthread_local\\b|\\bthrow\\b|\\btrue\\b|\\btry\\b|\\btypedef\\b|\\btypeid\\b|\\btypename\\b|\\bunion\\b|\\bunsigned\\b|\\busing\\b|\\bvirtual\\b|\\bvoid\\b|\\bvolatile\\b|\\bwchar_t\\b|\\bwhile\\b|\\bxor\\b|\\bxor_eq\\b"));

	rule.format = keywordFormat;

	highlightingRules.append(rule);

	QTextCharFormat classFormat;

	QColor purple(189, 147, 249);

	classFormat.setForeground(purple);

	classFormat.setFontWeight(QFont::Bold);

	rule.pattern = QRegularExpression(QStringLiteral("\\bQ[A-Za-z]+\\b"));

	rule.format = classFormat;

	highlightingRules.append(rule);

	QTextCharFormat quotationFormat;

	QColor yellow(241, 250, 140);

	quotationFormat.setForeground(yellow);

	rule.pattern = QRegularExpression(QStringLiteral("\".*\""));

	rule.format = quotationFormat;

	highlightingRules.append(rule);

	QTextCharFormat functionFormat;

	QColor green(80, 250, 123);

	functionFormat.setForeground(green);

	rule.pattern = QRegularExpression(QStringLiteral("\\b[A-Za-z0-9_]+(?=\\()"));

	rule.format = functionFormat;

	highlightingRules.append(rule);

	QTextCharFormat singleLineCommentFormat;

	singleLineCommentFormat.setForeground(Qt::lightGray);

	rule.pattern = QRegularExpression(QStringLiteral("//[^\n]*"));

	rule.format = singleLineCommentFormat;

	highlightingRules.append(rule);

	multiLineCommentFormat.setForeground(Qt::lightGray);

	commentStartExpression = QRegularExpression(QStringLiteral("/\\*"));

	commentEndExpression = QRegularExpression(QStringLiteral("\\*/"));
}

void Highlighter::highlightBlock(const QString &code) {
	for (const HighlightingRule &rule : std::as_const(highlightingRules)) {
		QRegularExpressionMatchIterator matchIterator = rule.pattern.globalMatch(code);

		while (matchIterator.hasNext()) {
			QRegularExpressionMatch match = matchIterator.next();

			setFormat(match.capturedStart(), match.capturedLength(), rule.format);
		}
	}

	setCurrentBlockState(0);

	int startIndex = 0;

	if (previousBlockState() != 1) {
		startIndex = code.indexOf(commentStartExpression);

		while (startIndex >= 0) {
			QRegularExpressionMatch match = commentEndExpression.match(code, startIndex);

			int endIndex = match.capturedStart();

			int commentLength = 0;

			if (endIndex == -1) {
				setCurrentBlockState(1);

				commentLength = code.length() - startIndex;
			} else {
				commentLength = endIndex - startIndex + match.capturedLength();
			}

			setFormat(startIndex, commentLength, multiLineCommentFormat);

			startIndex = code.indexOf(commentStartExpression, startIndex + commentLength);
		}
	}
}