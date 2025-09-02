#ifndef CODEEDITOR_H
#define CODEEDITOR_H

#include <QTabWidget>
#include <QPlainTextEdit>
#include "Highlighter.hpp"

class CodeEditor : public QPlainTextEdit {
	Q_OBJECT

	QTabWidget *tabs;

	QString path;

	QWidget *lineNumberArea;

	Highlighter *highlighter = new Highlighter(0);

	bool firstTextChange = true;

	bool wasSaved = true;

public:
	explicit CodeEditor(QWidget *parent, QTabWidget *&tabs, QString path, QString code, bool applyCppSyntaxHighlighting);

	void changeTabName();

	void save();

	void saveAs();

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
