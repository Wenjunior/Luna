#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QTabWidget>
#include <QMainWindow>
#include <QFileSystemModel>

enum Actions {
	SAVE,
	SAVE_AS,
	UNDO,
	REDO,
	CUT,
	COPY,
	PASTE,
	SELECT_ALL
};

class MainWindow : public QMainWindow {
	Q_OBJECT

	QTabWidget *tabs;

	QFileSystemModel *fileSystemModel;

	void newTab(QString tabName, QString path = nullptr, QString code = nullptr, bool isCpp = false);

	void newFile();

	int findTabIndexWithPath(QString path);

	void openFile();

	void actionPerformed(Actions action);

	void replaceAs();

	void openFileFromExplorer(const QModelIndex &index);

public:
	MainWindow(QWidget *parent = nullptr);
};

#endif