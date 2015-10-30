package jp.saka1029.cspj.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class JListTest {

private static JList<String> list;

    public static void main(String[] args) {

        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("item1");
        model.addElement("item2");
        model.addElement("item3");
        list = new JList<String>(model);

        JButton open = new JButton("open");
        open.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
                model.removeAllElements();
            }
        });
        JFrame frame = new JFrame();
        frame.setSize(400, 400);
        Container con = frame.getContentPane();
        con.setLayout(new BorderLayout());

        con.add(open, BorderLayout.LINE_START);
        con.add(list, BorderLayout.CENTER);
        con.add(new JScrollPane(list));
        frame.setVisible(true);

    }
}

