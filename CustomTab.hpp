#ifndef CUSTOMTAB_H
#define CUSTOMTAB_H

#include "CodeEdit.hpp"

#include <QWidget>

class CustomTab : public QWidget {
	Q_OBJECT

	QString path;

	CodeEdit *codeEdit;

public:
	explicit CustomTab(QWidget *parent = nullptr, QString path = nullptr, QString code = nullptr);

	void saveAs();

	void save();

	void undo();

	void redo();

	void cut();

	void copy();

	void paste();

	void selectAll();

	QString getPath();
};

#endif