import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

// 회전축(0, 0)을 기준으로 상대좌표를 구해 블럭의 모양을 그린다.
// X축: (-): 왼쪽, (+): 오른쪽
// Y축: (-): 위쪽, (+): 아래쪽

enum BlockType {		
	// 하늘색(1)
	// OXOO
	I(new int[][] {
		{0, 0}, {-1, 0}, {1, 0}, {2, 0}
	}),
	
	// 노란색(2)
	// XO
	// OO
	O(new int[][] {
		{0, 0}, {0, 1}, {1, 0}, {1, 1}
	}),
	
	// 빨강색(3)
	// OO
	//  XO
	Z(new int[][] {
		{0, 0}, {-1, -1}, {1, 0}, {0, -1}
	}),
	
	// 초록색(4)
	//  OO
	// OX
	S(new int[][] {
		{0, 0}, {-1, 0}, {1, -1}, {0, -1} 
	}),
	
	// 파란색(5)
	// O
	// OXO
	J(new int[][] {
		{0, 0}, {-1, -1}, {1, 0}, {-1, 0}
	}),
	
	// 주황색(6)
	//   O
	// OXO
	L(new int[][] {
		{0, 0}, {-1, 0}, {1, 0}, {1, -1}
	}),
	
	// 보라색(7)
	//  O
	// OXO
	T(new int[][] {
		{0, 0}, {-1, 0}, {1, 0}, {0, -1}
	}),
	
	// 분홍색(8)
	//  O
	// OXO
	//  O
	X(new int[][] {
		{0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}
	}),
	
	// 갈색(9)
	//  O
	//  X
	// OOO
	A_T(new int[][] {
		{0, 0}, {-1, 1}, {1, 1}, {0, 1}, {0, -1}		
	});
	
	private final int[][] shape;
	
	private BlockType(int[][] shape) {
		this.shape = shape;
	}
	
	// 모양 반환
	public int[][] getShape() {
		return this.shape;
	}
}

public class Block {
	private BlockType type;
	private BlockMap data;
	private final int CELL = 30; // 한 블럭은 30px
	int[][] shape; // 블럭의 현재 모양(회전 상태 반영)
	
	// 블럭의 XY 좌표
	private int blockX = 120;
	private int blockY = 30;
	
	public Block(BlockType type) {
		this.type = type;
		this.shape = type.getShape();
		this.data = new BlockMap();
	}

	// BlockType 설정 및 반환
	public void setBlockType(BlockType type) {
		this.type = type;
		this.shape = type.getShape();
	}
	
	public BlockType getBlockType() {
		return this.type;
	}
	
	// BlockType에 따라 사용해야 할 Image 객체(BlockImage, BlockLine) 반환
	public Image getBlockImage() {
		return this.data.getBlockImage(this);
	}
	
	public Image getBlockLine() {
		return this.data.getBlockLine(this);
	}
	
	// BlockShape 설정 및 반환
	private void setBlockShape(int[][] shape) {
		this.shape = shape;
	}
	
	public int[][] getShape() {
		return shape;
	}
	
	// 블럭의 XY 좌표 설정 및 반환
	public void setBlockX(int num) {
		this.blockX = num;
	}
	
	public void setBlockY(int num) {
		this.blockY = num;
	}
	
	public int getBlockX() {
		return this.blockX;
	}
	
	public int getBlockY() {
		return this.blockY;
	}
	
	// 왼쪽으로 이동
	public void moveLeft() {
		if(canMove(GameBoard.getBoard(), this.shape, -1, 0)) {
			this.setBlockX(this.blockX - CELL);
		}
	}
	
	// 오른쪽으로 이동
	public void moveRight() {
		if(canMove(GameBoard.getBoard(), this.shape, 1, 0)) {
			this.setBlockX(this.blockX + CELL);
		}
	}

	// 한 칸 아래로 이동
	public void softDrop() {
		if(canMove(GameBoard.getBoard(), this.shape, 0, 1)) {
			this.setBlockY(this.blockY + CELL);
		}
	}
	
