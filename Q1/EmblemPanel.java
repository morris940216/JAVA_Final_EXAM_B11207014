import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * 檔名：EmblemPanel.java
 * 這個類別繼承自 JPanel，用來繪製「單獨國徽」（青天白日），
 * 先把整面 600×400 填滿藍色，然後把青天 (300×200) 放大兩倍至 (600×400)，
 * 接著仍以 12 星角 (star‐polygon {12/5}) + 藍環 + 白圓 的方式畫出太陽。
 */
public class EmblemPanel extends JPanel {

    // 內容區尺寸：600×400
    private static final int FLAG_W = 600;
    private static final int FLAG_H = 400;

    // 放大前的青天尺寸：300×200
    private static final int SKY_W = 300;
    private static final int SKY_H = 200;

    // 放大前各圓半徑（同 ROCFlag 中定義）
    private static final double R_OUTER = 7.5 * 10;  // 75.0 px
    private static final double R_RING  = 4.25 * 10; // 42.5 px
    private static final double R_SUN   = 3.75 * 10; // 37.5 px

    // 青天中心 (放大前)：(CX, CY) = (150, 100)
    private static final double CX = SKY_W / 2.0; // 150.0
    private static final double CY = SKY_H / 2.0; // 100.0

    // 顏色
    private static final Color BLUE  = new Color(0x000095);
    private static final Color WHITE = Color.WHITE;

    public EmblemPanel() {
        // 確保這個 JPanel 的繪製區是 FLAG_W×FLAG_H
        setPreferredSize(new Dimension(FLAG_W, FLAG_H));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 使用 Graphics2D 以啟用反鋸齒
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // (1) 先把整面 600×400 塗滿藍色
        g2.setColor(BLUE);
        g2.fillRect(0, 0, FLAG_W, FLAG_H);

        // (2) 等比例放大 2 倍：原本 300×200 青天 → 放大後佔滿 600×400
        g2.scale(2.0, 2.0);

        // (3a) 在「300×200 座標系」內先畫青天藍色矩形
        g2.setColor(BLUE);
        g2.fillRect(0, 0, SKY_W, SKY_H);

        // (3b) 計算放大前的 12 個外圓頂點
        double[] px = new double[12];
        double[] py = new double[12];
        for (int i = 0; i < 12; i++) {
            double theta = Math.toRadians(i * 30 - 90);
            px[i] = CX + R_OUTER * Math.cos(theta);
            py[i] = CY + R_OUTER * Math.sin(theta);
        }

        // (3c) star‐polygon {12/5}：跳步連成閉合路徑
        Path2D star = new Path2D.Double();
        star.moveTo(px[0], py[0]);
        int idx = 0;
        for (int step = 0; step < 11; step++) {
            idx = (idx + 5) % 12;
            star.lineTo(px[idx], py[idx]);
        }
        star.closePath();

        // (3d) 填滿白色星形
        g2.setColor(WHITE);
        g2.fill(star);

        // (4) 藍色環：放大前半徑 R_RING=42.5 → scale(2,2) 後實畫半徑仍 42.5
        g2.setColor(BLUE);
        double ringD = R_RING * 2; // 85
        g2.fill(new Ellipse2D.Double(
            CX - R_RING,
            CY - R_RING,
            ringD,
            ringD
        ));

        // (5) 白色圓盤：放大前半徑 R_SUN=37.5 → scale(2,2) 後實畫半徑 37.5
        g2.setColor(WHITE);
        double sunD = R_SUN * 2; // 75
        g2.fill(new Ellipse2D.Double(
            CX - R_SUN,
            CY - R_SUN,
            sunD,
            sunD
        ));

        g2.dispose();
    }
}
