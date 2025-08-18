#include "MainWindow.hpp"

#include <QDir>
#include <QFile>
#include <QApplication>
#include <QCoreApplication>

int main(int argc, char *argv[]) {
	QApplication app(argc, argv);

	QFile file(":/css/dark theme.css");

	if (file.open(QFile::ReadOnly | QFile::Text)) {
		QString styleSheet = file.readAll();

		app.setStyleSheet(styleSheet);

		file.close();
	}

	MainWindow mainWindow;

	QIcon icon(":/icons/favicon.ico");

	mainWindow.setWindowIcon(icon);

	mainWindow.show();

	return app.exec();
}