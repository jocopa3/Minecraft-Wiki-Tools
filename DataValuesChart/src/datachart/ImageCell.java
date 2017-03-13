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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author Jocopa3
 */
public class ImageCell {

    private int id, data;
    private String fileName, obtain;
    private boolean displayDataValue;
    private transient BufferedImage image;

    private static int baseWidth = 36;
    private static int baseHeight = 51;

    public ImageCell(int id, String imageName) {
        this.id = id;
        this.data = 0;
        this.displayDataValue = false;

        if (imageName == null || imageName.isEmpty()) {
            image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            return;
        }

        image = ImageUtil.readImageFromResources("blocks" + File.separator + imageName);

        if (image == null) {
            image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            return;
        }

        image = ImageUtil.scaleImage(image, 32, 32);
    }

    public ImageCell(int id, int data, String imageName) {
        this.id = id;
        this.data = data;
        this.displayDataValue = true;

        if (imageName == null || imageName.isEmpty()) {
            image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            return;
        }

        image = ImageUtil.readImageFromResources("blocks" + File.separator + imageName);

        if (image == null) {
            image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            return;
        }

        image = ImageUtil.scaleImage(image, 32, 32);
    }

    public void init(ImageColumn column) {
        if (obtain == null || obtain.isEmpty()) {
            obtain = DataValuesChart.chart.getEnumByName("").getType();
        }

        if (fileName == null || fileName.isEmpty()) {
            image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            return;
        }

        image = ImageUtil.readImageFromFile(DataValuesChart.file.getParent() + File.separator + column.getFolder() + File.separator + fileName);

        if (image == null) {
            image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            return;
        }

        int wid = image.getWidth();
        int hei = image.getHeight();

        if (wid == hei) {
            image = ImageUtil.scaleImage(image, 32, 32, BufferedImage.TYPE_INT_ARGB);
        } else if (wid > hei) {
            float ratio = (float) hei / (float) wid;
            image = ImageUtil.scaleImage(image, 32, Math.round(32.0f * ratio), BufferedImage.TYPE_INT_ARGB);
        } else {
            float ratio = (float) wid / (float) hei;
            image = ImageUtil.scaleImage(image, Math.round(32.0f * ratio), 32, BufferedImage.TYPE_INT_ARGB);
        }

        //ImageUtil.discardAlpha(image);
    }

    public int getId() {
        return id;
    }

    public int getData() {
        return data;
    }

    public boolean isDisplayingData() {
        return displayDataValue;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setData(int data) {
        this.data = data;
    }
    
    public void shouldDisplayData(boolean display) {
        displayDataValue = display;
    }
    
    public static int calculateWidth(DrawOptions options) {
        return baseWidth + (options.showGridLines() ? 3 : 0) + (options.boldText() ? 4 : 0);
    }
    
    public static int calculateHeight(DrawOptions options) {
        return baseHeight;
    }

    public BufferedImage render(DrawOptions options) {
        int fontType = options.boldText() ? Font.BOLD : Font.PLAIN;
        int width = calculateWidth(options);
        int height = calculateHeight(options);
        
        Font idFont = FontUtil.getFont(fontType, 12);
        Font dvFont = FontUtil.getFont(fontType, 10);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setClip(0, 0, width, height);
        g.setBackground(options.getBackgroundColor());
        g.setColor(options.getBackgroundColor());
        g.fillRect(0, 0, width, height);

        FontMetrics idMetrics = g.getFontMetrics(idFont);
        FontMetrics dvMetrics = g.getFontMetrics(dvFont);
        int idWidth = idMetrics.stringWidth(Integer.toString(id));
        int dvWidth = dvMetrics.stringWidth(Integer.toString(data));
        int totalWidth = idWidth + dvWidth;

        ObtainEnum obtainType = DataValuesChart.chart.getEnumByName(obtain);

        if (displayDataValue) {
            g.setFont(idFont);
            g.setColor(obtainType.getColor());
            g.drawString(Integer.toString(id), (width - totalWidth) / 2, idMetrics.getHeight() / 2 + 4);

            g.setFont(dvFont);
            g.setColor(options.getLineColor());
            g.drawString(Integer.toString(data), (width - totalWidth) / 2 + idWidth - 1, dvMetrics.getHeight() / 2 + 8);
        } else {
            g.setFont(idFont);
            g.setColor(obtainType.getColor());
            g.drawString(Integer.toString(id), (width - idWidth) / 2, idMetrics.getHeight() / 2 + 4);
        }

        g.drawImage(image, (width - image.getWidth()) / 2, 16 + (32 - image.getHeight()), null);

        g.dispose();

        return img;
    }

    public boolean equals(ImageCell other) {
        return other.id == id
                && other.data == data
                && other.fileName.equals(fileName)
                && other.displayDataValue == displayDataValue
                && other.obtain.equals(obtain);
    }
}
