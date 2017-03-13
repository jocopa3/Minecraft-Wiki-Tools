/*
 * The MIT License
 *
 * Copyright 2017 Jocopa3.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package datachart;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 *
 * @author Jocopa3
 */
public class FontUtil {

    private static String FONT_NAME;
    private static HashMap<Integer, Font> FONTS;

    static {
        FONTS = new HashMap<Integer, Font>();

        // All this does is tries to find the Microsoft Reference Sans Serif font
        // or MS Sans Serif font (they're both the same font).
        // For Linux or MacOSX 10.4 and older, it defaults to the system Sans Serif font
        String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        Pattern sansRegex = Pattern.compile("^(MS|Microsoft)[ \\w]*(Sans)[ \\w]*(Serif)[ \\w]*$");
        ArrayList<String> potentialFonts = new ArrayList<String>();

        for (String fontName : fonts) {
            if (sansRegex.matcher(fontName).find()) {
                if (fontName.contains("Reference")) {
                    FONT_NAME = fontName;
                    break;
                }

                potentialFonts.add(fontName);
            }
        }

        if (!potentialFonts.isEmpty() && (FONT_NAME == null || FONT_NAME.isEmpty())) {
            // Don't know which to pick in this case, so pick the first font
            FONT_NAME = potentialFonts.get(0);
            if (FONT_NAME.isEmpty()) {
                FONT_NAME = Font.SANS_SERIF;
            }
        }
    }

    private static int fontHash(int size, int type) {
        return type << 7 | size;
    }

    public static Font getFont(int type, int size) {
        int index = fontHash(size, type);
        Font font = FONTS.get(fontHash(size, type));

        if (font == null) {
            font = new Font(FONT_NAME, type, size);
            FONTS.put(index, font);
        }

        return font;
    }
}
