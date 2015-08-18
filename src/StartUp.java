import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.Window.Type;
import java.awt.Toolkit;

import javax.swing.JPasswordField;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class StartUp {

	private JFrame frmChess;
	private JTextField timer_txt;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartUp window = new StartUp();
					window.frmChess.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StartUp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChess = new JFrame();
		frmChess.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\nman\\Pictures\\2012-11-03-539103.jpeg"));
		frmChess.setTitle("Chess");
		frmChess.setBounds(100, 100, 342, 228);
		frmChess.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChess.getContentPane().setLayout(null);
		
		JLabel title_lbl = new JLabel("Chess ");
		title_lbl.setFont(new Font("Times New Roman", Font.PLAIN, 26));
		title_lbl.setBounds(133, 0, 79, 56);
		frmChess.getContentPane().add(title_lbl);
		
		JButton start_but = new JButton("Start");
		start_but.setBounds(26, 122, 286, 42);
		frmChess.getContentPane().add(start_but);
		
		timer_txt = new JTextField();
		timer_txt.setEnabled(false);
		timer_txt.setBounds(147, 83, 165, 29);
		frmChess.getContentPane().add(timer_txt);
		timer_txt.setColumns(10);
		
		JRadioButton timerCheck_rad = new JRadioButton("Enable Timer");
		timerCheck_rad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (timerCheck_rad.isSelected()) {
					timer_txt.setEnabled(true);
				} else {
					timer_txt.setEnabled(false);
					timer_txt.setText("");
				}
				
			}
		});
		timerCheck_rad.setBounds(26, 72, 121, 50);
		frmChess.getContentPane().add(timerCheck_rad);
		
		JLabel timerInfo_lbl = new JLabel("Enter the time limit of the timer in minutes");
		timerInfo_lbl.setBounds(25, 36, 260, 50);
		frmChess.getContentPane().add(timerInfo_lbl);
		
		start_but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer time_limit = null;
				
				if (timerCheck_rad.isSelected()) {
					try {
						time_limit = Integer.parseInt(timer_txt.getText());
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(null, "Please enter an integer for the time limit");
						return;
					}
				}
				
				GameWindow gameWindow = new GameWindow(timerCheck_rad.isSelected(), time_limit); 
				gameWindow.setVisible(true);
				frmChess.dispose();
			}
		});
	}
}