	// 맨 아래로 이동
	public void hardDrop() {
		while(canMove(GameBoard.getBoard(), this.shape, 0, 1)) {
			this.setBlockY(this.blockY + CELL);
		}
	}
	
	// 시계 방향으로 90도 회전
	public void clockwiseRotate() {
		if (this.type == BlockType.O || this.type == BlockType.X) {
			return;
		}
		
		int[][] result = new int[this.shape.length][2];
		for(int i = 0; i < this.shape.length; ++i) {
			result[i][0] = -this.shape[i][1];
			result[i][1] = this.shape[i][0];
		}
		
		/*
		if (canMove(GameBoard.getBoard(), result, 0, 0)) {	
			this.setBlockShape(result);
		}
		*/
	}
	
	// 반시계 방향으로 90도 회전
	public void counterclockwiseRotate() {
		if (this.type == BlockType.O || this.type == BlockType.X) {
			return;
		}
		
		int[][] result = new int[this.shape.length][2];
		for(int i = 0; i < this.shape.length; ++i) {
			result[i][0] = this.shape[i][1];
			result[i][1] = -this.shape[i][0];
		}
		
		/*
		if (canMove(GameBoard.getBoard(), result, 0, 0)) {	
			this.setBlockShape(result);
		}
		*/
		
		rotateWithKick(result);
	}
	
	/*
	 * canMove(충돌 감지 함수)
	 * 
	 * 블럭이 이동 또는 회전 시 충돌되는 부분이 있는지 검사한다.
	 * 각 블럭을 구성하는 모든 칸을 대상으로 검사한다.  
	 * 문제가 없다면 true, 충돌이 발생하면 false를 반환한다.
	 * 
	 */
	
	public boolean canMove(int[][] gameBoard, int[][] shape, int dx, int dy) {
		for(int[] cell : shape) {
			int x = this.blockX / 30 + cell[0] + dx;
			int y = this.blockY / 30 + cell[1] + dy;
			
			// 블럭이 좌우 밖으로 나가는지 확인
			if(x < 0 || x > 9) {
				return false;
			}
			
			// 블럭이 바닥에 닿았는지 확인
			if(y > 21) {
				return false;
			}
			
			// 블럭이 다른 블럭과 겹치는지 확인
			if(y >= 2  && gameBoard[y][x] != 0) {
				return false;
			}
		}
		
		return true;
	}
	
	// 블럭을 해당 위치에 고정한다.
	// 만약 고정 위치가 유효하지 않다면 false 반환한다.
	public boolean lock(int[][] gameBoard) {
		for(int[] cell : this.shape) {
			int x = this.blockX / 30 + cell[0];
			int y = this.blockY / 30 + cell[1];
			
			// 블럭이 고정 불가능한 위치에 있으면 게임 오버로 처리한다.
			if(x < 0 || x > 9 || y < 2 || y > 21) {
				return false;
			}
		}
		
		for(int[] cell : this.shape) {
			int x = this.blockX / 30 + cell[0];
			int y = this.blockY / 30 + cell[1];
			
			// BlockShape에 따른 enum.ordinal(int 타입)로 저장한다.
			// 나중에 색상을 원활하게 고르기 위함.
			gameBoard[y][x] = this.type.ordinal() + 1;
		}
		
		return true;
	}
	
	
	// KICKS
	private static final int[][] KICKS = {
		    {0, 0}, {-1, 0}, {1, 0}, {0, -1}, {-2, 0}, {2, 0}
		};

		private void rotateWithKick(int[][] rotated) {
		    for (int[] k : KICKS) {
		        if (canMove(GameBoard.getBoard(), rotated, k[0], k[1])) {
		            this.setBlockShape(rotated);
		            this.setBlockX(this.blockX + k[0] * CELL);
		            this.setBlockY(this.blockY + k[1] * CELL);
		            return;
		        }
		    }
		}
	
}

