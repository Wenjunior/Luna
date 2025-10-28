#ifndef MAINWINDOW_HPP
#define MAINWINDOW_HPP

#include <QTabWidget>
#include <QMainWindow>
#include <QFileSystemModel>

#include "Languages.hpp"

class MainWindow : public QMainWindow {
	Q_OBJECT

	QTabWidget *tabs;

	QFileSystemModel *fileSystemModel;

	void newTab(QString tabName, QString path = nullptr, QString code = nullptr);

	void newFile();

	int findTabIndexWithPath(QString path);

	void openFile();

	void save();

	void saveAs();

	void undo();

	void redo();

	void cut();

	void copy();

	void paste();

	void selectAll();

	void replaceAs();

	void openFileFromExplorer(const QModelIndex &index);

public:
	MainWindow(QWidget *parent = nullptr);
};

#endif