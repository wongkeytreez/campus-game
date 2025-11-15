import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class CallbackPanel extends JPanel {

    private CallbackPanel instance;
    transient private final List<Runnable> callbacks = new CopyOnWriteArrayList<>();
    private final List<Runnable> mousePressCallbacks = new CopyOnWriteArrayList<>();
    private final Map<Integer, List<Runnable>> keyPressCallbacks = new ConcurrentHashMap<>();

    private Graphics2D currentGraphics;

    public int mouseX = 0, mouseY = 0, camx, camy;

    private final List<CustomButton> buttons = new CopyOnWriteArrayList<>();

    public CallbackPanel(int width, int height) {
        instance = this;

        setBackground(Color.WHITE);
        setOpaque(true);

        JFrame frame = new JFrame("Callback Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.add(this);
        frame.setVisible(true);

        // --- Mouse move tracking ---
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        // --- Mouse press callbacks ---
        addMouseListener(new MouseAdapter() {
             CustomButton pressedButton = null;
            @Override
            public void mousePressed(MouseEvent e) {
                // 1. Run button callbacks
                for (CustomButton b : buttons) {
            if (b.contains(e.getX(), e.getY())) {
                pressedButton = b;
                break;
            }
        }
              
                
            }
            @Override
    public void mouseReleased(MouseEvent e) {
          // 2. Run generic mouse press callbacks

        if (pressedButton != null && pressedButton.contains(e.getX(), e.getY())) {
            // only run if released inside same button
            pressedButton.callback.run();
        }
        for (Runnable cb : mousePressCallbacks) {
                    cb.run();
                }
        pressedButton = null; // reset
    }
        });

        // --- Keyboard press callbacks ---
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (keyPressCallbacks.containsKey(code)) {
                    for (Runnable cb : keyPressCallbacks.get(code)) {
                        cb.run();
                    }
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    // ===== Callback registration =====

    public void addCallback(Runnable callback) {
        if (instance != null) instance.callbacks.add(callback);
    }

    public void addMousePressCallback(Runnable callback) {
        if (instance != null) instance.mousePressCallbacks.add(callback);
    }

    public void addKeyPressCallback(int keyCode, Runnable callback) {
        keyPressCallbacks.computeIfAbsent(keyCode, k -> new CopyOnWriteArrayList<>()).add(callback);
    }

    // ===== Drawing =====

    public void addImage(BufferedImage img, int x, int y, double size, double rotation) {
        if (instance != null && instance.currentGraphics != null) {
            drawImage(img, x, y, size, rotation);
        }
    }

    public void addButton(int x, int y, int w, int h, BufferedImage img, Runnable callback) {
        buttons.add(new CustomButton(x, y, w, h, img, callback));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        currentGraphics = (Graphics2D) g;

        // run callbacks
        for (Runnable cb : callbacks) {
            cb.run();
        }

        // draw buttons
        for (CustomButton b : buttons) {
            b.draw(currentGraphics);
        }

        currentGraphics = null;
    }

    private void drawImage(BufferedImage img, int x, int y, double size, double rot) {
        int w = img.getWidth(), h = img.getHeight();
        AffineTransform t = new AffineTransform();
        t.translate((x - (w * size) / 2.0) + camx, (y - (h * size) / 2.0) + camy);
        t.scale(size, size);
        instance.currentGraphics.drawImage(img, t, null);
    }

    // ===== Inner button class =====
    private static class CustomButton {
        int x, y, w, h;
        BufferedImage img;
        Runnable callback;

        public CustomButton(int x, int y, int w, int h, BufferedImage img, Runnable callback) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.img = img;
            this.callback = callback;
        }

        public void draw(Graphics2D g) {
            g.setColor(new Color(0, 120, 215));
            g.fillRoundRect(x, y, w, h, 20, 20);
            if (img != null) {
                int imgX = x + (w - img.getWidth()) / 2;
                int imgY = y + (h - img.getHeight()) / 2;
                g.drawImage(img, imgX, imgY, null);
            }
        }

        public boolean contains(int mx, int my) {
            return mx >= x && mx <= x + w && my >= y && my <= y + h;
        }
    }
}
