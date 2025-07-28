#ifndef CODEEDITOR_H
#define CODEEDITOR_H

#include <QPlainTextEdit>

class CodeEditor : public QPlainTextEdit {
	Q_OBJECT

	QString path;

	QWidget *lineNumberArea;

public:
	explicit CodeEditor(QWidget *parent = nullptr, QString path = nullptr, QString code = nullptr);

	QString saveAs();

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