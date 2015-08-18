import java.awt.Color;
import java.awt.EventQueue;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JTextField;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.border.Border;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.border.LineBorder;

import java.awt.Canvas;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//TODO: timer logic
//TODO: place pieces function. requires enum

public class GameWindow {
    
	public static final int TILE_WIDTH = 55;
	public static final int TILE_HEIGHT = 50;
	public static final int A1_POSITION_X = 12;
	public static final int A1_POSITION_Y = 405;
	public static final int TILE_DIFF_X = 61 ; //distance between tiles on the x axis 
	public static final int TILE_DIFF_Y = 56; //distance between tiles on the y axis 
	
	public static final int TOTAL_NUM_TILES = 64;
	public static final int TOTAL_NUM_PIECES = 32;
	public static final int NUM_COLS = 8;
	public static final int NUM_ROWS = 8;
	public static final int NUM_TILES_DIAGONAL = 8;
	public static final int WHITE_PAWN_START_ROW = 1; //array indexed
	public static final int BLACK_PAWN_START_ROW = 6; //array indexed
	
	private JFrame frame;
	private JPanel board_pnl;
	private JPanel p1graveyard_pbl;
	private JPanel p2graveyard_pbl;
	
	private JLabel wtakenPawn_txt;
	private JLabel wtakenRook_txt;
	private JLabel wtakenBishop_txt;
	private JLabel wtakenKnight_txt;
	private JLabel wtakenQueen_txt;
	
	private JLabel btakenPawn_txt;
	private JLabel btakenRook_txt;
	private JLabel btakenBishop_txt;
	private JLabel btakenKnight_txt;
	private JLabel btakenQueen_txt;
	
	private boolean timerEnabled;
	private Integer time_limit;
	private Time player1_Time;
	private Time player2_Time;
	private JTextField p1timer_txt;
	private JTextField p2timer_txt;
	
	private int currTurn;
	public static final int WHITES_TURN = 0;
	public static final int BLACKS_TURN = 1;
	private GameState state;
	private boolean isCheck;
	private List<Tile> checkingTiles;
	
	private boolean hasWhiteKingMoved; // These boolean values will be used to implement castling logic
	private boolean hasBlackKingMoved;
	private boolean hasLeftWhiteRookMoved;
	private boolean hasLeftBlackRookMoved;
	private boolean hasRightWhiteRookMoved;
	private boolean hasRightBlackRookMoved;
	
	int[] takenPawns = new int[2]; // taken pieces 
	int[] takenRooks = new int[2];
	int[] takenBishops = new int[2];
	int[] takenKnights = new int[2];
	int[] takenQueens = new int[2];
	
