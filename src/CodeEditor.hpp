#ifndef CODEEDITOR_H
#define CODEEDITOR_H

#include <QTabWidget>
#include <QPlainTextEdit>
#include "Highlighter.hpp"

class CodeEditor : public QPlainTextEdit {
	Q_OBJECT

	QString path;

	QTabWidget *tabs;

	QWidget *lineNumberArea;

	Highlighter *highlighter = new Highlighter(0);

	bool wasSaved = true;

public:
	explicit CodeEditor(QWidget *parent, QTabWidget *&tabs, QString path = nullptr, QString code = nullptr);

	void applyCppSyntaxHighlighting();

	void addAsteriskToTabName();

	void saveAs();

	void save();

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