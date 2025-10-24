#ifndef FILEICONPROVIDER_H
#define FILEICONPROVIDER_H

#include <QAbstractFileIconProvider>

class IconProvider : public QAbstractFileIconProvider {
public:
	virtual QIcon icon(const QFileInfo &info) const override;
};

#endif