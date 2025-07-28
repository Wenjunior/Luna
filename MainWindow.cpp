#include "CodeEditor.hpp"
#include "MainWindow.hpp"

#include <QDir>
#include <QMenu>
#include <QMenuBar>
#include <QFileDialog>

void MainWindow::removeTab(int index) {
	this->tabs->removeTab(index);
}

void MainWindow::newFile() {
	CodeEditor *codeEditor = new CodeEditor();

	this->tabs->addTab(codeEditor, "Untitled");

	this->tabs->setCurrentIndex(this->tabs->count() - 1);
}

void MainWindow::openFile() {
	QFileDialog fileDialog;

	// TODO: Listar primeiro os diretórios e depois os arquivos.

	fileDialog.setFilter(QDir::NoDotAndDotDot | QDir::Readable | QDir::Writable);

	QStringList fileNames = fileDialog.getOpenFileNames(this, "Open File...", QDir::homePath());

	for (QString fileName : fileNames) {
		for (int index = 0; index < this->tabs->count(); index++) {
			QWidget *tab = (QWidget *) this->tabs->widget(index);

			CodeEditor *codeEditor = (CodeEditor *) tab;

			if (fileName.compare(codeEditor->getPath(), Qt::CaseSensitive) == 0) {
				this->tabs->setCurrentWidget(tab);

				return;
			}
		}

		QFile file(fileName);

		file.open(QFile::ReadOnly);

		QByteArray byteArray = file.readAll();

		file.close();

		if (byteArray.isValidUtf8()) {
			CodeEditor *codeEditor = new CodeEditor(this, file.fileName(), byteArray);

			QFileInfo fileInfo(file);

			this->tabs->addTab(codeEditor, fileInfo.fileName());

			/*
				Colocar a linha de código a seguir aqui evita que a última tab seja selecionada caso o arquivo não seja UTF-8.
				E se você abrir vários arquivos grandes, a última tab será selecionada de acordo com a ordem em que eles forem abertos.
			*/

			this->tabs->setCurrentIndex(this->tabs->count() - 1);
		}
	}
}

void MainWindow::saveAs() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	QString fileName = codeEditor->saveAs();

	if (fileName.isNull()) {
		return;
	}

	this->tabs->setTabText(this->tabs->currentIndex(), fileName);
}

void MainWindow::save() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	codeEditor->save();
}

void MainWindow::quit() {
	close();
}

void MainWindow::undo() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	codeEditor->undo();
}

void MainWindow::redo() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	codeEditor->redo();
}

void MainWindow::cut() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	codeEditor->cut();
}

void MainWindow::copy() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	codeEditor->copy();
}

void MainWindow::paste() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	if (codeEditor->canPaste()) {
		codeEditor->paste();
	}
}

void MainWindow::selectAll() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	codeEditor->selectAll();
}

MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent) {
	resize(1280, 720);

	showMaximized();

	this->tabs = new QTabWidget();

	this->tabs->setTabsClosable(true);

	connect(this->tabs, &QTabWidget::tabCloseRequested, this, &MainWindow::removeTab);

	setCentralWidget(this->tabs);

	QMenuBar *menuBar = this->menuBar();

	QMenu *file = new QMenu("File");

	menuBar->addMenu(file);

	QAction *newFile = new QAction("New File");

	newFile->setShortcut(QKeySequence::New);

	connect(newFile, &QAction::triggered, this, &MainWindow::newFile);

	file->addAction(newFile);

	QAction *openFile = new QAction("Open File...");

	openFile->setShortcut(QKeySequence::Open);

	connect(openFile, &QAction::triggered, this, &MainWindow::openFile);

	file->addAction(openFile);

	QAction *save = new QAction("Save");

	save->setShortcut(QKeySequence::Save);

	connect(save, &QAction::triggered, this, &MainWindow::save);

	file->addAction(save);

	QAction *saveAs = new QAction("Save As...");

	saveAs->setShortcut(QKeySequence::SaveAs);

	connect(saveAs, &QAction::triggered, this, &MainWindow::saveAs);

	file->addAction(saveAs);

	QAction *quit = new QAction("Quit");

	quit->setShortcut(QKeySequence::Quit);

	connect(quit, &QAction::triggered, this, &MainWindow::quit);

	file->addAction(quit);

	QMenu *edit = new QMenu("Edit");

	menuBar->addMenu(edit);

	QAction *undo = new QAction("Undo");

	undo->setShortcut(QKeySequence::Undo);

	connect(undo, &QAction::triggered, this, &MainWindow::undo);

	edit->addAction(undo);

	QAction *redo = new QAction("Redo");

	redo->setShortcut(QKeySequence::Redo);

	connect(redo, &QAction::triggered, this, &MainWindow::redo);

	edit->addAction(redo);

	QAction *cut = new QAction("Cut");

	cut->setShortcut(QKeySequence::Cut);

	connect(cut, &QAction::triggered, this, &MainWindow::cut);

	edit->addAction(cut);

	QAction *copy = new QAction("Copy");

	copy->setShortcut(QKeySequence::Copy);

	connect(copy, &QAction::triggered, this, &MainWindow::copy);

	edit->addAction(copy);

	QAction *paste = new QAction("Paste");

	paste->setShortcut(QKeySequence::Paste);

	connect(paste, &QAction::triggered, this, &MainWindow::paste);

	edit->addAction(paste);

	QAction *selectAll = new QAction("Select All");

	selectAll->setShortcut(QKeySequence::SelectAll);

	connect(selectAll, &QAction::triggered, this, &MainWindow::selectAll);

	edit->addAction(selectAll);
}