#ifndef CODEEDITOR_H
#define CODEEDITOR_H

#include <QPlainTextEdit>
#include "Highlighter.hpp"

class CodeEditor : public QPlainTextEdit {
	Q_OBJECT

	QString path;

	QWidget *lineNumberArea;

	Highlighter *highlighter = new Highlighter(0);

public:
	explicit CodeEditor(QWidget *parent = nullptr, QString path = nullptr, QString code = nullptr);

	void applyCppSyntaxHighlighting();

	QString saveAs();

	bool save();

	QString getPath();

	void lineNumberAreaPaintEvent(QPaintEvent *paintEvent);

	int lineNumberAreaWidth();

protected:
	void resizeEvent(QResizeEvent *resizeEvent) override;

private slots:
	void updateLineNumberAreaWidth(int newBlockCount);

	void highlightCurrentLine();

	void updateLineNumberArea(const QRect &rect, int dy);
};

#endif