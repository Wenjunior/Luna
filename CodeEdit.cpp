#include "CodeEdit.hpp"

CodeEdit::CodeEdit(QString code) {
	QFont font("Monospace", 10);

	font.setFixedPitch(true);

	setFont(font);

	if (code != nullptr) {
		setPlainText(code);
	}
}