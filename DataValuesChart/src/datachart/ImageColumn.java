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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Jocopa3
 */
public class ImageColumn {

    private String name;
    private String folder;
    private int columns;
    private ArrayList<ImageCell> cells;

    private transient final int leftPadding = 9;
    private transient final int rightPadding = 9;

    public ImageColumn(String name, String folder, int columns) {
        this.name = name;
        this.folder = folder;
        this.columns = columns;
        this.cells = new ArrayList<ImageCell>();
    }

    public void init() {
        int id = 0;
        int data = 0;
        ImageCell prev = null;

        for (ImageCell cell : cells) {
            if (cell.getId() == 0) {
                if (cell.isDisplayingData()) {
                    if (cell.getData() == 0) {
                        cell.setId(++id);
                        data = 0;
                    } else {
                        cell.setId(id);
                        cell.setData(++data);
                    }
                } else {
                    cell.setId(++id);
                    data = 0;
                }
            } else if (prev != null && !cell.isDisplayingData() && cell.getId() == prev.getId()) {
                cell.setData(++data);
                prev.shouldDisplayData(true);
                cell.shouldDisplayData(true);
            } else {
                id = cell.getId();
                data = cell.getData();
            }
            prev = cell;

            cell.init(this);
        }
    }

    public int calculateWidth(DrawOptions options) {
        return ImageCell.calculateWidth(options) * columns + leftPadding + rightPadding;
    }

    public int calculateHeight(DrawOptions options) {
        int rows = (int) Math.ceil(cells.size() / (float) columns);
        return 29 + ImageCell.calculateHeight(options) * rows;
    }

    public String getFolder() {
        return folder;
    }

    public BufferedImage render(DrawOptions options, int height) {
        if (columns <= 0) {
            columns = 1;
        }

        int rows = cells.size() / columns;
        int width = calculateWidth(options);
        //int height = getHeight();
        BufferedImage columnImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = columnImage.createGraphics();
        g.setBackground(options.getBackgroundColor());
        g.setColor(options.getBackgroundColor());
        g.fillRect(0, 0, columnImage.getWidth(), columnImage.getHeight());

        g.setColor(options.getLineColor());
        
        int fontType = options.boldText() ? Font.BOLD : Font.PLAIN;
        Font font = FontUtil.getFont(fontType, 12);
        FontMetrics fm = g.getFontMetrics(font);

        int textWidth = fm.stringWidth(name);
        int textHeight = fm.getHeight();
        int textX = width / 2 - textWidth / 2;
        int textY = textHeight / 2;

        g.setFont(font);
        g.drawString(name, textX, textY + textHeight / 4);

        // Top line
        g.drawLine(1, textY, textX - 3, textY);
        g.drawLine(textX + textWidth + 2, textY, width - 2, textY);

        // Left/Right lines
        g.drawLine(0, textY + 1, 0, height - 2);
        g.drawLine(width - 1, textY + 1, width - 1, height - 2);

        // Bottom line (heh)
        g.drawLine(1, height - 1, width - 2, height - 1);
        
        // Draw rendered cell images
        int icw = ImageCell.calculateWidth(options);
        int ich = ImageCell.calculateHeight(options);
        for (int i = 0; i < cells.size(); i++) {
            int cellX = i % columns;
            int cellY = i / columns;

            ImageCell cell = cells.get(i);

            g.drawImage(
                    cell.render(options),
                    icw * cellX + leftPadding,
                    ich * cellY + textY + 12,
                    null
            );
        }

        if (options.showGridLines() && !cells.isEmpty()) {
            BasicStroke dashedLine = new BasicStroke(1.0f,
                    BasicStroke.CAP_SQUARE,
                    BasicStroke.JOIN_MITER,
                    10.0f, new float[]{3.0f}, 0.0f);

            g.setStroke(dashedLine);

            int a = cells.size() % columns;
            for (int i = -1; i <= rows; i++) {
                int w = (i == rows) ? a: columns;
                g.drawLine(leftPadding, ich * (i + 1) + textY + 11, icw * w + leftPadding - 1, ich * (i + 1) + textY + 11);
            }
            
            for (int i = 0; i <= columns; i++) {
                int h = i <= a ? 1 : 0;
                g.drawLine(icw * i + leftPadding - 1, textY + 11, icw * i + leftPadding - 1, (rows + h) * ich + textY + 11);
            }
        }
        
        g.dispose();

        return columnImage;
    }
}
