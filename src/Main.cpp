#include "MainWindow.hpp"

#include <QDir>
#include <QFile>
#include <QApplication>

int main(int argc, char *argv[]) {
	QApplication app(argc, argv);

	QFile file(QDir::cleanPath(QDir::currentPath() + QDir::separator() + "css" + QDir::separator() + "dark_theme.css"));

	if (file.open(QFile::ReadOnly | QFile::Text)) {
		QString styleSheet = file.readAll();

		app.setStyleSheet(styleSheet);

		file.close();
	}

	MainWindow mainWindow;

	mainWindow.show();

	return app.exec();
}