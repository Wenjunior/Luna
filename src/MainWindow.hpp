#ifndef MAINWINDOW_HPP
#define MAINWINDOW_HPP

#include <QTabWidget>
#include <QMainWindow>
#include <QFileSystemModel>

#include "Languages.hpp"

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

	void newTab(QString tabName, QString path = nullptr, QString code = nullptr, Languages language = PLAIN_TEXT);

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