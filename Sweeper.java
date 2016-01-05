import java.awt.*;
import java.awt.event.*;
import java.lang.String;
import java.lang.Object;
import javax.swing.*;
import java.util.Random;

/*
 * Kendra Callwood
 * 7/24/2014
*/

public class Sweeper implements ActionListener{

	int row, col, totalNodes, bombs, flagnum, setBomb;

	JFrame frame;
	JPanel PBoard;
	JButton[] button;
	Node[] nodes;
	JLabel output, flags;
	Random rand = new Random();


	public static void main(String[] args) {
		JTextField rowF = new JTextField("5",5);
		JTextField colF = new JTextField("5",5);
		JTextField bmbF = new JTextField("4",5);

		JPanel panel = new JPanel();
		panel.add(new JLabel("Rows: "));
		panel.add(rowF);
		panel.add(new JLabel("Columns: "));
		panel.add(colF);
		panel.add(new JLabel("Bombs: "));
		panel.add(bmbF);

		int result = JOptionPane.showConfirmDialog(null, panel, 
		"Please Enter Values", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			int r = Integer.parseInt(rowF.getText());
	      		int c = Integer.parseInt(colF.getText());
			int b = Integer.parseInt(bmbF.getText());

			Sweeper sweeper = new Sweeper(r,c,b);
		}
		
		else System.exit(0);
	}

	public Sweeper(int rows, int cols, int bombnum) {

		row = rows; 
		col = cols;
		totalNodes = (row * col);
		bombs = bombnum;
		flagnum = bombnum;
		setBomb = 0;

		button = new JButton[totalNodes];
		nodes = new Node[totalNodes];

		frame = new JFrame("MineSweeper");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			PBoard = new JPanel();
			PBoard.setLayout(new GridLayout(row,col));	
			for (int i=0; i<totalNodes; i++) {
				button[i] = new JButton("");
				button[i].setPreferredSize(new Dimension(50, 50));
				button[i].addActionListener(this);
				button[i].addMouseListener(new flag());
				PBoard.add(button[i]);
			}
		frame.add("Center", PBoard);
			JPanel POut = new JPanel();
			POut.setLayout(new FlowLayout());
				output = new JLabel(" ");
				output.setHorizontalAlignment(SwingConstants.LEFT);
			POut.add(output);
				flags = new JLabel("Bombs: "+ flagnum);
				flags.setHorizontalAlignment(SwingConstants.CENTER);
			POut.add(flags);
		frame.add("South", POut);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

// Create the right number of Nodes
		for (int i=0; i<totalNodes; i++) {
			nodes[i] = new Node();
		}

	}


	public void actionPerformed(ActionEvent e) {

		output.setText("");

		int current=-1;
		Object b = e.getSource();
		for (int i=0; i<totalNodes; i++) {
			if (b == button[i]) {
				current = i;
				i = totalNodes;
			}
		}

		if (setBomb == 0) {
			setBombs(current);
			setBomb=1;
		}

		if (nodes[current].xplode == true) {
			output.setText("You Lose");
			disableButtons();
			for (int i = 0; i < totalNodes; i++) {
				if (nodes[i].xplode) {
					if (button[i].getText() != "?") {
						button[i].setText("X");
					}
					button[i].setBackground(Color.red);
				}
			}
			button[current].setBackground(Color.orange);
			prompt(row,col,bombs, "You Lost");
		}

		else if (nodes[current].seen == true) {
		}

		else update(current);
		
		if (completed()) {
			output.setText("Well Done!");
			flags.setText("Bombs: "+ 0);
			disableButtons();
			for (int i = 0; i < totalNodes; i++) {
				if (nodes[i].xplode) {
					button[i].setText("X");
					button[i].setBackground(Color.green);
				}
			}
			prompt(row,col,bombs, "You Won!");
		}
	}


// Place bombs in random spots
	void setBombs(int cnode) {
		for (int x=0; x<bombs; x++) {
			int bmb = rand.nextInt(totalNodes);
			if (bmb != cnode && nodes[bmb].xplode != true) {
				nodes[bmb].xplode = true;
				nodes[bmb].seen = true;
			}
			else {x=x-1;}

		}
	}

