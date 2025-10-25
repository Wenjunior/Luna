#ifndef FILEICONPROVIDER_HPP
#define FILEICONPROVIDER_HPP

#include <QAbstractFileIconProvider>

class IconProvider : public QAbstractFileIconProvider {
public:
	virtual QIcon icon(const QFileInfo &info) const override;
};

#endif