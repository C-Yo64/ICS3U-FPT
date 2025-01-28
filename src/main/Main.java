package main;

import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		GamePanel gp = new GamePanel();
		window.add(gp);
		window.pack();
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		// Add the window/taskbar icon
		ImageIcon img = new ImageIcon(Main.class.getResource("/sprites/logo.png"));
		window.setIconImage(img.getImage());
	}
}
