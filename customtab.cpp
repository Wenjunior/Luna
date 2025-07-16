#include "customtab.h"

#include <QFile>
#include <QFileDialog>
#include <QTextStream>
#include <QVBoxLayout>

CustomTab::CustomTab(QWidget *parent, QString path, QString code) : QWidget{parent} {
	this->path = path;

	QVBoxLayout *vbox = new QVBoxLayout();

	codeEdit = new CodeEdit(code);

	vbox->addWidget(codeEdit);

	setLayout(vbox);
}

void CustomTab::save() {
	if (this->path.isNull()) {
		saveAs();

		return;
	}

	QFile file(this->path);

	if (file.open(QFile::WriteOnly)) {
		file.write(this->codeEdit->toPlainText().toStdString().c_str());
	}

	file.close();
}

void CustomTab::saveAs() {
	QFileDialog fileDialog;

	fileDialog.setFilter(QDir::NoDotAndDotDot | QDir::Writable);

	QString fileName = fileDialog.getSaveFileName(this, "Save As...", QDir::homePath());

	if (fileName.isNull()) {
		return;
	}

	this->path = fileName;

	QFile file(fileName);

	if (file.open(QFile::WriteOnly)) {
		file.write(this->codeEdit->toPlainText().toStdString().c_str());
	}

	file.close();
}

void CustomTab::undo() {
	codeEdit->undo();
}

void CustomTab::redo() {
	codeEdit->redo();
}

void CustomTab::cut() {
	codeEdit->cut();
}

void CustomTab::copy() {
	codeEdit->copy();
}

void CustomTab::paste() {
	if (codeEdit->canPaste()) {
		codeEdit->paste();
	}
}

void CustomTab::selectAll() {
	codeEdit->selectAll();
}

QString CustomTab::getPath() {
	return this->path;
}