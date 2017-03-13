/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datachart;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;

/**
 *
 * @author Matt
 */
public class ImageUtil {

    private static final ImageUtil SINGLETON = new ImageUtil();

    private BufferedImage readImageInternal(String path) {
        BufferedImage image = null;

        try {
            //System.out.println(getClass().getResource(path));
            image = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException ex) {
            System.out.println("Failed to read image: " + path);
            //ex.printStackTrace();
        }

        return image;
    }

    private BufferedImage readImageExternal(String path) {
        File temp;
        try {
            File externalFolder = new File(ImageUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
            String f = externalFolder.getPath().replace("\\build", "\\");
            temp = new File(f + File.separator + path);

            if (!temp.exists()) {
                System.out.println("Image not found: " + temp.getPath());
            }
        } catch (URISyntaxException ex) {
            //ex.printStackTrace();
            System.out.println("Failed to find current folder.");
            temp = null;
        }

        if (temp == null || !temp.exists()) {
            return null;
        }

        BufferedImage image = null;

        try {
            //System.out.println(getClass().getResource(path));
            image = ImageIO.read(temp);
        } catch (IOException ex) {
            System.out.println("Failed to read image: " + temp.getPath());
            //ex.printStackTrace();
        }

        return image;
    }

    public static BufferedImage readImageFromAssets(String path) {
        // All paths should point to the assets folder
        path = path.replaceAll("\\\\", "/");

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (!path.startsWith("/assets/")) {
            path = "/assets/" + path;
        }

        return SINGLETON.readImageInternal(path);
    }

    public static BufferedImage readImageFromResources(String path) {
        // All paths should point to the resources folder
        path = path.replaceAll("\\\\", "/");

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (!path.startsWith("/res/")) {
            path = "/res/" + path;
        }

        return SINGLETON.readImageExternal(path);
    }
    
    public static BufferedImage readImageFromFile(String path) {
        BufferedImage image = null;

        try {
            //System.out.println(getClass().getResource(path));
            image = ImageIO.read(new File(path));
        } catch (IOException ex) {
            System.out.println("Failed to read image: " + path);
            //ex.printStackTrace();
        }

        return image;
    }

    public static void discardAlpha(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();

        int[] rgbArray = src.getRGB(0, 0, w, h, null, 0, w);

        for (int i = 0; i < w * h; i++) {
            int a = (rgbArray[i] >> 24) & 0xff;
            int r = (rgbArray[i] >> 16) & 0xff;
            int g = (rgbArray[i] >> 8) & 0xff;
            int b = rgbArray[i] & 0xff;

            // a/255 ensures alpha must be either 255 or 0
            rgbArray[i] = ((a/255 * 255) << 24) | (r << 16) | (g << 8) | b;
        }

        src.setRGB(0, 0, w, h, rgbArray, 0, w);
    }

    public static BufferedImage scaleImage(BufferedImage srcImg, int w, int h) {
        BufferedImage scaledImage = new BufferedImage(w, h, srcImg.getType());
        Graphics2D g2d = scaledImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        g2d.drawImage(srcImg, 0, 0, w, h, null);

        g2d.dispose();

        return scaledImage;
    }

    public static BufferedImage scaleImage(BufferedImage srcImg, int w, int h, int type) {
        BufferedImage scaledImage = new BufferedImage(w, h, type);
        Graphics2D g2d = scaledImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        g2d.drawImage(srcImg, 0, 0, w, h, null);

        g2d.dispose();

        return scaledImage;
    }

    public static BufferedImage scaleImage(Image srcImg, int w, int h) {
        BufferedImage scaledImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        g2d.drawImage(srcImg, 0, 0, w, h, null);

        g2d.dispose();

        return scaledImage;
    }

    public static byte[] asBytes(BufferedImage srcImg) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(srcImg, "png", baos);
            baos.flush();
            byte[] imgArray = baos.toByteArray();
            baos.close();
            return imgArray;
        } catch (IOException e) {
            return null;
        }
    }
}
