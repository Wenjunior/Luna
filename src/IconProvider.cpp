#include "IconProvider.hpp"

QIcon IconProvider::icon(const QFileInfo &info) const {
	if (info.isDir()) {
		return QIcon(":/icons/folder.svg");
	}

	return QIcon(":/icons/file.svg");
}