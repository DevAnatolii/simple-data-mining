package sample.dialogs;

import javax.swing.*;

public class ChoosePCATypeDialog extends JDialog {
    private JPanel contentPane;
    private JButton classicalPCAButton;
    private JButton kernelPCAButton;
    private Listener listener;

    public static interface Listener{
        void onClassicalPCAChoosen();
        void onKernelPCAChoosen();
    }

    public ChoosePCATypeDialog(Listener listener) {
        this.listener = listener;
        setContentPane(contentPane);
        setModal(true);

        classicalPCAButton.addActionListener(e -> {
            this.listener.onClassicalPCAChoosen();
            this.listener = null;
            dispose();
        });

        kernelPCAButton.addActionListener(e -> {
            this.listener.onKernelPCAChoosen();
            this.listener = null;
            dispose();
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        ChoosePCATypeDialog dialog = new ChoosePCATypeDialog(new Listener() {
            @Override
            public void onClassicalPCAChoosen() {

            }

            @Override
            public void onKernelPCAChoosen() {

            }
        });
    }
}
