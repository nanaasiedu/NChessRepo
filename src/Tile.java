import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

//TODO: implement picture logic in this class
//TODO: Allow Knights to check kings
public class Tile extends JButton implements Cloneable {
	public static final Color DEFAULT_TILE_COLOUR = Color.black;
	public static final Color DEFAULT_TILE_HOVER_COLOUR = Color.yellow;
	public static final Color DEFAULT_TILE_HIGHLIGHT_COLOUR = Color.green;
	public static final Color DEFAULT_TILE_ERROR_COLOUR = Color.red;
	public static final int DEFAULT_TILE_BORDER_THICKNESS = 2;
	
	private Piece piece; // the piece that is on the tile. If the tile is empty this field will be null
	private boolean legal; // this boolean value will be true if this tile can be legally selected during the games current state
	private boolean selected; //True if and only if this tile is currently selected by the player
	private int pos_x; // array index for position of this tile on the board
	private int pos_y; // array index for position of this tile on the board
	
	public Tile(int pos_x, int pos_y) {
		super();
		initilise(pos_x, pos_y);
	}
	
	public Tile(String str, int pos_x, int pos_y) {
		super(str);
		initilise(pos_x, pos_y);
	}
	
	private void initilise(int pos_x, int pos_y) {
		piece = null;
		legal = false;
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		setInitialBorder();
		placePiece(null);// place an empty tile
	}
	
	public boolean isEmpty() {
		// returns true if the tile is empty (no piece)
		return piece == null;
	} 
	
	public boolean hasPiece() {
		// returns true if the tile has a piece on it
		return piece != null;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public PieceType getType() {
		if (piece == null) return null;
		return piece.getType();
	}
	
	public boolean isKing() {
		// returns true if the tile holds a king piece
		if (isEmpty()) return false;
		return piece == Piece.King_W || piece == Piece.King_B;
	}
	
	public boolean isWhite() {
		if (isEmpty()) return false;
		return piece.getColour() == Piece.WHITE;
	}
	
	public boolean isBlack() {
		if (isEmpty()) return false;
		return piece.getColour() == Piece.BLACK;
	}
	
	public boolean isLegal() {
		return legal;
	}
	
	public void legalise() {
		legal = true;
		highlight();
	}
	
	public void illegalise() {
		legal = false;
		setInitialBorder();
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void select() {
		selected = true;
		legalise();
	}
	
	public void deselect() {
		selected = false;
		illegalise();
	}
	
	public int x() {
		return pos_x;
	}
	
	public int y() {
	    return pos_y;
	}
	
	public void removePiece() {
		//removes the piece currently on the tile from the tile
		piece = null;
		
		placePiece(null);
	}
	
	public void placePiece(Piece piece) {
		this.piece = piece;
		
		if (piece != null) {
			switch(piece) {
			case Pawn_W :
				setIcon(new ImageIcon("images/white_pawn.png"));
				break;
			case Rook_W:
				setIcon(new ImageIcon("images/white_rook.png"));
				break;
			case Knight_W:
				setIcon(new ImageIcon("images/white_knight.png"));
				break;
			case Bishop_W:
				setIcon(new ImageIcon("images/white_bishop.png"));
				break;
			case Queen_W:
				setIcon(new ImageIcon("images/white_queen.png"));
				break;
			case King_W:
				setIcon(new ImageIcon("images/white_king.png"));
				break;

			case Pawn_B :
				setIcon(new ImageIcon("images/black_pawn.png"));
				break;
			case Rook_B:
				setIcon(new ImageIcon("images/black_rook.png"));
				break;
			case Knight_B:
				setIcon(new ImageIcon("images/black_knight.png"));
				break;
			case Bishop_B:
				setIcon(new ImageIcon("images/black_bishop.png"));
				break;
			case Queen_B:
				setIcon(new ImageIcon("images/black_queen.png"));
				break;
			case King_B:
				setIcon(new ImageIcon("images/black_king.png"));
				break;
			default:
			}
		} else {
			setIcon(new ImageIcon("images/blank.png"));
		}
		
		//setText(text);
	}
	
	public void setInitialBorder() {
		if (isLegal()) {
			highlight();
		} else {
			setBorder(new LineBorder(DEFAULT_TILE_COLOUR, DEFAULT_TILE_BORDER_THICKNESS, true));
		}
	} 
	
	public void setHoverBorder() {
		setBorder(new LineBorder(DEFAULT_TILE_HOVER_COLOUR, DEFAULT_TILE_BORDER_THICKNESS, true));
	} 
	
	public void setErrorBorder() {
		setBorder(new LineBorder(DEFAULT_TILE_ERROR_COLOUR, DEFAULT_TILE_BORDER_THICKNESS, true));
	} 
	
	public void highlight() {
		setBorder(new LineBorder(DEFAULT_TILE_HIGHLIGHT_COLOUR, DEFAULT_TILE_BORDER_THICKNESS, true));
	}
	
	@Override 
	public Tile clone() {
		Tile clone = new Tile(pos_x,pos_y);
		
		clone.placePiece(piece);
		clone.legal = legal; 
		clone.selected = selected; 
		
		return clone;
	}
	
	public void setFields(Piece piece, boolean legal, boolean selected) {
		placePiece(piece);
		this.legal = legal;
		this.selected = selected;
	}
	
	@Override 
	public String toString() {
		String text = "";
		
		if (piece != null) {
			switch(piece) {
			case Pawn_W :
				text = "wPawn";
				break;
			case Rook_W:
				text = "wRook";
				break;
			case Knight_W:
				text = "wKnight";
				break;
			case Bishop_W:
				text = "wBishop";
				break;
			case Queen_W:
				text = "wQueen";
				break;
			case King_W:
				text = "wKing";
				break;

			case Pawn_B :
				text = "bPawn";
				break;
			case Rook_B:
				text = "bRook";
				break;
			case Knight_B:
				text = "bKnight";
				break;
			case Bishop_B:
				text = "bBishop";
				break;
			case Queen_B:
				text = "bQueen";
				break;
			case King_B:
				text = "bKing";
				break;
			default:
			}
		}
			
		return text + "(" + pos_x + "," + pos_y + ")";
	}
}
