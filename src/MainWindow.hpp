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

	void newFile();

	void openFile();

	void actionPerformed(Actions action);

	void replaceAs();

	void removeTab(int index);

	void openFileFromExplorer(const QModelIndex &index);

public:
	MainWindow(QWidget *parent = nullptr);
};

#endif