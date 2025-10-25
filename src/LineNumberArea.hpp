#ifndef LINENUMBERAREA_HPP
#define LINENUMBERAREA_HPP

class LineNumberArea : public QWidget {
	CodeEditor *codeEditor;

public:
	LineNumberArea(CodeEditor *codeEdit) : QWidget(codeEdit), codeEditor(codeEdit) {}

	QSize sizeHint() const override {
		return QSize(codeEditor->lineNumberAreaWidth(), 0);
	}

protected:
	void paintEvent(QPaintEvent *paintEvent) override {
		codeEditor->lineNumberAreaPaintEvent(paintEvent);
	}
};

#endif