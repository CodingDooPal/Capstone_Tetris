public class GameBoard {
	private static int[][] gameBoard = new int[22][10];
	
	/*
	 * x: 0~9, y:0~19 사용해서 총 가로(10)x세로(20)
	 *
	 * 값에 따른 블럭 유무
	 * 0: 빈 공간
	 * 1: 다른 블럭이 있는 공간
	 * 
	 */
	
	private GameBoard() {}
	
	public static void setBoard(int[][] board) {
		gameBoard = board;
	}
	
	public static int[][] getBoard() {
		return gameBoard;
	}
	
	// 보드 초기화
	public static void initBoard() {
		for(int x = 0; x < 10; ++x) {
			for(int y = 0; y < 22; ++y) {
				gameBoard[y][x] = 0;
			}
		}
		
		
	}
	
	// 완성된 라인이 있는지 체크
	// 있다면 해당 라인을 삭제 후 위의 라인을 한 칸 아래로 당긴다.
	public static void lineCheck(GamePanel gamePanel) {
		for(int y = 2; y < 22; ++y) {
			for(int x = 0; x < 10; ++x) {
				if(gameBoard[y][x] == 0) {
					break;
				}
				
				// 라인 완성
				if(x == 9 && gameBoard[y][x] != 0) {
					for(int m = y; m > 0; --m) {
						for(int n = 0; n < 10; ++n) {
							// 위의 라인을 한 칸 아래로 당긴다.
							gameBoard[m][n] = gameBoard[m - 1][n];
						}
					}
					
					// 맨 윗줄 비우기
					if(y == 1) {
						for(int n = 0; n < 10; ++n) {
							gameBoard[y][n] = 0;
						}
					}
					
					// 점수 250 증가
					gamePanel.score += 250;
					System.out.println("Score: " + gamePanel.score);
					--y; // 줄 다시 검사
				}
			}
		}
	}
	
	
}
