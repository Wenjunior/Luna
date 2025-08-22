#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QTabWidget>
#include <QMainWindow>
#include <QFileSystemModel>

class MainWindow : public QMainWindow {
	Q_OBJECT

	QTabWidget *tabs;

	QFileSystemModel *fileSystemModel;

	void newFile();

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

	void removeTab(int index);

	void openFileFromExplorer(const QModelIndex &index);

public:
	MainWindow(QWidget *parent = nullptr);
};

#endif