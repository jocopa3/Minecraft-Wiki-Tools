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

import java.awt.Color;

/**
 *
 * @author Jocopa3
 */
public class DrawOptions {
    private int[] backgroundColor;
    private Color bgColor;
    
    private int[] lineColor;
    private Color lnColor;
    
    private int[] textColor;
    private Color txColor;
    
    private boolean showGridLines;
    private boolean imageAntiAlias;
    private boolean textAntiAlias;
    
    private boolean boldText;
    
    private ImagePadding chartFormat;
    private ImagePadding columnFormat;
    private ImagePadding cellFormat;

    public Color getBackgroundColor() {
        if(bgColor == null) {
            bgColor = new Color(backgroundColor[0], backgroundColor[1], backgroundColor[2]);
        }
        
        return bgColor;
    }
    
    public Color getLineColor() {
        if(lnColor == null) {
            lnColor = new Color(lineColor[0], lineColor[1], lineColor[2]);
        }
        
        return lnColor;
    }
    
    public Color getTextColor() {
        if(txColor == null) {
            txColor = new Color(textColor[0], textColor[1], textColor[2]);
        }
        
        return txColor;
    }
    
    public boolean showGridLines() {
        return showGridLines;
    }
    
    public boolean imageAntiAliasing() {
        return imageAntiAlias;
    }
    
    public boolean textAntiAliasing() {
        return textAntiAlias;
    }
    
    public boolean boldText() {
        return boldText;
    }
}
