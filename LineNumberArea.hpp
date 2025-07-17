#ifndef LINENUMBERAREA
#define LINENUMBERAREA

class LineNumberArea : public QWidget {
    CodeEdit *codeEditor;

public:
    LineNumberArea(CodeEdit *codeEdit) : QWidget(codeEdit), codeEditor(codeEdit) {}

    QSize sizeHint() const override {
        return QSize(codeEditor->lineNumberAreaWidth(), 0);
    }

protected:
    void paintEvent(QPaintEvent *paintEvent) override {
        codeEditor->lineNumberAreaPaintEvent(paintEvent);
    }
};

#endif