package org.jbomberman.utils;

import javafx.scene.text.Font;

import static org.jbomberman.utils.SceneManager.SCALE_FACTOR;

public class FontManager {

    private static final String FONT_PATH = "/org/jbomberman/SfComicScriptBold-YXD2.ttf";
    public static final Font SMALL = Font.loadFont(SceneManager.class.getResourceAsStream(FONT_PATH), SCALE_FACTOR-5);

    public static Font loadCustomFont(double size) {
        return Font.loadFont(FontManager.class.getResourceAsStream(FONT_PATH), size);
    }

    public static Font loadCustomFont(String name, double size) {
        return Font.font(name, size);
    }
}
