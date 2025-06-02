//Torbert, e-mail: mr@torbert.com, website: www.mr.torbert.com
//version 6.17.2003  -- modified 2025-06-02 for grid-center walking

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class KarelPanel extends JPanel implements KeyListener {

    /* ===== 常數區 ===== */
    private static final int SIDE   = 390;   // 面板寬高 (正方形)
    private static final int STEP   = 30;    // 兩條格線的距離、一次鍵盤移動量
    private static final int OFFSET = 15;    // 第一條線離邊框距離 (題目給定)
    private static final Color BG   = new Color(204, 204, 204);

    /* ===== 影像與狀態 ===== */
    private final ImageIcon[] img = new ImageIcon[4];   // 0→ 1↑ 2← 3↓
    private int dir = 0;                                // 初始向右
    private int col, row;                               // 「交點座標」：col=0,row=0 = 左下交點

    private final BufferedImage canvas = new BufferedImage(SIDE, SIDE,
                                                           BufferedImage.TYPE_INT_RGB);
    private final Graphics buf = canvas.getGraphics();
    private final int w, h;                  // Karel 圖片寬、高 (假設四張一樣大)
    private final int colMax, rowMax;        // 允許的最大交點座標

    /* ===== 建構子 ===== */
    public KarelPanel() {

        img[0] = new ImageIcon("karele.gif");
        img[1] = new ImageIcon("kareln.gif");
        img[2] = new ImageIcon("karelw.gif");
        img[3] = new ImageIcon("karels.gif");

        /* 取第一張圖的尺寸即可 */
        w = img[0].getIconWidth();
        h = img[0].getIconHeight();

        /* 允許的 col/row 範圍 -- 保證整張圖永遠在面板內 */
        colMax = (SIDE - OFFSET - w / 2) / STEP;
        rowMax = (SIDE - OFFSET - h / 2) / STEP;

        reset(false);        // 起點左下，不顯示訊息

        setPreferredSize(new Dimension(SIDE, SIDE));
        setFocusable(true);
        addKeyListener(this);

        /* 只做重繪，邏輯全部在 keyPressed 內 */
        new Timer(40, e -> repaint()).start();
    }

    /* ===== 把交點座標轉成畫布像素 ===== */
    private int pixelX() {                       // 左上角 X
        return OFFSET + col * STEP - w / 2;
    }
    private int pixelY() {                       // 左上角 Y (注意 Swing Y 軸向下)
        // 交點的 Y (以頂端為 0) ＝ SIDE - OFFSET - row*STEP
        return (SIDE - OFFSET - row * STEP) - h / 2;
    }

    /* ===== 重置到左下交點 ===== */
    private void reset(boolean log) {
        col = 0;
        row = 0;
        if (log) System.out.println("已重置到起點");
    }

    /* ===== 繪圖 ===== */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        /* 背景 */
        buf.setColor(BG);
        buf.fillRect(0, 0, SIDE, SIDE);

        /* 紅色格線 */
        buf.setColor(Color.RED);
        for (int x = OFFSET; x <= SIDE; x += STEP) buf.drawLine(x, 0, x, SIDE);
        for (int y = OFFSET; y <= SIDE; y += STEP) buf.drawLine(0, y, SIDE, y);

        /* Karel */
        buf.drawImage(img[dir].getImage(), pixelX(), pixelY(), null);

        /* 貼到螢幕 */
        g.drawImage(canvas, 0, 0, null);
    }

    /* ===== 鍵盤事件 ===== */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_RIGHT:
                dir = 0;
                if (col < colMax)     col++;
                else System.out.println("警告：已到右邊界");
                break;

            case KeyEvent.VK_UP:
                dir = 1;
                if (row < rowMax)     row++;
                else System.out.println("警告：已到上邊界");
                break;

            case KeyEvent.VK_LEFT:
                dir = 2;
                if (col > 0)          col--;
                else System.out.println("警告：已到左邊界");
                break;

            case KeyEvent.VK_DOWN:
                dir = 3;
                if (row > 0)          row--;
                else System.out.println("警告：已到下邊界");
                break;

            case KeyEvent.VK_SPACE:
                reset(true);
                break;

            default:
                return;               // 其他鍵忽略
        }
        repaint();
    }

   
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e)   {}
}
