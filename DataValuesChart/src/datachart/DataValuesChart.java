package datachart;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import static datachart.DataValuesChart.gson;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Jocopa3
 */
public class DataValuesChart {

    static ImageChart chart;
    static BufferedImage image;
    static Gson gson;
    static ImagePane pane;
    static JFrame window;
    protected static File file;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore and let Java use the default look and feel
        }
        file = openJsonFile();
        
        gson = new GsonBuilder().registerTypeAdapter(ImageCell.class, new ImageCellDeserializer()).create();
        chart = gson.fromJson(new FileReader(file), ImageChart.class);
        chart.init();

        image = chart.render();//getColumn(0).render(chart.getDrawOptions());

        showPreview(image);
    }

    public static File openJsonFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle("Select Chart JSON File");

        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                final String name = f.getName();
                return name.toLowerCase().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "JSON Files";
            }
        });

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        
        return null;
    }

    public static void showPreview(BufferedImage img) {
        pane = new ImagePane(img);
        JScrollPane scroll = new JScrollPane(pane);
        
        window = new JFrame();
        window.setTitle(file.getName() + " Preview");
        window.setBackground(java.awt.Color.green);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        window.addKeyListener(new RefreshListener());
        
        window.add(scroll, BorderLayout.CENTER);
        window.pack();
        
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void refresh() {
        System.out.println("Refreshing...");
        try {
            chart = gson.fromJson(new FileReader(file), ImageChart.class);
        } catch (FileNotFoundException ex) {
            System.out.println("Couldn't find specified JSON file");
        }
        chart.init();

        image = chart.render();
        pane.setImage(image);

        window.repaint();
    }

    public static void save() {
        String savePath = file.getAbsolutePath().replaceFirst("[.][^.]+$", "");
        File saveFile = new File(savePath + ".png");
        
        try {
            ImageIO.write(image, "png", saveFile);
            
            System.out.println("Saved to: " + saveFile.getAbsolutePath());
            JOptionPane.showMessageDialog(window, "Saved to: " + saveFile.getAbsolutePath(), "Successfully Saved!", JOptionPane.PLAIN_MESSAGE);
            
            window.dispose();
            System.exit(0);
        } catch (IOException ex) {
            System.out.println("Couldn't save file: " + saveFile.getAbsolutePath());
            JOptionPane.showMessageDialog(window, "Couldn't save file: " + saveFile.getAbsolutePath(), "Failed to Save...", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class RefreshListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.isControlDown() || e.isAltDown() || e.isShiftDown() || e.isMetaDown()) {
            return;
        }
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                DataValuesChart.save();
                break;
            default:
                //long time = System.currentTimeMillis()
                DataValuesChart.refresh();
                //System.out.println("Took: " + (System.currentTimeMillis() - time));
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}

//This stuff used for rendering the image on the jPanel; not very important otherwise
class ImagePane extends javax.swing.JPanel {

    private BufferedImage background;

    public ImagePane(BufferedImage image) {
        background = image;
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    public void setImage(BufferedImage image) {
        background = image;
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    @Override
    public java.awt.Dimension getPreferredSize() {
        return background == null ? super.getPreferredSize() : new java.awt.Dimension(background.getWidth(), background.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //this.setBackground(Color.black);
        if (background != null) {
            g.drawImage(background, 0, 0, this);
        }
    }

}
