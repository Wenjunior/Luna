#ifndef CODEEDIT_H
#define CODEEDIT_H

#include <QPlainTextEdit>

class CodeEdit : public QPlainTextEdit {
	Q_OBJECT

    QWidget *lineNumberArea;

public:
    CodeEdit(QWidget *parent, QString code = nullptr);

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