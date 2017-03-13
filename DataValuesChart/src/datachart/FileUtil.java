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

import java.io.File;
import java.net.URISyntaxException;

/**
 *
 * @author Jocopa3
 */
public class FileUtil {
    public static File getExternalFile(String relPath) {
        File temp;
        
        try {
            File externalFolder = new File(ImageUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
            String folderPath = externalFolder.getPath().replace("\\build", "\\");
            temp = new File(folderPath + File.separator + relPath);

            if (!temp.exists()) {
                System.out.println("No file found at: " + temp.getPath());
            }
        } catch (URISyntaxException ex) {
            //ex.printStackTrace();
            System.out.println("Failed to get parent folder.");
            temp = null;
        }
        
        return temp;
    }
}
