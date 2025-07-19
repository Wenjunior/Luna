#include "MainWindow.hpp"

#include <QFile>
#include <QApplication>

int main(int argc, char *argv[]) {
	QApplication app(argc, argv);

	QFile file("./css/dark_theme.css");

	if (file.open(QFile::ReadOnly | QFile::Text)) {
		QString styleSheet = file.readAll();

		app.setStyleSheet(styleSheet);

		file.close();
	}

	MainWindow mainWindow;

	mainWindow.show();

	return app.exec();
}