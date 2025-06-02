import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * 檔名：ROCFlag.java
 * 這個類別繼承自 JPanel，用來繪製整面國旗（600×400）的內容，
 * 並將原先的 12 個白色三角形改為一個 12 星角的白色星形（star‐polygon {12/5}），
 * 接著再以藍色環遮去底部，最後繪製白色圓盤。
 */
public class ROCFlag extends JPanel {

    // 內容區尺寸：600×400
    private static final int FLAG_W = 600;
    private static final int FLAG_H = 400;

    // 青天矩形（左上角）：300×200
    private static final int SKY_W = 300;
    private static final int SKY_H = 200;

    // 白日（星形）各圓半徑（以像素計）
    // 外圓半徑 R_OUTER = 7.5 單位 × 10 = 75 px
    // 藍環半徑  R_RING  = 4.25 單位 × 10 = 42.5 px
    // 白圓半徑  R_SUN   = 3.75 單位 × 10 = 37.5 px
    private static final double R_OUTER = 7.5 * 10;  // 75.0
    private static final double R_RING  = 4.25 * 10; // 42.5
    private static final double R_SUN   = 3.75 * 10; // 37.5

    // 青天中心點 (cx, cy)
    private static final double CX = SKY_W / 2.0; // 300/2 = 150.0
    private static final double CY = SKY_H / 2.0; // 200/2 = 100.0

    // 官方近似色
    private static final Color RED   = new Color(0xFE0000);
    private static final Color BLUE  = new Color(0x000095);
    private static final Color WHITE = Color.WHITE;

    public ROCFlag() {
        // 確保這個 JPanel 的繪製區剛好是 FLAG_W×FLAG_H
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

        // (1) 紅底：填滿整個 600×400
        g2.setColor(RED);
        g2.fillRect(0, 0, FLAG_W, FLAG_H);

        // (2) 青天：左上角 300×200 塗滿藍色
        g2.setColor(BLUE);
        g2.fillRect(0, 0, SKY_W, SKY_H);

        // (3) 白色 12 星角：star‐polygon {12/5}
        //     先計算 12 個外圓頂點 (px[i], py[i])
        double[] px = new double[12];
        double[] py = new double[12];
        for (int i = 0; i < 12; i++) {
            double theta = Math.toRadians(i * 30 - 90); // -90° 讓第一頂朝上
            px[i] = CX + R_OUTER * Math.cos(theta);
            py[i] = CY + R_OUTER * Math.sin(theta);
        }
        // 再用「跳步 5 步」連成閉合路徑
        Path2D star = new Path2D.Double();
        star.moveTo(px[0], py[0]);
        int idx = 0;
        for (int step = 0; step < 11; step++) {
            idx = (idx + 5) % 12;
            star.lineTo(px[idx], py[idx]);
        }
        star.closePath();

        g2.setColor(WHITE);
        g2.fill(star);

        // (4) 藍色環：以半徑 R_RING 遮住星形底部的交疊雜線
        g2.setColor(BLUE);
        double ringD = R_RING * 2; // 42.5*2 = 85
        g2.fill(new Ellipse2D.Double(
            CX - R_RING,
            CY - R_RING,
            ringD,
            ringD
        ));

        // (5) 白色圓盤：以半徑 R_SUN 畫實心白圓
        g2.setColor(WHITE);
        double sunD = R_SUN * 2; // 37.5*2 = 75
        g2.fill(new Ellipse2D.Double(
            CX - R_SUN,
            CY - R_SUN,
            sunD,
            sunD
        ));

        g2.dispose();
    }
}
