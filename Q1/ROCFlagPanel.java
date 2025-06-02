import javax.swing.*;
import java.awt.*;

/**
 * 檔名：ROCFlagPanel.java
 * 這裡的 main() 會同時開啟兩個 JFrame：
 *   （A）整面「青天白日滿地紅」國旗（內容區 600×400）
 *   （B）單獨「青天白日」國徽（內容區 600×400）
 *
 * 為了確保「內容區」的繪製範圍恰好是 600×400，使用 pack() 加上 getInsets() 的方式，修正整個 JFrame 的大小。
 */
public class ROCFlagPanel {

    private static final int CONTENT_W = 600;
    private static final int CONTENT_H = 400;

    public static void main(String[] args) {
        /* ========================================
         * （A）整面國旗：內容區 600×400
         * ======================================== */
        JFrame flagFrame = new JFrame("中華民國國旗");
        flagFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. 建立 ROCFlag Panel，並指定內容大小
        ROCFlag flagPanel = new ROCFlag();
        flagPanel.setPreferredSize(new Dimension(CONTENT_W, CONTENT_H));
        flagFrame.getContentPane().add(flagPanel);

        // 2. pack → 讓 JFrame 根據 content 的預設大小計算一個初步的 frameSize（含 insets）
        flagFrame.pack();

        // 3. 讀取 insets（top: 標題列+上框, left, bottom, right）
        Insets insF = flagFrame.getInsets();

        // 4. 再度設定 JFrame 的整體大小，確保「內容區」正好是 600×400
        int frameW_Flag = CONTENT_W + insF.left + insF.right;
        int frameH_Flag = CONTENT_H + insF.top + insF.bottom;
        flagFrame.setSize(frameW_Flag, frameH_Flag);

        flagFrame.setResizable(false);
        flagFrame.setLocation(100, 100);
        flagFrame.setVisible(true);


        /* ========================================
         * （B）單獨國徽：內容區 600×400
         * ======================================== */
        JFrame emblemFrame = new JFrame("中華民國國徽");
        emblemFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        EmblemPanel emblemPanel = new EmblemPanel();
        emblemPanel.setPreferredSize(new Dimension(CONTENT_W, CONTENT_H));
        emblemFrame.getContentPane().add(emblemPanel);

        emblemFrame.pack();
        Insets insE = emblemFrame.getInsets();
        int frameW_Emblem = CONTENT_W + insE.left + insE.right;
        int frameH_Emblem = CONTENT_H + insE.top + insE.bottom;
        emblemFrame.setSize(frameW_Emblem, frameH_Emblem);

        emblemFrame.setResizable(false);
        emblemFrame.setLocation(750, 100);
        emblemFrame.setVisible(true);
    }
}
