import javax.swing.JFrame;

public class SlingshotFrame {
    public static void main(String[] args) {
        JFrame f = new JFrame("Slingshot In Place");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new SlingshotPanel());   // 遊戲面板
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
