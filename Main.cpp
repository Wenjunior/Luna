#include "MainWindow.hpp"

#include <QApplication>

int main(int argc, char *argv[]) {
	QApplication app(argc, argv);

	// TODO: Adicionar um tema escuro.

	MainWindow mainWindow;

	mainWindow.show();

	return app.exec();
}