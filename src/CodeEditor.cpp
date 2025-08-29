#include "CodeEditor.hpp"
#include "Highlighter.hpp"
#include "LineNumberArea.hpp"

#include <QFile>
#include <QPainter>
#include <QTextBlock>
#include <QFileDialog>
#include <QTextStream>
#include <QVBoxLayout>

#define FONT_FAMILY "Monospace"
#define FONT_SIZE 14
#define EVERY_CHAR_SHOULD_HAVE_THE_SAME_SIZE true
#define TAB_SIZE 5

#define EXTRA_SPACE_AT_LEFT 10
#define EXTRA_SPACE_AT_RIGHT 10
#define EXTRA_SPACE_AT_BOTTOM 100

CodeEditor::CodeEditor(QWidget *parent, QString id, QTabWidget *&tabs, QString path, QString code, bool isCpp) : QPlainTextEdit{parent} {
	this->id = id;

	this->tabs = tabs;

	this->path = path;

	QFont font;

	font.setFamily(FONT_FAMILY);

	font.setPixelSize(FONT_SIZE);

	font.setFixedPitch(EVERY_CHAR_SHOULD_HAVE_THE_SAME_SIZE);

	setFont(font);

	setTabStopDistance(fontMetrics().horizontalAdvance(' ') * TAB_SIZE);

	if (!code.isNull() && !code.isEmpty()) {
		setPlainText(code);
	}

	if (isCpp) {
		highlighter->setDocument(document());
	} else {
		firstTextChange = false;
	}

	lineNumberArea = new LineNumberArea(this);

	connect(this, &CodeEditor::blockCountChanged, this, &CodeEditor::updateLineNumberAreaWidth);

	connect(this, &CodeEditor::updateRequest, this, &CodeEditor::updateLineNumberArea);

	connect(this, &CodeEditor::cursorPositionChanged, this, &CodeEditor::highlightCurrentLine);

	connect(this, &CodeEditor::textChanged, this, &CodeEditor::addAsteriskToTabName);

	updateLineNumberAreaWidth(0);

	highlightCurrentLine();
}

void CodeEditor::addAsteriskToTabName() {
	// Toda vez que um conteúdo precisa ser realçado o primeiro realce é considerado uma mudança no código.

	if (firstTextChange) {
		firstTextChange = false;

		return;
	}

	wasSaved = false;

	QString tabName = this->tabs->tabText(this->tabs->currentIndex());

	if (!tabName.endsWith(" *")) {
		this->tabs->setTabText(this->tabs->currentIndex(), tabName.append(" *"));
	}
}

void CodeEditor::save() {
	if (this->path.isNull()) {
		saveAs();

		return;
	}

	if (wasSaved) {
		return;
	}

	QFile file(this->path);

	if (!file.open(QFile::WriteOnly)) {
		return;
	}

	file.write(toPlainText().toStdString().c_str());

	file.close();

	wasSaved = true;

	QString tabName = this->tabs->tabText(this->tabs->currentIndex());

	if (tabName.endsWith(" *")) {
		this->tabs->setTabText(this->tabs->currentIndex(), tabName.replace(" *", ""));
	}
}

void CodeEditor::saveAs() {
	QFileDialog fileDialog;

	fileDialog.setFilter(QDir::NoDotAndDotDot | QDir::Writable);

	QString fileName = fileDialog.getSaveFileName(this, "Save As...", QDir::homePath());

	if (fileName.isNull()) {
		return;
	}

	this->path = fileName;

	QFile file(fileName);

	if (!file.open(QFile::WriteOnly)) {
		return;
	}

	file.write(toPlainText().toStdString().c_str());

	file.close();

	wasSaved = true;

	QString fileName2 = QFileInfo(fileName).fileName();

	this->tabs->setTabText(this->tabs->currentIndex(), fileName2);

	if (fileName2.endsWith(".cpp") || fileName2.endsWith(".hpp")) {
		highlighter->setDocument(document());
	}
}

QString CodeEditor::getPath() {
	return this->path;
}

QString CodeEditor::getID() {
	return this->id;
}

int CodeEditor::lineNumberAreaWidth() {
	int digits = 1;

	int max = qMax(1, blockCount());

	while (max >= 10) {
		max /= 10;

		digits++;
	}

	int space = 3 + fontMetrics().horizontalAdvance(QLatin1Char('9')) * digits;

	return space + EXTRA_SPACE_AT_RIGHT;
}

void CodeEditor::updateLineNumberAreaWidth(int /* newBlockCount */) {
	setViewportMargins(lineNumberAreaWidth() + EXTRA_SPACE_AT_LEFT, 0, 0, EXTRA_SPACE_AT_BOTTOM);
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

			QColor lightGray(127, 138, 149);

			painter.setPen(lightGray);

			painter.drawText(0, top, lineNumberArea->width(), fontMetrics().height(), Qt::AlignRight, number);
		}

		block = block.next();

		top = bottom;

		bottom = top + qRound(blockBoundingRect(block).height());

		blockNumber++;
	}
}