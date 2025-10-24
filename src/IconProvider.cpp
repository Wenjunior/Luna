#include "IconProvider.hpp"

QIcon IconProvider::icon(const QFileInfo &info) const {
	if (info.isDir()) {
		return QIcon(":/icons/folder.png");
	}

	return QIcon(":/icons/file.png");
}