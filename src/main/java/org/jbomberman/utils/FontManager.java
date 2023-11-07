package org.jbomberman.utils;

import javafx.scene.text.Font;

public class FontManager {

    private static final String FONT_PATH = "/resources/SfComicScriptBold-YXD2.ttf";

    public static Font loadCustomFont(double size) {
        return Font.loadFont(FontManager.class.getResourceAsStream(FONT_PATH), size);
    }

    public static Font loadCustomFont(String name, double size) {
        return Font.font(name, size);
    }
}
