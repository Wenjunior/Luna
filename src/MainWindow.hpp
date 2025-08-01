#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QTabWidget>
#include <QMainWindow>

class MainWindow : public QMainWindow {
	Q_OBJECT

	QTabWidget *tabs;

public:
	MainWindow(QWidget *parent = nullptr);

	void removeTab(int index);

	void newFile();

	void openFile();

	void saveAs();

	void save();

	void quit();

	void undo();

	void redo();

	void cut();

	void copy();

	void paste();

	void selectAll();

	void replaceAs();
};

#endif