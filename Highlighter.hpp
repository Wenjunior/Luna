#ifndef HIGHLIGHTER_H
#define HIGHLIGHTER_H

#include <QRegularExpression>
#include <QSyntaxHighlighter>

class Highlighter : public QSyntaxHighlighter {
	Q_OBJECT

public:
	Highlighter(QTextDocument *parent = nullptr);

protected:
	void highlightBlock(const QString &code) override;

private:
	struct HighlightingRule {
		QRegularExpression pattern;

		QTextCharFormat format;
	};

	QList<HighlightingRule> highlightingRules;

	QTextCharFormat singleLineCommentFormat;

	QTextCharFormat multiLineCommentFormat;

	QRegularExpression commentStartExpression;

	QRegularExpression commentEndExpression;
};

#endif