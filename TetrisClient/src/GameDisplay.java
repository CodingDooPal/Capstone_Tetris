import java.awt.*;
import java.awt.event.*;
import java.util.Map;

import javax.swing.*;

public class GameDisplay extends JPanel {	    
    private final int CELL = 30; // 블럭 하나의 크기 = 30px
    
    private final BlockMap map = new BlockMap();
    private final Image lineImg = new ImageIcon(getClass().getResource("/images/Line.png")).getImage();
    private final Image gameOverLineImg = new ImageIcon(getClass().getResource("/images/GameOverLine.png")).getImage();
    
    public Block fallingBlock; // 떨어지는 블럭
    public Block nextBlock; // 다음에 나올 블럭
    
    GameDisplay(BlockType currentBlockType, BlockType nextBlockType) {
    	this.fallingBlock = new Block(currentBlockType);
    	this.nextBlock = new Block(nextBlockType);
    }
    
    // 블럭 좌표를 갱신하는 함수
    public void updateBlockXY(int x, int y) {
        this.fallingBlock.setBlockX(x);
        this.fallingBlock.setBlockY(y);
        repaint();
    }
    
    // 떨어지는 블럭의 BlockType을 설정한다.
    public void setFallingBlockType(BlockType blockType) {
    	fallingBlock.setBlockType(blockType);
    	repaint();
    }
    
    // 다음 블럭의 BlockType을 설정한다.
    public void setNextBlockType(BlockType blockType) {
    	nextBlock.setBlockType(blockType);
    	repaint();
    }

    // Display에 블럭을 그리는 함수
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        // BlockMap 객체에서 image를 불러오는지 확인하기 위한 테스트 코드
        
        /*
        System.out.println("paint: " + this.blockType + " " + 
        fallingBlock.getBlockX() + "," + fallingBlock.getBlockY());
        */
        
        // images 폴더에 저장된 모든 이미지를 정상적으로 불러오는지 확인
        // (블럭 이미지, x, y, x 크기, y 크기, 나타낼 객체);
        /*g.drawImage(map.yellowBlock, 0, 0, CELL, CELL, this);
        g.drawImage(map.cyanBlock, 0, 30, CELL, CELL, this);
        g.drawImage(map.redBlock, 0, 60, CELL, CELL, this);
        g.drawImage(map.greenBlock, 0, 90, CELL, CELL, this);
        g.drawImage(map.blueBlock, 0, 120, CELL, CELL, this);
        g.drawImage(map.orangeBlock, 0, 150, CELL, CELL, this);
        g.drawImage(map.pinkBlock, 0, 180, CELL, CELL, this);
        g.drawImage(map.purpleBlock, 0, 210, CELL, CELL, this);
        g.drawImage(map.brownBlock, 0, 240, CELL, CELL, this);
        */
        
        int[][] gameBoard = GameBoard.getBoard();
        
		// 세로 경계
		for(int n = 0; n < 22; ++n) {
			g.drawImage(lineImg, 300, n * 30, CELL, CELL, this);
		}
		
		// 가로 경계
		for(int n = 330; n < 510; n += 30) {
			g.drawImage(lineImg, n, 180, CELL, CELL, this);
		}
		
		// 게임 오버 라인
		for(int n = 0; n < 10; ++n) {
			g.drawImage(gameOverLineImg, n * 30, 30, CELL, CELL, this);
		}
		
        // 게임 보드에 저장된 내용
        for(int x = 0; x < 10; ++x) {
        	for(int y = 0; y < 22; ++y) {
        		if(gameBoard[y][x] != 0) {
        			g.drawImage(getBlockImage(gameBoard[y][x], map), x * 30, y * 30, CELL, CELL, this);
        		}
        	}
        }
        
		// 하드 드롭 위치 표시
		int num = 0;
		while(fallingBlock.canMove(GameBoard.getBoard(), fallingBlock.shape, 0, num + 1)) {
			++num;
		}
		
		for (int[] cell : fallingBlock.getShape()) {
			g.drawImage(fallingBlock.getBlockLine(), fallingBlock.getBlockX() + cell[0] * CELL,
	                fallingBlock.getBlockY() + (cell[1] + num) * CELL, CELL, CELL, this);
			
		}
        
		// 떨어지는 블럭 표시
		for (int[] cell : fallingBlock.getShape()) {
	        g.drawImage(fallingBlock.getBlockImage(), fallingBlock.getBlockX() + cell[0] * CELL,
	                fallingBlock.getBlockY() + cell[1] * CELL, CELL, CELL, this);
	    }
		
		// 다음 블럭 표시
		for(int[] cell : nextBlock.getShape()) {
			g.drawImage(nextBlock.getBlockImage(), 390 + cell[0] * CELL,
	                75 + cell[1] * CELL, CELL, CELL, this);
		}
		
		
		
    }
    
    private Image getBlockImage(int num, BlockMap map) {
    	switch(num) {
    	case 1:
    		return map.cyanBlock;
    	case 2:
    		return map.yellowBlock;
    	case 3:
    		return map.redBlock;
    	case 4:
    		return map.greenBlock;
    	case 5:
    		return map.blueBlock;
    	case 6:
    		return map.orangeBlock;
    	case 7:
    		return map.purpleBlock;
    	case 8:
    		return map.pinkBlock;
    	case 9:
    		return map.brownBlock;
    	default:
    		return map.cyanBlock;
    	}
    }
}


