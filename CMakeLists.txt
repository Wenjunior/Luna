cmake_minimum_required(VERSION 3.19)
project(Luna LANGUAGES CXX)

find_package(Qt6 6.5 REQUIRED COMPONENTS Core Widgets)

qt_standard_project_setup()

qt_add_executable(Luna
	WIN32 MACOSX_BUNDLE
	src/Main.cpp
	src/MainWindow.hpp src/MainWindow.cpp
	src/CodeEditor.hpp src/CodeEditor.cpp
	src/LineNumberArea.hpp
	src/Highlighter.hpp src/Highlighter.cpp
)

target_link_libraries(Luna
	PRIVATE
		Qt::Core
		Qt::Widgets
)

include(GNUInstallDirs)

install(TARGETS Luna
	BUNDLE  DESTINATION .
	RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR}
	LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
)

qt_generate_deploy_app_script(
	TARGET Luna
	OUTPUT_SCRIPT deploy_script
	NO_UNSUPPORTED_PLATFORM_ERROR
)

install(SCRIPT ${deploy_script})