class BlockMap {
	public final Image yellowBlock =
    		new ImageIcon(getClass().getResource("/images/YellowBlock.png")).getImage();
	public final Image cyanBlock =
	        new ImageIcon(getClass().getResource("/images/CyanBlock.png")).getImage();
	public final Image redBlock =
	        new ImageIcon(getClass().getResource("/images/RedBlock.png")).getImage();
	public final Image greenBlock =
	        new ImageIcon(getClass().getResource("/images/GreenBlock.png")).getImage();
	public final Image blueBlock =
	        new ImageIcon(getClass().getResource("/images/BlueBlock.png")).getImage();
	public final Image orangeBlock =
	        new ImageIcon(getClass().getResource("/images/OrangeBlock.png")).getImage();
	public final Image pinkBlock =
	        new ImageIcon(getClass().getResource("/images/PinkBlock.png")).getImage();
	public final Image purpleBlock =
	        new ImageIcon(getClass().getResource("/images/PurpleBlock.png")).getImage();
	public final Image brownBlock =
	        new ImageIcon(getClass().getResource("/images/brownBlock.png")).getImage();
	
	public final Image yellowLine =
    		new ImageIcon(getClass().getResource("/images/YellowLine.png")).getImage();
	public final Image cyanLine =
	        new ImageIcon(getClass().getResource("/images/CyanLine.png")).getImage();
	public final Image redLine =
	        new ImageIcon(getClass().getResource("/images/RedLine.png")).getImage();
	public final Image greenLine =
	        new ImageIcon(getClass().getResource("/images/GreenLine.png")).getImage();
	public final Image blueLine =
	        new ImageIcon(getClass().getResource("/images/BlueLine.png")).getImage();
	public final Image orangeLine =
	        new ImageIcon(getClass().getResource("/images/OrangeLine.png")).getImage();
	public final Image pinkLine =
	        new ImageIcon(getClass().getResource("/images/PinkLine.png")).getImage();
	public final Image purpleLine =
	        new ImageIcon(getClass().getResource("/images/PurpleLine.png")).getImage();
	public final Image brownLine =
	        new ImageIcon(getClass().getResource("/images/brownLine.png")).getImage();

	final private Map<BlockType, Image> TypeImageMap = new HashMap<BlockType, Image>();
	final private Map<BlockType, Image> TypeLineMap = new HashMap<BlockType, Image>();
	
	public BlockMap() {
		initTypeImageMap();
		initTypeLineMap();
	}
	
	// BlockType에 맞는 BlockImage(or BlockLine).png를 반환한다.
	public Image getBlockImage(Block block) {
		return TypeImageMap.get(block.getBlockType());
	}
	
	public Image getBlockLine(Block block) {
		return TypeLineMap.get(block.getBlockType());
	}
	
	// Map 초기화
	private void initTypeImageMap() {
    	this.TypeImageMap.put(BlockType.I, cyanBlock);
    	this.TypeImageMap.put(BlockType.O, yellowBlock);
    	this.TypeImageMap.put(BlockType.Z, redBlock);
    	this.TypeImageMap.put(BlockType.S, greenBlock);
    	this.TypeImageMap.put(BlockType.J, blueBlock);
    	this.TypeImageMap.put(BlockType.L, orangeBlock);
    	this.TypeImageMap.put(BlockType.T, purpleBlock);
    	this.TypeImageMap.put(BlockType.X, pinkBlock);
    	this.TypeImageMap.put(BlockType.A_T, brownBlock);
    }
	
	private void initTypeLineMap() {
		this.TypeLineMap.put(BlockType.I, cyanLine);
    	this.TypeLineMap.put(BlockType.O, yellowLine);
    	this.TypeLineMap.put(BlockType.Z, redLine);
    	this.TypeLineMap.put(BlockType.S, greenLine);
    	this.TypeLineMap.put(BlockType.J, blueLine);
    	this.TypeLineMap.put(BlockType.L, orangeLine);
    	this.TypeLineMap.put(BlockType.T, purpleLine);
    	this.TypeLineMap.put(BlockType.X, pinkLine);
    	this.TypeLineMap.put(BlockType.A_T, brownLine);
	}
}


