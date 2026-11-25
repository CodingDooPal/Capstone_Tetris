import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

/*
 * 테트리스 키 입력
 * ←: 블럭 왼쪽 이동
 * →: 블럭 오른쪽 이동
 * ↓: 소프트 드롭(한 칸 아래로)
 * Space: 하드 드롭(맨 아래로)
 * ↑, x: 시계 방향 회전
 * Ctrl, Z: 반시계 방향 회전
 *
 * 
 */


public class KeyEventListener implements KeyListener {
	private Block fallingBlock;
	private JPanel GamePanel;

	public KeyEventListener(Block fallingBlock, JPanel GamePanel) {
		this.fallingBlock = fallingBlock;
		this.GamePanel = GamePanel;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			// System.out.println("위쪽 방향키");
			// 시계 방향 회전
			fallingBlock.clockwiseRotate();
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			// System.out.println("왼쪽 방향키");
			// 왼쪽 이동
			fallingBlock.moveLeft();
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			// System.out.println("오른쪽 방향키");
			// 오른쪽 이동
			fallingBlock.moveRight();
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			// System.out.println("아래쪽 방향키");
			// 아래쪽 이동
			fallingBlock.softDrop();
		}
		else if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
			// System.out.println("컨트롤키");
			// 반시계 방향 회전
			fallingBlock.counterclockwiseRotate();
		}
		else if(e.getKeyCode() == KeyEvent.VK_X) {
			// System.out.println("X키");
			// 시계 방향 회전
			fallingBlock.clockwiseRotate();
		}
		else if(e.getKeyCode() == KeyEvent.VK_Z) {
			// System.out.println("Z키");
			// 반시계 방향 회전
			fallingBlock.counterclockwiseRotate();
		}
		else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			// System.out.println("스페이스키");
			// 하드 드롭(맨 아래로 바로 이동)
			fallingBlock.hardDrop();
		}
		
		GamePanel.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
