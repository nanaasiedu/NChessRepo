
public enum Piece {
	Pawn_W(0, PieceType.Pawn), Knight_W(0, PieceType.Knight), Bishop_W(0, PieceType.Bishop), 
	Rook_W(0, PieceType.Rook), Queen_W(0, PieceType.Queen), King_W(0, PieceType.King),
	Pawn_B(1, PieceType.Pawn), Knight_B(1, PieceType.Knight), Bishop_B(1, PieceType.Bishop), 
	Rook_B(1, PieceType.Rook), Queen_B(1, PieceType.Queen), King_B(1, PieceType.King);
	
	public static final int WHITE = GameWindow.WHITES_TURN;
	public static final int BLACK = GameWindow.BLACKS_TURN;
	
	private final int colour;
	private final PieceType type;
	
	private Piece(int colour, PieceType type) {
		this.colour = colour;
		this.type = type;
	}
	
	public int getColour() {
		return colour;
	}
	
	public boolean isWhite() {
		return colour == WHITE;
	}
	
	public boolean isBlack() {
		return colour == BLACK;
	}
	
	public PieceType getType() {
		return type;
	}
	
}
