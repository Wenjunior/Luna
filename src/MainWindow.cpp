#include "CodeEditor.hpp"
#include "MainWindow.hpp"
#include "IconProvider.hpp"

#include <QDir>
#include <QMenu>
#include <QLabel>
#include <QMenuBar>
#include <QTreeView>
#include <filesystem>
#include <QDockWidget>
#include <QFileDialog>
#include <QFormLayout>
#include <QHeaderView>
#include <QPushButton>
#include <QInputDialog>
#include <QDialogButtonBox>

namespace fs = std::filesystem;

void MainWindow::newTab(QString tabName, QString path, QString code) {
	Languages language = PLAIN_TEXT;

	if (tabName.endsWith(".cpp") || tabName.endsWith(".hpp")) {
		language = CPP;
	}

	CodeEditor *codeEditor = new CodeEditor(this, this->tabs, path, code, language);

	this->tabs->addTab(codeEditor, tabName);

	QTabBar *tabBar = this->tabs->tabBar();

	QPushButton *closeButton = new QPushButton();

	closeButton->setIcon(QIcon(":/icons/close button.svg"));

	closeButton->setFlat(true);

	int tabIndex = this->tabs->count() - 1;

	tabBar->setTabButton(tabIndex, QTabBar::RightSide, closeButton);

	connect(closeButton, &QPushButton::clicked, this, [this, closeButton]() {
		for (int index = 0; index < this->tabs->count(); index++) {
			if (this->tabs->tabBar()->tabButton(index, QTabBar::RightSide) == closeButton) {
				this->tabs->removeTab(index);

				break;
			}
		}
	});

	this->tabs->setCurrentIndex(tabIndex);
}

void MainWindow::newFile() {
	newTab("Untitled");
}

int MainWindow::findTabIndexWithPath(QString path) {
	for (int index = 0; index < this->tabs->count(); index++) {
		QWidget *tab = (QWidget *) this->tabs->widget(index);

		CodeEditor *codeEditor = (CodeEditor *) tab;

		if (path.compare(codeEditor->getPath(), Qt::CaseSensitive) == 0) {
			return index;
		}
	}

	return -1;
}

void MainWindow::openFile() {
	QFileDialog fileDialog;

	fileDialog.setFilter(QDir::NoDotAndDotDot | QDir::Readable | QDir::Writable);

	QStringList fileNames = fileDialog.getOpenFileNames(this, "Open File...", QDir::homePath());

	for (QString fileName : fileNames) {
		int tabIndex = findTabIndexWithPath(fileName);

		if (tabIndex != -1) {
			this->tabs->setCurrentIndex(tabIndex);

			return;
		}

		QFile file(fileName);

		file.open(QFile::ReadOnly);

		QByteArray byteArray = file.readAll();

		file.close();

		if (byteArray.isValidUtf8()) {
			QFileInfo fileInfo(file);

			newTab(fileInfo.fileName(), file.fileName(), byteArray);
		}
	}
}

void MainWindow::save() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	codeEditor->save();
}

void MainWindow::saveAs() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	codeEditor->saveAs();
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

void MainWindow::replaceAs() {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	QDialog dialog(this);

	QFormLayout formLayout(&dialog);

	QLineEdit *find = new QLineEdit();

	formLayout.addRow(new QLabel("Find: "), find);

	QLineEdit *replaceBy = new QLineEdit();

	formLayout.addRow(new QLabel("Replace By: "), replaceBy);

	QDialogButtonBox *buttonBox = new QDialogButtonBox(QDialogButtonBox::Ok | QDialogButtonBox::Cancel, Qt::Horizontal, this);

	connect(buttonBox, &QDialogButtonBox::accepted, this, [currentWidget, find, replaceBy, &dialog]() {
		CodeEditor *codeEditor = (CodeEditor *) currentWidget;

		codeEditor->setPlainText(codeEditor->toPlainText().replace(find->text(), replaceBy->text()));

		dialog.accept();
	});

	connect(buttonBox, &QDialogButtonBox::rejected, this, [currentWidget, find, replaceBy, &dialog]() {
		dialog.reject();
	});

	formLayout.addRow(buttonBox);

	dialog.exec();
}

void MainWindow::openFileFromExplorer(const QModelIndex &index) {
	QString filePath = this->fileSystemModel->filePath(index);

	if (fs::is_directory(fs::path(filePath.toStdString()))) {
		return;
	}

	int tabIndex = findTabIndexWithPath(filePath);

	if (tabIndex != -1) {
		this->tabs->setCurrentIndex(tabIndex);

		return;
	}

	QFile file(filePath);

	file.open(QFile::ReadOnly);

	QByteArray byteArray = file.readAll();

	file.close();

	if (byteArray.isValidUtf8()) {
		QFileInfo fileInfo(file);

		newTab(fileInfo.fileName(), file.fileName(), byteArray);
	}
}

MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent) {
	resize(1280, 720);

	showMaximized();

	this->fileSystemModel = new QFileSystemModel(this);

	this->fileSystemModel->setRootPath(QDir::homePath());

	this->fileSystemModel->setIconProvider(new IconProvider());

	QTreeView *fileExplorer = new QTreeView();

	fileExplorer->setModel(this->fileSystemModel);

	fileExplorer->setRootIndex(this->fileSystemModel->index(QDir::homePath()));

	fileExplorer->setHeaderHidden(true);

	for (int i = 1; i < fileExplorer->model()->columnCount(); i++) {
		fileExplorer->header()->hideSection(i);
	}

	connect(fileExplorer, &QTreeView::doubleClicked, this, &MainWindow::openFileFromExplorer);

	fileExplorer->show();

	QDockWidget *fileExplorerDockWidget = new QDockWidget(nullptr, this);

	fileExplorerDockWidget->setFeatures(QDockWidget::NoDockWidgetFeatures);

	fileExplorerDockWidget->setWidget(fileExplorer);

	addDockWidget(Qt::LeftDockWidgetArea, fileExplorerDockWidget);

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

	QAction *openFolder = new QAction("Open Folder...");

	openFolder->setShortcut(QKeySequence("Ctrl+Shift+O"));

	connect(openFolder, &QAction::triggered, this, [this, fileExplorer]() {
		QString folder = QFileDialog::getExistingDirectory(nullptr, "Open Folder...", QDir::homePath(), QFileDialog::ShowDirsOnly);

		if (!folder.isEmpty() && !folder.isNull()) {
			fileExplorer->setRootIndex(this->fileSystemModel->index(folder));
		}
	});

	file->addAction(openFolder);

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

	connect(quit, &QAction::triggered, this, &MainWindow::close);

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

	QAction *replaceAs = new QAction("Replace As...");

	replaceAs->setShortcut(QKeySequence::Replace);

	connect(replaceAs, &QAction::triggered, this, &MainWindow::replaceAs);

	edit->addAction(replaceAs);

	this->tabs = new QTabWidget();

	this->tabs->setMovable(true);

	setCentralWidget(this->tabs);
}