	private Tile[][] board; // A 2D array of tiles representing a board 
	private Tile selectedTile; // Currently selected piece
    
	
	/**
	 * Create the application.
	 */
	public GameWindow(boolean timerEnabled, Integer time_limit) {
		initialize();
		initializeFields(timerEnabled, time_limit);
		placePieces(board);
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// Sets up the components and containers for the window
		frame = new JFrame();
		frame.setBounds(100, 100, 843, 538);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//Creating board of buttons (tiles) contained in a panel
		board_pnl = new JPanel();
		board_pnl.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		board_pnl.setBounds(150, 13, 511, 465);
		frame.getContentPane().add(board_pnl);
		board_pnl.setLayout(null);
		
		board = new Tile[NUM_ROWS][NUM_COLS];
		
		for (int i = 0; i < NUM_ROWS; i++) {
			for (int j = 0; j < NUM_COLS; j++) {
				board[i][j] = new Tile("", j, i); // new button created for the current tile
				board[i][j].setBounds(A1_POSITION_X + j*TILE_DIFF_X,
						                A1_POSITION_Y - i*TILE_DIFF_Y, 
						                TILE_WIDTH, TILE_HEIGHT);
				
                setTileEvents(board[i][j]);
				board_pnl.add(board[i][j]);
			}
		}
		
		JLabel player1_lbl = new JLabel("Player 1");
		player1_lbl.setFont(new Font("Tahoma", Font.PLAIN, 17));
		player1_lbl.setBounds(12, 8, 110, 30);
		frame.getContentPane().add(player1_lbl);
		
		p1timer_txt = new JTextField();
		p1timer_txt.setBorder(new LineBorder(Color.BLACK, 1, true));
		p1timer_txt.setEnabled(false);
		p1timer_txt.setFont(new Font("Tahoma", Font.PLAIN, 17));
		p1timer_txt.setBounds(12, 38, 133, 30);
		frame.getContentPane().add(p1timer_txt);
		p1timer_txt.setColumns(10);
		
		JLabel player2_lbl = new JLabel("Player 2");
		player2_lbl.setFont(new Font("Tahoma", Font.PLAIN, 17));
		player2_lbl.setBounds(670, 13, 110, 30);
		frame.getContentPane().add(player2_lbl);
		
		p2timer_txt = new JTextField();
		p2timer_txt.setBorder(new LineBorder(Color.BLACK));
		p2timer_txt.setEnabled(false);
		p2timer_txt.setFont(new Font("Tahoma", Font.PLAIN, 17));
		p2timer_txt.setColumns(10);
		p2timer_txt.setBounds(670, 43, 133, 30);
		frame.getContentPane().add(p2timer_txt);
		
		JPanel p1graveyard_pnl = new JPanel();
		p1graveyard_pnl.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		p1graveyard_pnl.setBounds(12, 81, 126, 397);
		frame.getContentPane().add(p1graveyard_pnl);
		p1graveyard_pnl.setLayout(null);
		
		JLabel pawn1_lbl = new JLabel("Pawn");
		pawn1_lbl.setFont(new Font("Tahoma", Font.PLAIN, 17));
		pawn1_lbl.setBounds(12, 13, 56, 16);
		p1graveyard_pnl.add(pawn1_lbl);
		
		JLabel lblNewLabel = new JLabel("Rook");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel.setBounds(12, 71, 56, 16);
		p1graveyard_pnl.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Knight");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel_1.setBounds(12, 114, 56, 26);
		p1graveyard_pnl.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Bishop");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel_2.setBounds(12, 169, 56, 26);
		p1graveyard_pnl.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Queen");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel_3.setBounds(12, 221, 56, 26);
		p1graveyard_pnl.add(lblNewLabel_3);
		
		wtakenPawn_txt = new JLabel("x0");
		wtakenPawn_txt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		wtakenPawn_txt.setBounds(12, 42, 56, 16);
		p1graveyard_pnl.add(wtakenPawn_txt);
		
		wtakenRook_txt = new JLabel("x0");
		wtakenRook_txt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		wtakenRook_txt.setBounds(12, 96, 56, 16);
		p1graveyard_pnl.add(wtakenRook_txt);
		
		wtakenBishop_txt = new JLabel("x0");
		wtakenBishop_txt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		wtakenBishop_txt.setBounds(12, 204, 56, 16);
		p1graveyard_pnl.add(wtakenBishop_txt);
		
		wtakenKnight_txt = new JLabel("x0");
		wtakenKnight_txt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		wtakenKnight_txt.setBounds(12, 150, 56, 16);
		p1graveyard_pnl.add(wtakenKnight_txt);
		
		wtakenQueen_txt = new JLabel("x0");
		wtakenQueen_txt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		wtakenQueen_txt.setBounds(12, 260, 56, 16);
		p1graveyard_pnl.add(wtakenQueen_txt);
		
		p2graveyard_pbl = new JPanel();
		p2graveyard_pbl.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		p2graveyard_pbl.setBounds(670, 81, 126, 397);
		frame.getContentPane().add(p2graveyard_pbl);
		p2graveyard_pbl.setLayout(null);
		
		JLabel label = new JLabel("Queen");
		label.setFont(new Font("Tahoma", Font.PLAIN, 17));
		label.setBounds(12, 221, 56, 26);
		p2graveyard_pbl.add(label);
		
		JLabel label_1 = new JLabel("Bishop");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 17));
		label_1.setBounds(12, 169, 56, 26);
		p2graveyard_pbl.add(label_1);
		
		JLabel label_2 = new JLabel("Knight");
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 17));
		label_2.setBounds(12, 114, 56, 26);
		p2graveyard_pbl.add(label_2);
		
		JLabel label_3 = new JLabel("Rook");
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 17));
		label_3.setBounds(12, 71, 56, 16);
		p2graveyard_pbl.add(label_3);
		
		JLabel label_4 = new JLabel("Pawn");
		label_4.setFont(new Font("Tahoma", Font.PLAIN, 17));
		label_4.setBounds(12, 13, 56, 16);
		p2graveyard_pbl.add(label_4);
		
		btakenQueen_txt = new JLabel("x0");
		btakenQueen_txt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btakenQueen_txt.setBounds(12, 257, 56, 16);
		p2graveyard_pbl.add(btakenQueen_txt);
		
		btakenBishop_txt = new JLabel("x0");
		btakenBishop_txt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btakenBishop_txt.setBounds(12, 201, 56, 16);
		p2graveyard_pbl.add(btakenBishop_txt);
		
		btakenKnight_txt = new JLabel("x0");
		btakenKnight_txt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btakenKnight_txt.setBounds(12, 147, 56, 16);
		p2graveyard_pbl.add(btakenKnight_txt);
		
		btakenRook_txt = new JLabel("x0");
		btakenRook_txt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btakenRook_txt.setBounds(12, 93, 56, 16);
		p2graveyard_pbl.add(btakenRook_txt);
		
		btakenPawn_txt = new JLabel("x0");
		btakenPawn_txt.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btakenPawn_txt.setBounds(12, 39, 56, 16);
		p2graveyard_pbl.add(btakenPawn_txt);
	}
	
	private void initializeFields(boolean timerEnabled, Integer time_limit) {
		// Sets up fields for the start of a new game
		this.timerEnabled = timerEnabled;
		if (timerEnabled) {
			this.time_limit = time_limit;
			this.player1_Time = new Time (0, time_limit, 0);
			this.player2_Time = new Time (0, time_limit, 0);
		} 
		
		currTurn = WHITES_TURN;
		state = GameState.IDLE;
		isCheck = false;
		checkingTiles = new ArrayList<Tile>();
	}
	
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	
	private void placePieces(Tile[][] board) {
		// Sets up the chess board for a new game with all pieces
		int number_of_pawns = 8;
		
		//White pieces
		board[convertRow('A')][convertCol(1)].placePiece(Piece.Rook_W);
		board[convertRow('A')][convertCol(8)].placePiece(Piece.Rook_W);
		board[convertRow('A')][convertCol(2)].placePiece(Piece.Knight_W);
		board[convertRow('A')][convertCol(7)].placePiece(Piece.Knight_W);
		board[convertRow('A')][convertCol(3)].placePiece(Piece.Bishop_W);
		board[convertRow('A')][convertCol(6)].placePiece(Piece.Bishop_W);
		board[convertRow('A')][convertCol(4)].placePiece(Piece.Queen_W);
		board[convertRow('A')][convertCol(5)].placePiece(Piece.King_W);
		
		for (int i = 1; i <= number_of_pawns; i++) {
			board[convertRow('B')][convertCol(i)].placePiece(Piece.Pawn_W);
		}
		
		//Black pieces
		board[convertRow('H')][convertCol(1)].placePiece(Piece.Rook_B);
		board[convertRow('H')][convertCol(8)].placePiece(Piece.Rook_B);
		board[convertRow('H')][convertCol(2)].placePiece(Piece.Knight_B);
		board[convertRow('H')][convertCol(7)].placePiece(Piece.Knight_B);
		board[convertRow('H')][convertCol(3)].placePiece(Piece.Bishop_B);
		board[convertRow('H')][convertCol(6)].placePiece(Piece.Bishop_B);
		board[convertRow('H')][convertCol(4)].placePiece(Piece.Queen_B);
		board[convertRow('H')][convertCol(5)].placePiece(Piece.King_B);
		
		for (int i = 1; i <= number_of_pawns; i++) {
			board[convertRow('G')][convertCol(i)].placePiece(Piece.Pawn_B);
		}
	}
	
	private void setTileEvents(Tile tile) {
		tile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				Tile tile = (Tile)e.getSource();
				tile.setHoverBorder();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				Tile tile = (Tile)e.getSource(); 
				tile.setInitialBorder();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				Tile tile = (Tile)e.getSource();
				switch(state) {
				  case IDLE:
					  if (currTurn == WHITES_TURN && tile.isWhite() || currTurn == BLACKS_TURN && tile.isBlack()) {
						  //SELECT PIECE
						  tile.select(); // the current piece is now selected 
						  selectedTile = tile;
						  state = GameState.SELECTED; // advance the game state
						  showPossibleMoves(tile); // highlight legal moves
						  
					  } else {
						  // INCORRECT
						  checkWrongTurn(tile);
						  tile.setErrorBorder();
						  
					  }
                  
				  break;
				  case SELECTED:
					  if(!tile.isLegal()) {
						  // INCORRECT
						  tile.setErrorBorder();
						  
					  } else if (tile.isSelected()) {
						  // DESELECT PIECE
						  tile.deselect();
						  illegaliseAll(board);
						  state = GameState.IDLE;
						  selectedTile = null;
						  
					  } else if (tile.isLegal()) {
						  Tile InitialSelectedTile = selectedTile.clone(); // A clone of the initial tile of the selected piece
						  Tile InitialMoveTile = tile.clone(); // A clone of the initial tile that the selected piece will move to
						  // MOVE PIECE
						  movePiece(selectedTile, tile);
						  
						  if (isKingChecked()) { // if the king checked by this move then the move was illegal
							  // reverse the move by setting tiles to their original state
							  selectedTile.setFields(InitialSelectedTile.getPiece(), InitialSelectedTile.isLegal(), InitialSelectedTile.isSelected());
							  tile.setFields(InitialMoveTile.getPiece(), InitialMoveTile.isLegal(), InitialMoveTile.isSelected());;
							  
							  getKing().setErrorBorder();
							  
							  JOptionPane.showMessageDialog(frame, "Illegal move. The king is checked");
							  return;
						  }
						  
						  //Checks if castling was performed
						  if (InitialSelectedTile.isKing()) {
							  int king_x = tile.x(); int king_y = tile.y(); // kings coordinates
							  if (selectedTile.x() - InitialMoveTile.x() == 2) { //Did the king move to the left?
								  board[king_y][king_x+1].placePiece(board[king_y][convertCol(1)].getPiece());
								  board[king_y][0].removePiece();
							  } else if (selectedTile.x() - InitialMoveTile.x() == -2) {//Did the king move to the right?
								  board[king_y][king_x-1].placePiece(board[king_y][convertCol(NUM_COLS)].getPiece());
								  board[king_y][convertCol(NUM_COLS)].removePiece();
							  }
						  }
						  
						  //Check if the pawn is to be promoted
						  if (InitialSelectedTile.getPiece() == Piece.Pawn_W && tile.y() == convertRow('H') ||
							  InitialSelectedTile.getPiece() == Piece.Pawn_B && tile.y() == convertRow('A')) {
							  promoteTile(tile);
						  }
						  
						  // Add the taken piece to the players collection of taken pieces
						  if (InitialMoveTile.hasPiece()) {
							  takePiece(InitialMoveTile.getPiece());
						  }
						  
						  selectedTile.deselect();
						  illegaliseAll(board);
						  
						  checkCastlingPieces(InitialSelectedTile);
						  checkCastlingPieces(InitialMoveTile);
						  selectedTile = null;
						  state = GameState.IDLE;
						  if (!checkingTiles.isEmpty()) checkingTiles.clear();
						  changeTurn();
						  
						  if (isKingChecked()) {
							  checkingTiles = accuseAllEnemyTiles(); 
							  if (isCheckMate()) {
								  JOptionPane.showMessageDialog(frame, "CHECKMATE! " + (currTurn == WHITES_TURN ? "Blacks" : "Whites") + " wins!");
								  getKing().setErrorBorder();
								  state = GameState.CHECKMATE;
							  } else {
								  JOptionPane.showMessageDialog(frame, "CHECK!");	  
							  }
						  }
						  
					  }
				  break;
				}
			}
		});
	}
	
	//setTileEvents helper
	private void movePiece(Tile source, Tile destination) {
		//moves the piece on source to the destination
		destination.placePiece(source.getPiece());
		source.removePiece();
	}
	
	private void promoteTile(Tile tile) {
		Piece promotedPiece = null;
		
		Object[] possibilities = {"Queen", "Rook", "Bishop", "Knight"};
		String choosenPiece = (String)JOptionPane.showInputDialog(
		                    frame,
		                    "Choose a new piece",
		                    "Promote your pawn",
		                    JOptionPane.PLAIN_MESSAGE,
		                    new ImageIcon(currTurn == WHITES_TURN ? "images/white_pawn.png" : "images/black_pawn.png"),
		                    possibilities,
		                    "Queen");
		
		switch(choosenPiece) {
		case "Queen":
			promotedPiece = (currTurn == WHITES_TURN ? Piece.Queen_W : Piece.Queen_B);
			break;
		case "Rook":
			promotedPiece = (currTurn == WHITES_TURN ? Piece.Rook_W : Piece.Rook_B);
			break;
		case "Bishop":
			promotedPiece = (currTurn == WHITES_TURN ? Piece.Bishop_W : Piece.Bishop_B);
			break;
		case "Knights":
			promotedPiece = (currTurn == WHITES_TURN ? Piece.Knight_W : Piece.Knight_B);
			break;
		}
		
		tile.placePiece(promotedPiece);
		
	}
	
	private void illegaliseAll(Tile[][] board) {
		for (int i = 0; i < board.length; i++) {
			for (Tile tile : board[i]) {
				tile.illegalise();
			}
		}
		
	}
		
	private void changeTurn() {
		if (currTurn == WHITES_TURN) {
			currTurn = BLACKS_TURN;
		} else {
			currTurn = WHITES_TURN;
		}
	} 
	
	private void checkWrongTurn(Tile tile) {
		if (tile.hasPiece() && tile.isWhite() && currTurn == BLACKS_TURN ||
			tile.hasPiece() && tile.isBlack() && currTurn == WHITES_TURN) {
			JOptionPane.showMessageDialog(frame, "It's " + (currTurn == WHITES_TURN ? "whites" : "blacks") + " turn");
			
		}
		
	}
	
	private void checkCastlingPieces(Tile suspect) {
		// sets castings boolean values depending on if the suspect piece was moved or taken		
		if (isCastlingImpossible() || suspect.getPiece() == null) {
			return;
		}
		
		switch(suspect.getPiece()) {
		case King_B: 
			hasBlackKingMoved = true;
			break;
		case King_W:
			hasWhiteKingMoved = true;
			break;
		case Rook_W:
			if (suspect.x() == 0) {
				hasLeftWhiteRookMoved = true;
			} else {
				hasRightWhiteRookMoved = true;
			}
			break;
		case Rook_B:
			if (suspect.x() == 0) {
				hasLeftBlackRookMoved = true;
			} else {
				hasRightBlackRookMoved = true;
			}
			break;
			
		}
	}

	private boolean isCastlingImpossible() {
		if (currTurn == WHITES_TURN) {
			return hasWhiteKingMoved || hasLeftWhiteRookMoved && hasRightWhiteRookMoved;
		} else {
			return hasBlackKingMoved || hasLeftBlackRookMoved && hasRightBlackRookMoved;
		}
	}
	
	private int convertRow(char row) {
		// converts row characters into their respective array indexes 
		int asciiForA = 65;
		return (int)row - asciiForA;
	} 
	
	private boolean onBoard(int col, int row) {
		// checks if the coordinates passed are on the chess board (indexed on board array)
		return (0 <= col && col < NUM_COLS) && (0 <= row && row < NUM_ROWS);
	}
	
	private int convertCol(int col) {
		// returns an array index based on the colomn
		return col - 1;
	}
	
	private boolean isCheckMate() {
	    // Checks if a checkmate has occured
		Tile king = getKing();
		int pos_x = king.x();
		int pos_y = king.y();
		int col;
		int row;
		
		// Can the king move?
		Piece kingPiece = king.getPiece();// The king piece is removed temporarily to allow for more effect checking detection
		king.removePiece();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				
				col = pos_x + i; row = pos_y + j;
				if(isTileSafe(col,row)) {
					king.placePiece(kingPiece); // The king piece is returned
					return false;
				}
				
			}
		}
		king.placePiece(kingPiece); // The king piece is returned
		
		// If the king can't move and their are more than 1 checker, checkmate will occur
		if (checkingTiles.size() > 1) {
			return true;
		}
		
		// Can the checkers be killed or blocked?
		changeTurn();
		for (Tile tile: checkingTiles) {
			if (isChecked(tile) || canBlock(tile)) {
				changeTurn();
				return false;
			}
		}
		changeTurn();
		
		return true;
	}
	
	//isCheckMate helper function
 	private boolean isTileSafe(int col, int row) {
		// returns true if the king can move to this tile
		return onBoard(col, row) && board[row][col].isEmpty() && !isChecked(board[row][col]);
	} 
	
 	private boolean canBlock(Tile tile) {
 		//returns true if the tile's attack can be blocked by the enemy
		switch(tile.getPiece()) {
		case Rook_W:
		case Rook_B:
			return canBlockRook(tile);
		case Bishop_W:
		case Bishop_B:
			return canBlockBishop(tile);
		case Queen_W:
		case Queen_B:
			return canBlockRook(tile) || canBlockBishop(tile); 
		default:
			//other pieces can not be blocked
		}
 		
 		return false;
 	}
 	
 	private boolean canBlockRook(Tile tile) {
		// if the pieces's path is able to be blocked: true will be returned
		int pos_x = tile.x(); // x coordinate of rook
		int pos_y = tile.y(); // y coordinate of rook
		
		changeTurn(); Tile king = getKing(); changeTurn();
		int king_x = king.x();
		int king_y = king.y();
		
		//LEFT:
		if (king_y == pos_y && king_x < pos_x) {
			for (int i = pos_x - 1; i >= 0; i--) {
				if (board[pos_y][i].hasPiece()) break;
				if (isChecked(board[pos_y][i])) return true;
			}
		}
		
		//RIGHT: 
		if (king_y == pos_y && king_x > pos_x) {
			for (int i = pos_x + 1; i < NUM_COLS; i++) {
				if (board[pos_y][i].hasPiece()) break;
				if (isChecked(board[pos_y][i])) return true;
			}
		}
		
		//DOWN: 
		if (king_y < pos_y && king_x == pos_x) {
			for (int j = pos_y - 1; j >= 0; j--) {
				if (board[j][pos_x].hasPiece()) break;
				if (isChecked(board[j][pos_x])) return true;
			}
		}
		//UP:
		if (king_y > pos_y && king_x == pos_x) {
			for (int j = pos_y + 1; j < NUM_ROWS; j++) {
				if (board[j][pos_x].hasPiece()) break;
				if (isChecked(board[j][pos_x])) return true;
			}
		}
		
		return false;
 	}
 	
 	private boolean canBlockBishop(Tile tile) {
		// if the pieces's path is able to be blocked: true will be returned
		int pos_x = tile.x(); // x coordinate of rook
		int pos_y = tile.y(); // y coordinate of rook
		
		changeTurn(); Tile king = getKing(); changeTurn();
		int king_x = king.x();
		int king_y = king.y();
		
		int i = 1;
		//NW:
		if (king_y > pos_y && king_x < pos_x) {
			while(!board[pos_y+i][pos_x-i].hasPiece()) {
				if (isChecked(board[pos_y+i][pos_x-i++])) return true;
			}
		}
		
		i = 1;
		//NE: 
		if (king_y > pos_y && king_x > pos_x) {
			while(!board[pos_y+i][pos_x+i].hasPiece()){
				if (isChecked(board[pos_y+i][pos_x+i++])) return true;
			}
		}
		
		i = 1;
		//SE: 
		if (king_y < pos_y && king_x > pos_x) {
			while(!board[pos_y-i][pos_x-i].hasPiece()){
				if (isChecked(board[pos_y-i][pos_x-i++])) return true;
			}
		}
		
		i = 1;
		//SW: 
		if (king_y < pos_y && king_x < pos_x) {
			while(!board[pos_y-i][pos_x-i].hasPiece()){
				if (isChecked(board[pos_y-i][pos_x-i++])) return true;
			}
		}
		
		return false;
 	}
 	//--------------------------
 	
	private List<Tile> accuseAllEnemyTiles() {
		// returns a list of all enemy tiles that are checking the current king
		List<Tile> suspectTiles = new ArrayList<Tile>(); // This list will contain all tiles that are checking the king
		
		for (Tile[] row: board) {
			for (Tile tile: row) {
				if ((currTurn == WHITES_TURN ? tile.isBlack() : tile.isWhite()) && accuse(tile)) {
					suspectTiles.add(tile);
				}
			}
		}
		
		return suspectTiles;
		
	}
	
	private boolean accuse(Tile tile) {
		// returns true if the tile is checking the current king
		int pos_x = tile.x();
		int pos_y = tile.y();
		
		switch(tile.getPiece()) {
		case Pawn_W:
			if (accuseWhitePawn(pos_x, pos_y)) return true;
			break;
		case Pawn_B:
			if (accuseBlackPawn(pos_x, pos_y)) return true;
			break;
		case Rook_W:
		case Rook_B:
			if (accuseRook(pos_x, pos_y)) return true;
			break;
		case Knight_W:
		case Knight_B:
			if (accuseKnight(pos_x, pos_y)) return true;
			break;
		case Bishop_W:
		case Bishop_B:
			if (accuseBishop(pos_x, pos_y)) return true;
			break;
		case Queen_W:
		case Queen_B:
			if (accuseQueen(pos_x, pos_y)) return true;
			break;
		}
		
		return false;
	}
	
	//accuse helper functions
	private boolean accuseWhitePawn(int pos_x, int pos_y) {
		// returns true if the pawn in this position is checking a king
		if (onBoard(pos_x-1, pos_y+1) && board[pos_y+1][pos_x-1].isKing() && board[pos_y+1][pos_x-1].isBlack()) {
			return true;
		} else if (onBoard(pos_x+1, pos_y+1) && board[pos_y+1][pos_x+1].isKing() && board[pos_y+1][pos_x+1].isBlack()) {
			return true;
		}
		
		return false;
	}
	
	private boolean accuseBlackPawn(int pos_x, int pos_y) {
		// returns true if the pawn in this position is checking a king
		if (onBoard(pos_x-1, pos_y-1) && board[pos_y-1][pos_x-1].isKing() && board[pos_y-1][pos_x-1].isWhite()) {
			return true;
		} else if (onBoard(pos_x+1, pos_y-1) && board[pos_y-1][pos_x+1].isKing() && board[pos_y-1][pos_x+1].isWhite()) {
			return true;
		};
		
		return false;
	}

	private boolean accuseRook(int pos_x, int pos_y) {
		// returns true if the rook or queen in this position is checking a king
		//LEFT: try to legalise all tiles to the left of the rook
		for (int i = pos_x - 1; i >= 0; i--) {
			//if this tile failed to legalise then break the loop
			if (board[pos_y][i].isKing() && (currTurn == WHITES_TURN ? board[pos_y][i].isWhite() :board[pos_y][i].isBlack())) {
				return true;
			} else if (board[pos_y][i].hasPiece()) {
				break;
			}
		}
		
		//RIGHT: try to legalise all tiles to the right of the rook
		for (int i = pos_x + 1; i < NUM_COLS; i++) {
			//if this tile failed to legalise then break the loop
			if (board[pos_y][i].isKing() && (currTurn == WHITES_TURN ? board[pos_y][i].isWhite() :board[pos_y][i].isBlack())){
				return true;
			} else if (board[pos_y][i].hasPiece()) {
				break;
			}
		}
		
		//DOWN: try to legalise all tiles below the rook
		for (int j = pos_y - 1; j >= 0; j--) {
			//if this tile failed to legalise then break the loop
			if (board[j][pos_x].isKing() && (currTurn == WHITES_TURN ? board[j][pos_x].isWhite() :board[j][pos_x].isBlack())) {
				return true;
			} else if (board[j][pos_x].hasPiece()) {
				break;
			}
		}
		
		//UP: try to legalise all tiles above the rook
		for (int j = pos_y + 1; j < NUM_ROWS; j++) {
			//if this tile failed to legalise then break the loop
			if (board[j][pos_x].isKing() && (currTurn == WHITES_TURN ? board[j][pos_x].isWhite() :board[j][pos_x].isBlack())) {
				return true;
			} else if (board[j][pos_x].hasPiece()) {
				break;
			}
		}
		
		return false;
	}
		
	private boolean accuseKnight(int pos_x, int pos_y) {
		// returns true if the knight in this position is checking a king
		if (onBoard(pos_x-2,pos_y+1) && board[pos_y+1][pos_x-2].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
			return true;
		}
		
		if (onBoard(pos_x-2,pos_y-1) && board[pos_y-1][pos_x-2].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
			return true;
		}
		
		if (onBoard(pos_x+2,pos_y+1) && board[pos_y+1][pos_x+2].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
			return true;
		}
		
		if (onBoard(pos_x+2,pos_y-1) && board[pos_y-1][pos_x+2].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
			return true;
		}
		
		if (onBoard(pos_x-1,pos_y+2) && board[pos_y+2][pos_x-1].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
			return true;
		}
		
		if (onBoard(pos_x+1,pos_y+2) && board[pos_y+2][pos_x+1].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
			return true;
		}
		
		if (onBoard(pos_x+1,pos_y-2) && board[pos_y-2][pos_x+1].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
			return true;
		}
		
		if (onBoard(pos_x-1,pos_y-2) && board[pos_y-2][pos_x-1].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
			return true;
		}
		
		return false;
	}
		
	private boolean accuseBishop(int pos_x, int pos_y) {
		// returns true if the bishop in this position is checking a king
		//NW
		System.out.println();
		for (int i = 1; i < NUM_TILES_DIAGONAL; i++) {
		    if (!onBoard(pos_x-i, pos_y+i)) {
		    	break;
		    } else if (board[pos_y+i][pos_x-i].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
				return true;
			} else if (board[pos_y+i][pos_x-i].hasPiece()){
				break;
			}
		}
		
		//NE
		for (int i = 1; i < NUM_TILES_DIAGONAL; i++) {
		    if (!onBoard(pos_x+i, pos_y+i)) {
		    	break;
		    } else if (board[pos_y+i][pos_x+i].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
				return true;
			} else if (board[pos_y+i][pos_x+i].hasPiece()){
				break;
			}
		}
		
		//SE
		for (int i = 1; i < NUM_TILES_DIAGONAL; i++) {
		    if (!onBoard(pos_x+i, pos_y-i)) {
		    	break;
		    } else if (board[pos_y-i][pos_x+i].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
				return true;
			} else if (board[pos_y-i][pos_x+i].hasPiece()){
				break;
			}
		}
		
		//SW
		for (int i = 1; i < NUM_TILES_DIAGONAL; i++) {
		    if (!onBoard(pos_x-i, pos_y-i)) {
		    	break;
		    } else if (board[pos_y-i][pos_x-i].getPiece() == (currTurn == WHITES_TURN ? Piece.King_W : Piece.King_B)) {
				return true;
			} else if (board[pos_y-i][pos_x-i].hasPiece()){
				break;
			}
		}
		
		return false;
	}
		
	private boolean accuseQueen(int pos_x, int pos_y) {
		return accuseBishop(pos_x, pos_y) || accuseRook(pos_x, pos_y);
	}
	//------------------------
 	
	private void takePiece(Piece piece) {
		// adds the piece passed to the number of piece taken by the current player
		switch(piece.getType()) {
		case Pawn:
            takenPawns[piece.getColour()]++;
            if (piece.isWhite()) {
            	wtakenPawn_txt.setText("x"+takenPawns[piece.getColour()]);
            } else {
            	btakenPawn_txt.setText("x"+takenPawns[piece.getColour()]);
            }
			break;
			
		case Rook:
			takenRooks[piece.getColour()]++;
            if (piece.isWhite()) {
            	wtakenRook_txt.setText("x"+takenRooks[piece.getColour()]);
            } else {
            	btakenRook_txt.setText("x"+takenRooks[piece.getColour()]);
            }
			break;
			
		case Knight:
			takenKnights[piece.getColour()]++;
            if (piece.isWhite()) {
            	wtakenKnight_txt.setText("x"+takenKnights[piece.getColour()]);
            } else {
            	btakenKnight_txt.setText("x"+takenKnights[piece.getColour()]);
            }
			break;
			
		case Bishop:
			takenBishops[piece.getColour()]++;
            if (piece.isWhite()) {
            	wtakenBishop_txt.setText("x"+takenBishops[piece.getColour()]);
            } else {
            	btakenBishop_txt.setText("x"+takenBishops[piece.getColour()]);
            }
			break;
			
		case Queen:
			takenQueens[piece.getColour()]++;
            if (piece.isWhite()) {
            	wtakenQueen_txt.setText("x"+takenQueens[piece.getColour()]);
            } else {
            	btakenQueen_txt.setText("x"+takenQueens[piece.getColour()]);
            }
			break;
			
		}
		
	}
	
	private void showPossibleMoves(Tile selectedTile) {
		// Highlights the possible tiles the player can select next
		switch(selectedTile.getPiece()) {
		case Pawn_W:
			whitePawnPossibleMoves(selectedTile);
			break;
		case Pawn_B:
			blackPawnPossibleMoves(selectedTile);
			break;
		case Rook_W:
		case Rook_B:
			rookPossibleMoves(selectedTile);
			break;
		case Knight_W:
		case Knight_B:
			knightPossibleMoves(selectedTile);
			break;
		case Bishop_W:
		case Bishop_B:
			bishopPossibleMoves(selectedTile);
			break;
		case Queen_W:
		case Queen_B:
			rookPossibleMoves(selectedTile);
			bishopPossibleMoves(selectedTile);
			break;
		case King_W:
		case King_B:
			kingPossibleMoves(selectedTile);
			break;
		}
	}
	
	//Possible moves helper function
	private void whitePawnPossibleMoves(Tile tile) {
		int pos_x = tile.x(); // x coordinate of white pawn
		int pos_y = tile.y(); // y coordinate of white pawn
		
		// forward path
		if (onBoard(pos_x,pos_y+1) && board[pos_y+1][pos_x].isEmpty()) {
			board[pos_y+1][pos_x].legalise();
			
			if (pos_y == WHITE_PAWN_START_ROW && onBoard(pos_x,pos_y+2) && board[pos_y+2][pos_x].isEmpty()) {
				board[pos_y+2][pos_x].legalise();
			} 
		} 
		
		//diagonal attack
		if (onBoard(pos_x-1, pos_y+1) && board[pos_y+1][pos_x-1].isBlack()) {
			board[pos_y+1][pos_x-1].legalise();
		}
		
		if (onBoard(pos_x+1, pos_y+1) && board[pos_y+1][pos_x+1].isBlack()) {
			board[pos_y+1][pos_x+1].legalise();
		}
		
	}
		
	private void blackPawnPossibleMoves(Tile tile) {
		int pos_x = tile.x(); // x coordinate of black pawn
		int pos_y = tile.y(); // y coordinate of black pawn
		
		// forward path
		if (onBoard(pos_x,pos_y-1) && board[pos_y-1][pos_x].isEmpty()) {
			board[pos_y-1][pos_x].legalise();
			
			if (pos_y == BLACK_PAWN_START_ROW && onBoard(pos_x,pos_y-2) && board[pos_y-2][pos_x].isEmpty()) {
				board[pos_y-2][pos_x].legalise();
			} 
		} 
		
		//diagonal attack
		if (onBoard(pos_x-1, pos_y-1) && board[pos_y-1][pos_x-1].isWhite()) {
			board[pos_y-1][pos_x-1].legalise();
		} 
		
		if (onBoard(pos_x+1, pos_y-1) && board[pos_y-1][pos_x+1].isWhite()) {
			board[pos_y-1][pos_x+1].legalise();
		}
		
	}

	private boolean tryToLegalise(int row, int col) {
		// the tile indexed at (row,col) will be legalised if it is empty or has an enemy piece
		// the function will return false if the tile failed to legalise or an ememy piece is blocking the path
		
		if (!onBoard(col,row)) {
			return false;
		}
		
		Tile tile = board[row][col];
		
		if (tile.isEmpty()) {
			tile.legalise();
			return true;
		} else if ((currTurn == WHITES_TURN ? tile.isBlack() : tile.isWhite())) {
			tile.legalise();
			return false;
		} else {
			return false;
		}
	}
	
	private void rookPossibleMoves(Tile tile) {
		// if the pieces's path is able to be blocked: true will be returned
		int pos_x = tile.x(); // x coordinate of rook
		int pos_y = tile.y(); // y coordinate of rook
		
		//LEFT: try to legalise all tiles to the left of the rook
		for (int i = pos_x - 1; i >= 0; i--) {
			//if this tile failed to legalise then break the loop
			if (!tryToLegalise(pos_y,i)) break;
		}
		
		//RIGHT: try to legalise all tiles to the right of the rook
		for (int i = pos_x + 1; i < NUM_COLS; i++) {
			//if this tile failed to legalise then break the loop
			if (!tryToLegalise(pos_y,i)) break;
		}
		
		//DOWN: try to legalise all tiles below the rook
		for (int j = pos_y - 1; j >= 0; j--) {
			//if this tile failed to legalise then break the loop
			if (!tryToLegalise(j,pos_x)) break;
		}
		
		//UP: try to legalise all tiles above the rook
		for (int j = pos_y + 1; j < NUM_ROWS; j++) {
			//if this tile failed to legalise then break the loop
			if (!tryToLegalise(j,pos_x)) break;
		}
	}
	
	private void bishopPossibleMoves(Tile tile) {
		// if the pieces's path is able to be blocked: true will be returned
		int pos_x = tile.x(); // x coordinate of rook
		int pos_y = tile.y(); // y coordinate of rook
		
		int i = 1;
		//NW: try to legalise all tiles to the north west of the bishop
		while(tryToLegalise(pos_y + i,pos_x - i++));
		
		i = 1;
		//NE: try to legalise all tile to the north east of the bishop
		while(tryToLegalise(pos_y + i,pos_x + i++));
		
		i = 1;
		//SE: try to legalise all tile to the south east of the bishop
		while(tryToLegalise(pos_y - i,pos_x + i++));
		
		i = 1;
		//SW: try to legalise all tiles to the south west of the bishop
		while(tryToLegalise(pos_y - i,pos_x - i++));
	}
	
	private void knightPossibleMoves(Tile tile) {
		int pos_x = tile.x(); // x coordinate of knight
		int pos_y = tile.y(); // y coordinate of knight
		

		tryToLegalise(pos_y+1,pos_x-2);
		
		tryToLegalise(pos_y-1,pos_x-2);

		tryToLegalise(pos_y+2,pos_x-1);

		tryToLegalise(pos_y+2,pos_x+1);

		
		tryToLegalise(pos_y+1,pos_x+2);
		
		tryToLegalise(pos_y-1,pos_x+2);
		
		tryToLegalise(pos_y-2,pos_x+1);
		
		tryToLegalise(pos_y-2,pos_x-1);

	}
	
	private void kingPossibleMoves(Tile tile) {
		int pos_x = tile.x(); // x coordinate of king
		int pos_y = tile.y(); // y coordinate of king
		
		Piece king_piece = tile.getPiece(); // holds on to the king piece
		tile.removePiece(); // The kings piece is removed to allow for better check detection
		
		if (onBoard(pos_x-1,pos_y+1) && !isChecked(board[pos_y+1][pos_x-1])) tryToLegalise(pos_y+1,pos_x-1);
		if (onBoard(pos_x,pos_y+1) && !isChecked(board[pos_y+1][pos_x])) tryToLegalise(pos_y+1,pos_x);
		if (onBoard(pos_x+1,pos_y+1) && !isChecked(board[pos_y+1][pos_x+1])) tryToLegalise(pos_y+1,pos_x+1);
		if (onBoard(pos_x+1,pos_y) && !isChecked(board[pos_y][pos_x+1])) tryToLegalise(pos_y,pos_x+1);
		if (onBoard(pos_x+1,pos_y-1) && !isChecked(board[pos_y-1][pos_x+1])) tryToLegalise(pos_y-1,pos_x+1);
		if (onBoard(pos_x,pos_y-1) && !isChecked(board[pos_y-1][pos_x])) tryToLegalise(pos_y-1,pos_x);
		if (onBoard(pos_x-1,pos_y-1) && !isChecked(board[pos_y-1][pos_x-1])) tryToLegalise(pos_y-1,pos_x-1);
		if (onBoard(pos_x-1,pos_y) && !isChecked(board[pos_y][pos_x-1])) tryToLegalise(pos_y,pos_x-1);
		
		
		if (!isCastlingImpossible()) {
			if (currTurn == WHITES_TURN ? !hasLeftWhiteRookMoved : !hasLeftBlackRookMoved) {
				if (board[pos_y][pos_x-1].isLegal() && !isChecked(board[pos_y][pos_x-2])) {
					tryToLegalise(pos_y, pos_x-2);
				}
			} 
			
			if (currTurn == WHITES_TURN ? !hasRightWhiteRookMoved : !hasRightBlackRookMoved) {
				if (board[pos_y][pos_x+1].isLegal() && !isChecked(board[pos_y][pos_x+2])) {
					tryToLegalise(pos_y, pos_x+2);
				}
			}
		}
		
		tile.placePiece(king_piece); // The kings piece is returned to its tile
	}

	//----------------------------------------
	private boolean isKingChecked() {
		//Returns true if the king is in check
		return isChecked(getKing());
	}
		
	private boolean isChecked(Tile tile) {
		// Checks if the current tile is in check
		// will return true if the tile is checked and false otherwise
		int pos_x = tile.x(); // x coordinate of tile
		int pos_y = tile.y(); // y coordinate of tile
		
		//LEFT: searches for rooks, queens and the enemy king to the left 
		if (onBoard(pos_x-1,pos_y) && board[pos_y][pos_x-1].isKing() && (currTurn == WHITES_TURN ? board[pos_y][pos_x-1].isBlack() : board[pos_y][pos_x-1].isWhite())) {
			if (canEnemyKingMove(tile)) return true;
		}
		
		for (int i = pos_x - 1; i >= 0; i--) {
			if (board[pos_y][i].getPiece() == (currTurn == WHITES_TURN ? Piece.Rook_B : Piece.Rook_W) ||
				board[pos_y][i].getPiece() == (currTurn == WHITES_TURN ? Piece.Queen_B : Piece.Queen_W)) {
				return true;
			} else if (board[pos_y][i].hasPiece()){
				break;
			}
		}
		
		//RIGHT: searches for rooks, queens and the enemy king to the right
		if (onBoard(pos_x+1,pos_y) && board[pos_y][pos_x+1].isKing() && (currTurn == WHITES_TURN ? board[pos_y][pos_x+1].isBlack() : board[pos_y][pos_x+1].isWhite())) {
			if (canEnemyKingMove(tile)) return true;
		}
		
		for (int i = pos_x + 1; i < NUM_COLS; i++) {
			if (board[pos_y][i].getPiece() == (currTurn == WHITES_TURN ? Piece.Rook_B : Piece.Rook_W) ||
				board[pos_y][i].getPiece() == (currTurn == WHITES_TURN ? Piece.Queen_B : Piece.Queen_W)) {
				return true;
			} else if (board[pos_y][i].hasPiece()){
				break;
			}
		}
		
		//DOWN: searches for rooks, queens and the enemy king below 
		if (onBoard(pos_x,pos_y-1) && board[pos_y-1][pos_x].isKing() && (currTurn == WHITES_TURN ? board[pos_y-1][pos_x].isBlack() : board[pos_y-1][pos_x].isWhite())) {
			if (canEnemyKingMove(tile)) return true;
		}
		
		for (int j = pos_y - 1; j >= 0; j--) {
			if (board[j][pos_x].getPiece() == (currTurn == WHITES_TURN ? Piece.Rook_B : Piece.Rook_W) ||
				board[j][pos_x].getPiece() == (currTurn == WHITES_TURN ? Piece.Queen_B : Piece.Queen_W)) {
				return true;
			} else if (board[j][pos_x].hasPiece()){
				break;
			}
		}
		
		//UP: searches for rooks, queens and the enemy king to the above
		if (onBoard(pos_x,pos_y+1) && board[pos_y+1][pos_x].isKing() && (currTurn == WHITES_TURN ? board[pos_y+1][pos_x].isBlack() : board[pos_y+1][pos_x].isWhite())) {
			if (canEnemyKingMove(tile)) return true;
		}
		
		for (int j = pos_y + 1; j < NUM_ROWS; j++) {
			if (board[j][pos_x].getPiece() == (currTurn == WHITES_TURN ? Piece.Rook_B : Piece.Rook_W) ||
				board[j][pos_x].getPiece() == (currTurn == WHITES_TURN ? Piece.Queen_B : Piece.Queen_W)) {
					return true;
			} else if (board[j][pos_x].hasPiece()){
					break;
			}
		}
		
		//Checks if pawns and enemy king are checking the tile
		if(currTurn == WHITES_TURN) {
			if (onBoard(pos_x-1,pos_y+1)) {		
				if (board[pos_y+1][pos_x-1].isKing() && board[pos_y+1][pos_x-1].isBlack()) {
					if (canEnemyKingMove(tile)) return true;
				} else if (board[pos_y+1][pos_x-1].getPiece() == Piece.Pawn_B) {
					return true;
				}
			}
			
			if (onBoard(pos_x+1, pos_y+1)) {
				if (board[pos_y+1][pos_x+1].isKing() && board[pos_y+1][pos_x+1].isBlack()) {
					if (canEnemyKingMove(tile)) return true;
				} else if (board[pos_y+1][pos_x+1].getPiece() == Piece.Pawn_B) {
					return true;
				}
			}
			
			if (onBoard(pos_x-1,pos_y-1) && board[pos_y-1][pos_x-1].isKing() && board[pos_y-1][pos_x-1].isBlack() || onBoard(pos_x+1,pos_y-1) && board[pos_y-1][pos_x + 1].isKing() && board[pos_y-1][pos_x+1].isBlack()) {
				if (canEnemyKingMove(tile)) return true;
			}
			
		} else {
			if (onBoard(pos_x-1,pos_y-1)) {		
				if (board[pos_y-1][pos_x-1].isKing() && board[pos_y-1][pos_x-1].isWhite()) {
					if (canEnemyKingMove(tile)) return true;
				} else if (board[pos_y-1][pos_x-1].getPiece() == Piece.Pawn_W) {
					return true;
				}
			}
			
			if (onBoard(pos_x+1, pos_y-1)) {
				if (board[pos_y-1][pos_x+1].isKing() && board[pos_y-1][pos_x+1].isWhite()) {
					if (canEnemyKingMove(tile)) return true;
				} else if (board[pos_y-1][pos_x+1].getPiece() == Piece.Pawn_W) {
					return true;
				}
			}
				
			if (onBoard(pos_x-1,pos_y+1) && board[pos_y+1][pos_x-1].isKing() && board[pos_y][pos_x-1].isWhite()|| onBoard(pos_x+1,pos_y+1) && board[pos_y+1][pos_x + 1].isKing() && board[pos_y+1][pos_x+1].isWhite()) {
				if (canEnemyKingMove(tile)) return true;
			}
		}
		
		//NW: check for bishops and queens	
		for (int i = 1; i < NUM_TILES_DIAGONAL; i++) {
		    if (!onBoard(pos_x-i, pos_y+i)) {
		    	break;
		    } else if (board[pos_y+i][pos_x-i].getPiece() == (currTurn == WHITES_TURN ? Piece.Bishop_B : Piece.Bishop_W) ||
				board[pos_y+i][pos_x-i].getPiece() == (currTurn == WHITES_TURN ? Piece.Queen_B : Piece.Queen_W)) {
				return true;
			} else if (board[pos_y+i][pos_x-i].hasPiece()){
				break;
			}
		}
		
		//NE
		for (int i = 1; i < NUM_TILES_DIAGONAL; i++) {
		    if (!onBoard(pos_x+i, pos_y+i)) {
		    	break;
		    } else if (board[pos_y+i][pos_x+i].getPiece() == (currTurn == WHITES_TURN ? Piece.Bishop_B : Piece.Bishop_W) ||
				board[pos_y+i][pos_x+i].getPiece() == (currTurn == WHITES_TURN ? Piece.Queen_B : Piece.Queen_W)) {
				return true;
			} else if (board[pos_y+i][pos_x+i].hasPiece()){
				break;
			}
		}
		
		//SE
		for (int i = 1; i < NUM_TILES_DIAGONAL; i++) {
		    if (!onBoard(pos_x+i, pos_y-i)) {
		    	break;
		    } else if (board[pos_y-i][pos_x+i].getPiece() == (currTurn == WHITES_TURN ? Piece.Bishop_B : Piece.Bishop_W) ||
				board[pos_y-i][pos_x+i].getPiece() == (currTurn == WHITES_TURN ? Piece.Queen_B : Piece.Queen_W)) {
				return true;
			} else if (board[pos_y-i][pos_x+i].hasPiece()){
				break;
			}
		}
		
		//SW
		for (int i = 1; i < NUM_TILES_DIAGONAL; i++) {
		    if (!onBoard(pos_x-i, pos_y-i)) {
		    	break;
		    } else if (board[pos_y-i][pos_x-i].getPiece() == (currTurn == WHITES_TURN ? Piece.Bishop_B : Piece.Bishop_W) ||
				board[pos_y-i][pos_x-i].getPiece() == (currTurn == WHITES_TURN ? Piece.Queen_B : Piece.Queen_W)) {
				return true;
			} else if (board[pos_y-i][pos_x-i].hasPiece()){
				break;
			}
		}
		
		//Checks if knights are checking the tile
		if (onBoard(pos_x-2,pos_y+1) && board[pos_y+1][pos_x-2].getPiece() == (currTurn == WHITES_TURN ? Piece.Knight_B : Piece.Knight_W)) {
			return true;
		}
		
		if (onBoard(pos_x-2,pos_y-1) && board[pos_y-1][pos_x-2].getPiece() == (currTurn == WHITES_TURN ? Piece.Knight_B : Piece.Knight_W)) {
			return true;
		}
		
		if (onBoard(pos_x+2,pos_y+1) && board[pos_y+1][pos_x+2].getPiece() == (currTurn == WHITES_TURN ? Piece.Knight_B : Piece.Knight_W)) {
			return true;
		}
		
		if (onBoard(pos_x+2,pos_y-1) && board[pos_y-1][pos_x+2].getPiece() == (currTurn == WHITES_TURN ? Piece.Knight_B : Piece.Knight_W)) {
			return true;
		}
		
		if (onBoard(pos_x-1,pos_y+2) && board[pos_y+2][pos_x-1].getPiece() == (currTurn == WHITES_TURN ? Piece.Knight_B : Piece.Knight_W)) {
			return true;
		}
		
		if (onBoard(pos_x+1,pos_y+2) && board[pos_y+2][pos_x+1].getPiece() == (currTurn == WHITES_TURN ? Piece.Knight_B : Piece.Knight_W)) {
			return true;
		}
		
		if (onBoard(pos_x+1,pos_y-2) && board[pos_y-2][pos_x+1].getPiece() == (currTurn == WHITES_TURN ? Piece.Knight_B : Piece.Knight_W)) {
			return true;
		}
		
		if (onBoard(pos_x-1,pos_y-2) && board[pos_y-2][pos_x-1].getPiece() == (currTurn == WHITES_TURN ? Piece.Knight_B : Piece.Knight_W)) {
			return true;
		}
		
		return false;
	}
	
	//isChecked helper function
	private boolean canEnemyKingMove(Tile tile) {
		// returns true if the enemy king can move to the tile
		boolean value;
		changeTurn(); // enemy turn
		value = !isChecked(tile);
		changeTurn(); // change back
		
		return value;
	}
	
	private Tile getKing() {
		//returns the king of the current player
		Tile king = null;
		
		for (int j = 0; j < NUM_ROWS; j++) {
			for (int i = 0; i < NUM_COLS; i++) {
				if (currTurn == WHITES_TURN ? board[j][i].isWhite() && board[j][i].isKing() :
					                          board[j][i].isBlack() && board[j][i].isKing()) {
					king = board[j][i];
					break;
				}
			}
		}
		
		return king;
	}
}
