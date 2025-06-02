import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * SlingshotPanel – 手動拖曳繞中心、放開彈射（含速度衰減）
 *  1. 左鍵點到黑球 → 記錄抓球點 centerPoint，進入拖曳
 *  2. 拖曳時：黑球位置 = 滑鼠位置（因此可手動繞圈）
 *             當前半徑 orbitRadius = |center→ball|
 *             畫紅線／黃圓做瞄準參考
 *  3. 放開：依「center→放手點」向量反向給初速，之後每幀乘 SPEED_DECAY
 *  4. 非拖曳：黑球自動飛行，遇牆反彈、撞紅球得分
 */
public class SlingshotPanel extends JPanel {

    /* ── 可調參數 ── */
    private static final int    W = 512, H = 512;
    private static final int    FPS = 60;
    private static final int    BALL_R = 8;
    private static final int    TARGET_R = 12;
    private static final double SPEED_SCALE = 0.25;   // 彈弓倍率
    private static final double SPEED_DECAY = 0.995;  // 衰減係數
    /* ── ① 新增一個固定半徑常數 ── */
      private static final int CENTER_MARK_R = 10;   // 黃圈半徑（固定大小）

    /* ── 遊戲狀態 ── */
    private Point  ball   = new Point(80, 380);       // 黑球
    private Point  target = randTarget();             // 紅球
    private double vx, vy;                            // 黑球速度
    private int    score = 0;

    /* ── 拖曳狀態 ── */
    private boolean dragging    = false;
    private Point   centerPoint = new Point();        // 抓球點（固定圓心）
    private double  orbitRadius = 0;                  // 目前半徑

    /* ── 建構子 ── */
    public SlingshotPanel() {
        setPreferredSize(new Dimension(W, H));
        setBackground(new Color(0xCCCCCC));

        /* 給黑球隨機初速 */
        ThreadLocalRandom r = ThreadLocalRandom.current();
        do { vx = r.nextInt(-4, 5); vy = r.nextInt(-4, 5); }
        while (vx == 0 && vy == 0);

        /* 更新計時器 */
        new Timer(1000 / FPS, e -> { updatePhysics(); repaint(); }).start();

        /* 滑鼠事件 */
        MouseAdapter m = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 &&
                    e.getPoint().distance(ball) <= BALL_R) {

                    dragging     = true;
                    centerPoint  = e.getPoint();   // 固定圓心
                    orbitRadius  = 0;              // 初始半徑
                    vx = vy      = 0;              // 停住
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    /* 黑球位置 = 滑鼠位置 */
                    ball.setLocation(e.getPoint());

                    /* 更新半徑（給黃圈用） */
                    orbitRadius = centerPoint.distance(ball);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (dragging && e.getButton() == MouseEvent.BUTTON1) {
                    dragging = false;

                    /* 依 center→放手 向量反向發射 */
                    double dx = centerPoint.x - e.getX();
                    double dy = centerPoint.y - e.getY();
                    vx = dx * SPEED_SCALE;
                    vy = dy * SPEED_SCALE;
                }
            }
        };
        addMouseListener(m);
        addMouseMotionListener(m);
    }

    /* ── 物理更新 ── */
    private void updatePhysics() {
        if (!dragging) {
            /* 自動飛行 */
            ball.translate((int)Math.round(vx), (int)Math.round(vy));

            /* 邊界反彈 */
            if (ball.x < BALL_R)          { ball.x = BALL_R;       vx = -vx; }
            if (ball.x > W - BALL_R)      { ball.x = W - BALL_R;   vx = -vx; }
            if (ball.y < BALL_R)          { ball.y = BALL_R;       vy = -vy; }
            if (ball.y > H - BALL_R)      { ball.y = H - BALL_R;   vy = -vy; }

            /* 速度衰減 */
            vx *= SPEED_DECAY;
            vy *= SPEED_DECAY;
        }

        /* 撞紅球得分 */
        if (ball.distance(target) <= BALL_R + TARGET_R) {
            score++;
            target = randTarget();
        }
    }

    /* ── 隨機紅球座標 ── */
    private static Point randTarget() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        return new Point(r.nextInt(80, W - 80), r.nextInt(80, H - 80));
    }

    /* ── 畫面 ── */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        /* 分數 */
        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20f));
        g2.drawString("Score: " + score, 10, 28);

        /* 紅球 */
        g2.setColor(Color.RED);
        g2.fillOval(target.x - TARGET_R, target.y - TARGET_R,
                    TARGET_R * 2, TARGET_R * 2);

        /* 黑球 */
        g2.setColor(Color.BLACK);
        g2.fillOval(ball.x - BALL_R, ball.y - BALL_R,
                    BALL_R * 2, BALL_R * 2);

        /* 拖曳視覺 */
        if (dragging) {
            float[] dash = {6f, 6f};
            Stroke old = g2.getStroke();

            /* 黃色虛線圓：半徑 orbitRadius、圓心 centerPoint */
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT,
                                         BasicStroke.JOIN_ROUND, 0, dash, 0));
            g2.setColor(Color.YELLOW);
            
            g2.drawOval(centerPoint.x - CENTER_MARK_R,
            centerPoint.y - CENTER_MARK_R,
            CENTER_MARK_R * 2, CENTER_MARK_R * 2);


            /* 紅色彈弓線 */
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(Color.RED);
            g2.drawLine(centerPoint.x, centerPoint.y,
                        ball.x, ball.y);

            g2.setStroke(old);
        }
    }
}
