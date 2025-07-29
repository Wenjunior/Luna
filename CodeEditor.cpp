#include "CodeEditor.hpp"
#include "Highlighter.hpp"
#include "LineNumberArea.hpp"

#include <QFile>
#include <QPainter>
#include <QTextBlock>
#include <QFileDialog>
#include <QTextStream>
#include <QVBoxLayout>

CodeEditor::CodeEditor(QWidget *parent, QString path, QString code) : QPlainTextEdit{parent} {
	this->path = path;

	QFont font("Monospace", 10);

	font.setFixedPitch(true);

	setFont(font);

	setWordWrapMode(QTextOption::NoWrap);

	if (code != nullptr) {
		setPlainText(code);
	}

	lineNumberArea = new LineNumberArea(this);

	connect(this, &CodeEditor::blockCountChanged, this, &CodeEditor::updateLineNumberAreaWidth);

	connect(this, &CodeEditor::updateRequest, this, &CodeEditor::updateLineNumberArea);

	connect(this, &CodeEditor::cursorPositionChanged, this, &CodeEditor::highlightCurrentLine);

	updateLineNumberAreaWidth(0);

	highlightCurrentLine();

	new Highlighter(document());
}

void CodeEditor::save() {
	if (this->path.isNull()) {
		saveAs();

		return;
	}

	QFile file(this->path);

	if (file.open(QFile::WriteOnly)) {
		file.write(toPlainText().toStdString().c_str());
	}

	file.close();
}

QString CodeEditor::saveAs() {
	QFileDialog fileDialog;

	fileDialog.setFilter(QDir::NoDotAndDotDot | QDir::Writable);

	QString fileName = fileDialog.getSaveFileName(this, "Save As...", QDir::homePath());

	if (fileName.isNull()) {
		return nullptr;
	}

	this->path = fileName;

	QFile file(fileName);

	if (file.open(QFile::WriteOnly)) {
		file.write(toPlainText().toStdString().c_str());
	}

	file.close();

	return QFileInfo(fileName).fileName();
}

QString CodeEditor::getPath() {
	return this->path;
}

int CodeEditor::lineNumberAreaWidth() {
	int digits = 1;

	int max = qMax(1, blockCount());

	while (max >= 10) {
		max /= 10;

		digits++;
	}

	int space = 3 + fontMetrics().horizontalAdvance(QLatin1Char('9')) * digits;

	return space;
}

void CodeEditor::updateLineNumberAreaWidth(int /* newBlockCount */) {
	setViewportMargins(lineNumberAreaWidth(), 0, 0, 0);
}

void CodeEditor::updateLineNumberArea(const QRect &rect, int dy) {
	if (dy) {
		lineNumberArea->scroll(0, dy);
	} else {
		lineNumberArea->update(0, rect.y(), lineNumberArea->width(), rect.height());
	}

	if (rect.contains(viewport()->rect())) {
		updateLineNumberAreaWidth(0);
	}
}

void CodeEditor::resizeEvent(QResizeEvent *resizeEvent) {
	QPlainTextEdit::resizeEvent(resizeEvent);

	QRect cRect = contentsRect();

	lineNumberArea->setGeometry(QRect(cRect.left(), cRect.top(), lineNumberAreaWidth(), cRect.height()));
}

void CodeEditor::highlightCurrentLine() {
	QList<QTextEdit::ExtraSelection> extraSelections;

	if (!isReadOnly()) {
		QTextEdit::ExtraSelection selection;

		QColor lineColor = QColor(Qt::lightGray);

		lineColor.setAlpha(20);

		selection.format.setBackground(lineColor);

		selection.format.setProperty(QTextCharFormat::FullWidthSelection, true);

		selection.cursor = textCursor();

		selection.cursor.clearSelection();

		extraSelections.append(selection);
	}

	setExtraSelections(extraSelections);
}

void CodeEditor::lineNumberAreaPaintEvent(QPaintEvent *paintEvent) {
	QPainter painter(lineNumberArea);

	painter.setFont(font());

	QTextBlock block = firstVisibleBlock();

	int blockNumber = block.blockNumber();

	int top = qRound(blockBoundingGeometry(block).translated(contentOffset()).top());

	int bottom = top + qRound(blockBoundingRect(block).height());

	while (block.isValid() && top <= paintEvent->rect().bottom()) {
		if (block.isVisible() && bottom >= paintEvent->rect().top()) {
			QString number = QString::number(blockNumber + 1);

			painter.setPen(Qt::lightGray);

			painter.drawText(0, top, lineNumberArea->width(), fontMetrics().height(), Qt::AlignRight, number);
		}

		block = block.next();

		top = bottom;

		bottom = top + qRound(blockBoundingRect(block).height());

		blockNumber++;
	}
}