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

void MainWindow::newTab(QString tabName, QString path, QString code, Languages language) {
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
			Languages language = PLAIN_TEXT;

			if (file.fileName().endsWith(".cpp") || file.fileName().endsWith(".hpp")) {
				language = CPP;
			}

			QFileInfo fileInfo(file);

			(fileInfo.fileName(), file.fileName(), byteArray, language);
		}
	}
}

void MainWindow::actionPerformed(Actions action) {
	QWidget *currentWidget = this->tabs->currentWidget();

	if (currentWidget == nullptr) {
		return;
	}

	CodeEditor *codeEditor = (CodeEditor *) currentWidget;

	switch (action) {
		case SAVE:
			codeEditor->save();

			break;

		case SAVE_AS:
			codeEditor->saveAs();

			break;

		case UNDO:
			codeEditor->undo();

		case REDO:
			codeEditor->redo();

			break;

		case CUT:
			codeEditor->cut();

			break;

		case COPY:
			codeEditor->copy();

			break;

		case PASTE:
			if (codeEditor->canPaste()) {
				codeEditor->paste();
			}

			break;
		case SELECT_ALL:
			codeEditor->selectAll();

			break;
	}
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
		Languages language = PLAIN_TEXT;

		if (file.fileName().endsWith(".cpp") || file.fileName().endsWith(".hpp")) {
			language = CPP;
		}

		QFileInfo fileInfo(file);

		(fileInfo.fileName(), file.fileName(), byteArray, language);
	}
}

MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent) {
	resize(1280, 720);

	showMaximized();

	fileSystemModel = new QFileSystemModel(this);

	fileSystemModel->setRootPath(QDir::homePath());

	fileSystemModel->setIconProvider(new IconProvider());

	QTreeView *fileExplorer = new QTreeView();

	fileExplorer->setModel(fileSystemModel);

	fileExplorer->setRootIndex(fileSystemModel->index(QDir::homePath()));

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
			fileExplorer->setRootIndex(fileSystemModel->index(folder));
		}
	});

	file->addAction(openFolder);

	QAction *save = new QAction("Save");

	save->setShortcut(QKeySequence::Save);

	connect(save, &QAction::triggered, this, [this]() {
		actionPerformed(SAVE);
	});

	file->addAction(save);

	QAction *saveAs = new QAction("Save As...");

	saveAs->setShortcut(QKeySequence::SaveAs);

	connect(saveAs, &QAction::triggered, this, [this]() {
		actionPerformed(SAVE_AS);
	});

	file->addAction(saveAs);

	QAction *quit = new QAction("Quit");

	quit->setShortcut(QKeySequence::Quit);

	connect(quit, &QAction::triggered, this, &MainWindow::close);

	file->addAction(quit);

	QMenu *edit = new QMenu("Edit");

	menuBar->addMenu(edit);

	QAction *undo = new QAction("Undo");

	undo->setShortcut(QKeySequence::Undo);

	connect(undo, &QAction::triggered, this, [this]() {
		actionPerformed(UNDO);
	});

	edit->addAction(undo);

	QAction *redo = new QAction("Redo");

	redo->setShortcut(QKeySequence::Redo);

	connect(redo, &QAction::triggered, this, [this]() {
		actionPerformed(REDO);
	});

	edit->addAction(redo);

	QAction *cut = new QAction("Cut");

	cut->setShortcut(QKeySequence::Cut);

	connect(cut, &QAction::triggered, this, [this]() {
		actionPerformed(CUT);
	});

	edit->addAction(cut);

	QAction *copy = new QAction("Copy");

	copy->setShortcut(QKeySequence::Copy);

	connect(copy, &QAction::triggered, this, [this]() {
		actionPerformed(COPY);
	});

	edit->addAction(copy);

	QAction *paste = new QAction("Paste");

	paste->setShortcut(QKeySequence::Paste);

	connect(paste, &QAction::triggered, this, [this]() {
		actionPerformed(PASTE);
	});

	edit->addAction(paste);

	QAction *selectAll = new QAction("Select All");

	selectAll->setShortcut(QKeySequence::SelectAll);

	connect(selectAll, &QAction::triggered, this, [this]() {
		actionPerformed(SELECT_ALL);
	});

	edit->addAction(selectAll);

	QAction *replaceAs = new QAction("Replace As...");

	replaceAs->setShortcut(QKeySequence::Replace);

	connect(replaceAs, &QAction::triggered, this, &MainWindow::replaceAs);

	edit->addAction(replaceAs);

	this->tabs = new QTabWidget();

	this->tabs->setMovable(true);

	setCentralWidget(this->tabs);
}