package view;
import javax.swing.*;
import java.awt.*;

public class EditDlg extends OKCancelDialog {
    private JTextField  edit;

    public EditDlg(JFrame frame, String title) {
        super(frame, title);
        edit = new JTextField();
        setLayout(new BorderLayout());
        add("Center", edit);
        add("South", btnPanel);
        pack();
        setResizable(false);
    }

    public String getValue()        { return edit.getText(); }

    @Override
    protected boolean processOK()   { return true; }

    @Override
    protected void processCancel()  {}
}
