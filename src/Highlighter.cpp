#include <QFile>
#include <QJsonArray>
#include <QJsonObject>
#include <QJsonDocument>
#include <QMessageBox>

#include "Highlighter.hpp"

Highlighter::Highlighter(QTextDocument *parent) : QSyntaxHighlighter(parent) {
	QFile file(":/patterns/cpp.json");

	if (file.open(QFile::ReadOnly | QFile::Text)) {
		QByteArray json = file.readAll();

		file.close();

		QJsonDocument doc = QJsonDocument::fromJson(json);

		if (doc.isArray()) {
			QJsonArray values = doc.array();

			for (QJsonValue value : values) {
				if (value.isObject()) {
					QJsonObject obj = value.toObject();

					if (obj.contains("pattern") && obj.contains("color")) {
						QJsonValue pattern = obj["pattern"];

						if (pattern.isString() && obj["color"].isString()) {
							HighlightingRule rule;

							rule.pattern = QRegularExpression(pattern.toString());

							QTextCharFormat format;

							format.setForeground(QColor(248, 248, 242));

							QString color = obj["color"].toString();

							if (color.compare("red", Qt::CaseSensitivity::CaseInsensitive) == 0) {
								format.setForeground(QColor(255, 85, 85));
							}

							if (color.compare("cyan", Qt::CaseSensitivity::CaseInsensitive) == 0) {
								format.setForeground(QColor(139, 233, 253));
							}

							if (color.compare("green", Qt::CaseSensitivity::CaseInsensitive) == 0) {
								format.setForeground(QColor(80, 250, 123));
							}

							if (color.compare("pink", Qt::CaseSensitivity::CaseInsensitive) == 0) {
								format.setForeground(QColor(255, 121, 198));
							}

							if (color.compare("purple", Qt::CaseSensitivity::CaseInsensitive) == 0) {
								format.setForeground(QColor(255, 121, 198));
							}

							if (color.compare("orange", Qt::CaseSensitivity::CaseInsensitive) == 0) {
								format.setForeground(QColor(255, 184, 108));
							}

							if (color.compare("yellow", Qt::CaseSensitivity::CaseInsensitive) == 0) {
								format.setForeground(QColor(241, 250, 140));
							}

							rule.format = format;

							this->highlightingRules.append(rule);
						}
					}
				}
			}
		}
	}

	// Single line comments

	HighlightingRule rule;

	this->singleLineCommentFormat.setForeground(Qt::lightGray);

	rule.pattern = QRegularExpression(QStringLiteral("//[^\n]*"));

	rule.format = this->singleLineCommentFormat;

	this->highlightingRules.append(rule);

	// Multi line comments

	this->multiLineCommentFormat.setForeground(Qt::lightGray);

	this->commentStartExpression = QRegularExpression(QStringLiteral("/\\*"));

	this->commentEndExpression = QRegularExpression(QStringLiteral("\\*/"));
}

void Highlighter::highlightBlock(const QString &text) {
	for (const HighlightingRule &rule : std::as_const(this->highlightingRules)) {
		QRegularExpressionMatchIterator matchIterator = rule.pattern.globalMatch(text);

		while (matchIterator.hasNext()) {
			QRegularExpressionMatch match = matchIterator.next();

			setFormat(match.capturedStart(), match.capturedLength(), rule.format);
		}
	}

	setCurrentBlockState(0);

	int startIndex = 0;

	if (previousBlockState() != 1) {
		startIndex = text.indexOf(this->commentStartExpression);
	}

	while (startIndex >= 0) {
		QRegularExpressionMatch match = this->commentEndExpression.match(text, startIndex);

		int endIndex = match.capturedStart();

		int commentLength = 0;

		if (endIndex == -1) {
			setCurrentBlockState(1);

			commentLength = text.length() - startIndex;
		} else {
			commentLength = endIndex - startIndex + match.capturedLength();
		}

		setFormat(startIndex, commentLength, this->multiLineCommentFormat);

		startIndex = text.indexOf(this->commentStartExpression, startIndex + commentLength);
	}
}