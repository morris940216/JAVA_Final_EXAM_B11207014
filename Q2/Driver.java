
import javax.swing.*;

public class Driver {
    public static void main(String[] args) {
        JFrame f = new JFrame("Karel Keyboard Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new KarelPanel());
        f.pack();                 // 依 KarelPanel 的 preferredSize
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}