	void update(int current) {

		if (nodes[current].seen == true) {
			return;
		}

		nodes[current].seen = true;
		button[current].setBackground(Color.lightGray);

		int i = (current/row);
		int j = (current%row);

		if ((i+1)<row && (j>0) && nodes[(i+1)*row+(j-1)].xplode == true)  
			{nodes[current].count++;}
		if ((i+1)<row && nodes[(i+1)*row+j].xplode == true) 
			{nodes[current].count++;}
		if ((i+1)<row && (j+1)<col && nodes[(i+1)*row+(j+1)].xplode == true)  
			{nodes[current].count++;}
		if ((j+1)<col && nodes[i*row+(j+1)].xplode == true)  
			{nodes[current].count++;}
		if ((i>0) && (j+1)<col && nodes[(i-1)*row+(j+1)].xplode == true)  
			{nodes[current].count++;}
		if ((i>0) && nodes[(i-1)*row+j].xplode == true)  
			{nodes[current].count++;}
		if ((i>0) && (j>0) && nodes[(i-1)*row+(j-1)].xplode == true)  
			{nodes[current].count++;}
		if ((j>0) && nodes[i*row+(j-1)].xplode == true)  
			{nodes[current].count++;}
 
		
		if (nodes[current].count == 0) {
			if (button[current].getText() != "?") {
				button[current].setText(" ");
			}
			button[current].setEnabled(false);
			if ((i+1)<row && (j>0)) { update((i+1)*row+(j-1)); }
			if ((i+1)<row) { update((i+1)*row+j); }
			if ((i+1)<row && (j+1)<col) { update((i+1)*row+(j+1)); }
			if ((j+1)<col) { update(i*row+(j+1)); }
			if ((i>0) && (j+1)<col) { update((i-1)*row+(j+1)); }
			if ((i>0)) { update((i-1)*row+j); }
			if ((i>0) && (j>0)) { update((i-1)*row+(j-1)); }
			if ((j>0)) { update(i*row+(j-1)); }
		}

		else {
			button[current].setText(String.valueOf(nodes[current].count)); }


	}

	boolean completed() {
		boolean comp;
		for (int i = 0; i<totalNodes; i++) {
			if (nodes[i].seen == false) {
				return false;
			}
		}
		return true;
	}

	void disableButtons() {
		for (int i = 0; i < totalNodes; i++) {
			button[i].removeActionListener(this);
		}
	}

	public void prompt(int rr, int cc, int bb, String WoL) {

		int result = JOptionPane.showConfirmDialog(null, "Play Again?", 
		WoL, JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.OK_OPTION) {

			frame.dispose();

			JTextField rowF = new JTextField(String.valueOf(rr),5);
			JTextField colF = new JTextField(String.valueOf(cc),5);
			JTextField bmbF = new JTextField(String.valueOf(bb),5);

			JPanel panel = new JPanel();
			panel.add(new JLabel("Rows: "));
			panel.add(rowF);
			panel.add(new JLabel("Columns: "));
			panel.add(colF);
			panel.add(new JLabel("Bombs: "));
			panel.add(bmbF);

			int result2 = JOptionPane.showConfirmDialog(null, panel, 
			"Please Enter Values", JOptionPane.OK_CANCEL_OPTION);
			if (result2 == JOptionPane.OK_OPTION) {
				int r = Integer.parseInt(rowF.getText());
		      		int c = Integer.parseInt(colF.getText());
				int b = Integer.parseInt(bmbF.getText());

				Sweeper sweeper = new Sweeper(r,c,b);
			}
		
			else System.exit(0);
		}
	}

	class flag extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			int current=-1;
			Object b = e.getSource();
			for (int i=0; i<totalNodes; i++) {
				if (b == button[i]) {
					current = i;
				}
			}
			if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
				if (button[current].getText() == "") {
					button[current].setText("?");
					button[current].setEnabled(false);
					flagnum--;
					flags.setText("Bombs: "+ flagnum);
				}
				else if (button[current].getText() == "?") {
					button[current].setText("");
					button[current].setEnabled(true);
					flagnum++;
					flags.setText("Bombs: "+ flagnum);
				}
			}
		}
	}

	
}

