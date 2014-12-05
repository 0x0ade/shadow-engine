#!/bin/bash
BIN=./shadow-platformer-desktop/bin:./shadow-platformer/bin

DIR_GDX=~/libjars/libgdx/
GDX=${DIR_GDX}gdx.jar:${DIR_GDX}gdx-natives.jar:${DIR_GDX}gdx-backend-jglfw.jar:${DIR_GDX}gdx-backend-jglfw-natives.jar:${DIR_GDX}gdx-backend-lwjgl.jar:${DIR_GDX}gdx-backend-lwjgl-natives.jar

DIR_GDX_CONTROLLERS=${DIR_GDX}extensions/gdx-controllers/
GDX_CONTROLLERS=${DIR_GDX_CONTROLLERS}gdx-controllers.jar:${DIR_GDX_CONTROLLERS}gdx-controllers-desktop.jar:${DIR_GDX_CONTROLLERS}gdx-controllers-desktop-natives.jar

DIR_GDX_FREETYPE=${DIR_GDX}extensions/gdx-freetype/
GDX_FREETYPE=${DIR_GDX_FREETYPE}gdx-freetype.jar:${DIR_GDX_FREETYPE}gdx-freetype-natives.jar

DIR_GDX_BOX2D=${DIR_GDX}extensions/gdx-box2d/
GDX_BOX2D=${DIR_GDX_BOX2D}gdx-box2d.jar:${DIR_GDX_BOX2D}gdx-box2d-natives.jar

KRYONET=./shadow-platformer/libs/kryonet-2.18-all.jar

java -classpath ${BIN}:${GDX}:${GDX_CONTROLLERS}:${GDX_FREETYPE}:${GDX_BOX2D}:${KRYONET} net.fourbytes.shadow.Main jglfw
