#ifndef CODEEDITOR_H
#define CODEEDITOR_H

#include <QTabWidget>
#include <QPlainTextEdit>
#include "Highlighter.hpp"

class CodeEditor : public QPlainTextEdit {
	Q_OBJECT

	QString id;

	QTabWidget *tabs;

	QString path;

	QWidget *lineNumberArea;

	Highlighter *highlighter = new Highlighter(0);

	bool firstTextChange = true;

	bool wasSaved = true;

public:
	explicit CodeEditor(QWidget *parent, QString id, QTabWidget *&tabs, QString path, QString code, bool isCpp);

	void addAsteriskToTabName();

	void saveAs();

	void save();

	QString getPath();

	QString getID();

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