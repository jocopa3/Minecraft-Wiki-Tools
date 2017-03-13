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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Jocopa3
 */
public class ImageChart {

    private String name;
    private String version;
    private String format;
    private int columns;

    private DrawOptions drawOptions;

    private ArrayList<ObtainEnum> obtainTypes;
    private transient HashMap<String, ObtainEnum> obtainMap;
    private ObtainEnum defaultEnum;

    private int[] rowHeights;
    private ArrayList<ImageColumn> chartColumns;

    private transient int leftMargin = 19;
    private transient int rightMargin = 19;
    private transient int topMargin = 12;
    private transient int bottomMargin = 32;
    private transient int vColumnPadding = 5;
    private transient int hColumnPadding = 18;

    protected void init() {
        obtainMap = new HashMap<String, ObtainEnum>();

        for (ObtainEnum obtainability : obtainTypes) {
            if (obtainability.isDefault()) {
                defaultEnum = obtainability;
            }

            obtainMap.put(obtainability.getType(), obtainability);
        }

        rowHeights = new int[(int) Math.ceil(chartColumns.size() / (float) columns)];

        int i = 0;
        for (ImageColumn column : chartColumns) {
            column.init();

            int h = rowHeights[(int) Math.floor(i / (float) columns)];
            rowHeights[(int) Math.floor(i / (float) columns)] = Math.max(h, column.calculateHeight(drawOptions));

            i++;
        }
    }

    public int getWidth() {
        int maxWidth = 0;
        int tempWidth = 0;
        int i = 0;

        for (ImageColumn column : chartColumns) {
            i++;
            if (chartColumns.size() <= columns) {
                maxWidth += column.calculateWidth(drawOptions);
            } else {
                tempWidth += column.calculateWidth(drawOptions);

                if (i % columns == 0) {
                    maxWidth = Math.max(tempWidth, maxWidth);
                    tempWidth = 0;
                }
            }
        }

        return leftMargin + rightMargin + (columns - 1) * hColumnPadding + maxWidth;
    }

    public int getHeight() {
        int maxHeight = 0;

        for (int i = 0; i < rowHeights.length; i++) {
            maxHeight += rowHeights[i];
        }

        return topMargin + bottomMargin + (rowHeights.length) * vColumnPadding + maxHeight + obtainTypes.size() * 14;
    }
    
    public String getName() {
        return name;
    }

    public BufferedImage render() {
        int width = getWidth();
        int height = getHeight();

        BufferedImage chartImage = new BufferedImage(
                getWidth(),
                getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = chartImage.createGraphics();

        g.setBackground(drawOptions.getBackgroundColor());
        g.setColor(drawOptions.getBackgroundColor());
        g.fillRect(0, 0, chartImage.getWidth(), chartImage.getHeight());

        int x = leftMargin;
        int y = topMargin;
        int rowHeight;
        int prevRowHeight = rowHeights[0];
        int i = 0;

        for (ImageColumn column : chartColumns) {
            rowHeight = rowHeights[(int) Math.floor(i / (float) columns)];
            
            if (i % columns == 0 && i != 0) {
                x = leftMargin;
                y += prevRowHeight + vColumnPadding;
                prevRowHeight = rowHeight;
            }
            i++;

            g.drawImage(column.render(drawOptions, rowHeight), x, y, null);
            x += column.calculateWidth(drawOptions) + hColumnPadding;
        }

        int fontType = drawOptions.boldText() ? Font.BOLD : Font.PLAIN;
        Font size10Font = FontUtil.getFont(fontType, 10);
        Font size12Font = FontUtil.getFont(fontType, 12);
        FontMetrics fm10 = g.getFontMetrics(size10Font);
        FontMetrics fm12 = g.getFontMetrics(size12Font);
        
        g.setFont(size12Font);
        g.setColor(drawOptions.getLineColor());
        
        int versionWidth = fm12.stringWidth(version);
        g.drawString(version, width / 2 - versionWidth / 2, fm12.getHeight() - fm12.getHeight()/4 + 1);
        
        g.setFont(size10Font);
        
        int maxLen = 0;
        for(ObtainEnum obtainType : obtainTypes) {
            maxLen = Math.max(fm10.stringWidth(obtainType.getDescription()), maxLen);
        }
        
        y = height - bottomMargin - obtainTypes.size() * 14;
        x = width - rightMargin - maxLen;
        for(ObtainEnum obtainType : obtainTypes) {
            g.setColor(obtainType.getColor());
            g.fillRect(x - 11, y, 8, 8);
            g.setColor(drawOptions.getTextColor());
            g.drawString(obtainType.getDescription(), x + 1, y + fm10.getHeight()/2 + 1);
            y += 14;
        }
        
        g.dispose();

        return chartImage;
    }
    
    public ImageColumn getColumn(int ind) {
        return chartColumns.get(ind);
    }

    public ObtainEnum getEnumByName(String name) {
        ObtainEnum type = obtainMap.get(name);

        if (type == null) {
            return defaultEnum;
        }

        return type;
    }

    public DrawOptions getDrawOptions() {
        return drawOptions;
    }
}
