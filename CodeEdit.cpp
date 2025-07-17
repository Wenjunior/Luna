#include "CodeEdit.hpp"
#include "LineNumberArea.hpp"

#include <QPainter>
#include <QTextBlock>

CodeEdit::CodeEdit(QWidget *parent, QString code) : QPlainTextEdit(parent) {
	QFont font("Monospace", 10);

	font.setFixedPitch(true);

	setFont(font);

	if (code != nullptr) {
		setPlainText(code);
	}

    lineNumberArea = new LineNumberArea(this);

    connect(this, &CodeEdit::blockCountChanged, this, &CodeEdit::updateLineNumberAreaWidth);

    connect(this, &CodeEdit::updateRequest, this, &CodeEdit::updateLineNumberArea);

    connect(this, &CodeEdit::cursorPositionChanged, this, &CodeEdit::highlightCurrentLine);

    updateLineNumberAreaWidth(0);

    highlightCurrentLine();
}

int CodeEdit::lineNumberAreaWidth() {
    int digits = 1;

    int max = qMax(1, blockCount());

    while (max >= 10) {
        max /= 10;

        digits++;
    }

    int space = 3 + fontMetrics().horizontalAdvance(QLatin1Char('9')) * digits;

    return space;
}

void CodeEdit::updateLineNumberAreaWidth(int /* newBlockCount */) {
    setViewportMargins(lineNumberAreaWidth(), 0, 0, 0);
}

void CodeEdit::updateLineNumberArea(const QRect &rect, int dy) {
    if (dy) {
        lineNumberArea->scroll(0, dy);
    } else {
        lineNumberArea->update(0, rect.y(), lineNumberArea->width(), rect.height());
    }

    if (rect.contains(viewport()->rect())) {
        updateLineNumberAreaWidth(0);
    }
}

void CodeEdit::resizeEvent(QResizeEvent *resizeEvent) {
    QPlainTextEdit::resizeEvent(resizeEvent);

    QRect cr = contentsRect();

    lineNumberArea->setGeometry(QRect(cr.left(), cr.top(), lineNumberAreaWidth(), cr.height()));
}

void CodeEdit::highlightCurrentLine() {
    QList<QTextEdit::ExtraSelection> extraSelections;

    if (!isReadOnly()) {
        QTextEdit::ExtraSelection selection;

        QColor color = QColor(Qt::yellow).lighter(160);

        selection.format.setBackground(color);

        selection.format.setProperty(QTextCharFormat::FullWidthSelection, true);

        selection.cursor = textCursor();

        selection.cursor.clearSelection();

        extraSelections.append(selection);
    }

    setExtraSelections(extraSelections);
}

void CodeEdit::lineNumberAreaPaintEvent(QPaintEvent *paintEvent) {
    QPainter painter(lineNumberArea);

    painter.fillRect(paintEvent->rect(), Qt::lightGray);

    QTextBlock block = firstVisibleBlock();

    int blockNumber = block.blockNumber();

    int top = qRound(blockBoundingGeometry(block).translated(contentOffset()).top());

    int bottom = top + qRound(blockBoundingRect(block).height());

    while (block.isValid() && top <= paintEvent->rect().bottom()) {
        if (block.isVisible() && bottom >= paintEvent->rect().top()) {
            QString number = QString::number(blockNumber + 1);

            painter.setPen(Qt::black);

            painter.drawText(0, top, lineNumberArea->width(), fontMetrics().height(), Qt::AlignRight, number);
        }

        block = block.next();

        top = bottom;

        bottom = top + qRound(blockBoundingRect(block).height());

        blockNumber++;
    }
}