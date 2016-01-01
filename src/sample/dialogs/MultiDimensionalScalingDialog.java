package sample.dialogs;

import javax.swing.*;
import java.awt.event.*;

public class MultiDimensionalScalingDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField skeletonSize;
    private JTextField dimentionalSize;

    private Listener listener;

    public void setData(MultiDimensionalScalingDialog data) {
    }

    public void getData(MultiDimensionalScalingDialog data) {
    }

    public boolean isModified(MultiDimensionalScalingDialog data) {
        return false;
    }

    public interface Listener{
        void onCancel();
        void onOk(int skeletonSize, int newDimensionalSize);
    }

    public MultiDimensionalScalingDialog(Listener listener) {
        this.listener = listener;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener((e)-> onCancel());

        setTitle("Set Parameter for Multidimensional scaling");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onOK() {
        int k = Integer.parseInt(skeletonSize.getText());
        int n = Integer.parseInt(dimentionalSize.getText());
        listener.onOk(k, n);
        dispose();
    }

    private void onCancel() {
        listener.onCancel();
        dispose();
    }

}
