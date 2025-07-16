#ifndef CODEEDIT_H
#define CODEEDIT_H

#include <QTextEdit>

class CodeEdit : public QTextEdit {
	Q_OBJECT

public:
	CodeEdit(QString code);
};

#endif