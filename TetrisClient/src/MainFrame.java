import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// 랜덤
import java.util.Random;

// 각 화면을 나타내기 위한 enum
enum Screen { 
	LOGIN, SIGNUP, MENU, GAME, SCORE, USERINFO
}

public class MainFrame extends JFrame {
	private final CardLayout cards = new CardLayout();
	private final JPanel mainPanel = new JPanel(cards);
	private final Screen screenInit = Screen.LOGIN;
	
	private final LoginPanel loginPanel = new LoginPanel(this);
	private final SignupPanel signupPanel = new SignupPanel(this);
	private final MenuPanel menuPanel = new MenuPanel(this);
	private final GamePanel gamePanel = new GamePanel(this);
	private final ScorePanel scorePanel = new ScorePanel(this);
	private final UserInfoPanel userInfoPanel = new UserInfoPanel(this);
	
	private int userNumber = -1; // 로그아웃 상태
	
	public MainFrame(String title) {
		super(title);
		
		// mainPanel에 각 Panel 추가하고, JFrame에 mainPanel 추가
		this.addPanels();
		this.add(mainPanel);
		
		this.setResizable(false); // 사용자가 창 크기 변경을 못 하게 변경
		this.setSize(800, 850); // 기본 창 크기 800x850
		this.setVisible(true); 
		
		// 시작 화면 = 로그인 화면
		cards.show(mainPanel, screenInit.name());
	}
	
	// 화면 전환 시 필요한 작업들 수행
	public void changeScreen(Screen screenState) {
		switch(screenState) {
		case LOGIN:
			// 로그인 창의 TextPanel 값 초기화
			loginPanel.initLabel();
			break;
		case SIGNUP:
			// 회원가입 창의 TextPanel 값 초기화
			signupPanel.initLabel();
			break;
		case SCORE:
			// 랭킹 테이블 불러오기
			scorePanel.setRankingTable(getRankingTable());
			String[] rankingTable = getRankingTable();
			
			if(rankingTable[1].equals(TCPCommand.SUCCESS.name())) {
				scorePanel.displayRankingTable();
			}
			break;
		case USERINFO:
			// 유저 정보 불러오기
			userInfoPanel.setUserInfo(getUserInfo());
			String[] userInfo = getUserInfo();
						
			if(userInfo[1].equals(TCPCommand.SUCCESS.name())) {
				userInfoPanel.displayUserInfo();
			}
			break;
		case GAME:
			gamePanel.gameTimer.start();
			break;
		default:
			break;
		}
		
	    cards.show(mainPanel, screenState.name());
	}
	
	private void addPanels() {
		mainPanel.add(loginPanel, Screen.LOGIN.name());
		mainPanel.add(signupPanel, Screen.SIGNUP.name());
		mainPanel.add(menuPanel, Screen.MENU.name());
		mainPanel.add(gamePanel, Screen.GAME.name());
		mainPanel.add(scorePanel, Screen.SCORE.name());
		mainPanel.add(userInfoPanel, Screen.USERINFO.name());
	}
	
	public void setUserNumber(int userNumber) {
		this.userNumber = userNumber;
	}
	
	public int getUserNumber() {
		return userNumber;
	}
	
	public void logout() {
		userNumber = -1;
	}
	
	private String[] getRankingTable() {
		String[] sendData = RequestData.createRankingTableRequest();
		
		TCPTx tcp = new TCPTx();
		String[] receiveData = tcp.request(sendData);
		
		return receiveData;
	}
	
	private String[] getUserInfo() {
		String[] sendData = RequestData.createUserInfoRequest(String.valueOf(this.getUserNumber()));
		
		TCPTx tcp = new TCPTx();
		String[] receiveData = tcp.request(sendData);
		
		return receiveData;		
	}
}

class LoginPanel extends JPanel implements ActionListener{
	private final MainFrame mainFrame;
	
	//private final JPanel loginPanel = new JPanel();
	
	private final JButton loginButton;
    private final JButton signupButton;
    private final JButton exitButton;
	
	private final JTextField idTextField;
	private final JPasswordField  passwordTextField;
	
	private JLabel[] errorMessageLabels = new JLabel[4];
	
	private final int TEXT_FIELD_X = 250;
	private final int TEXT_FIELD_WIDTH = 300;
	private final int TEXT_FIELD_HEIGHT = 40;
	
	private final int BUTTON_WIDTH = 140;
	private final int BUTTON_HEIGHT = 40;
	
	private final int LABEL_X = 180;
	private final int LABEL_WIDTH = 70;
	private final int LABEL_HEIGHT = 40;
	
	public LoginPanel(MainFrame mainFrame) {
		setLayout(null);
		this.mainFrame = mainFrame;
		
		idTextField = new JTextField(20);
		passwordTextField = new JPasswordField(20);

        loginButton = new JButton("로그인");
        signupButton = new JButton("회원가입");
        exitButton = new JButton("게임 종료");

        loginButton.addActionListener(this);
        signupButton.addActionListener(this);
        exitButton.addActionListener(this);
        
        idTextField.setBounds(TEXT_FIELD_X, 200, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
        passwordTextField.setBounds(TEXT_FIELD_X, 250, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);

        loginButton.setBounds(250, 310, BUTTON_WIDTH, BUTTON_HEIGHT);
        signupButton.setBounds(410, 310, BUTTON_WIDTH, BUTTON_HEIGHT);
        exitButton.setBounds(600, 580, 150, 50);

        JLabel idLabel = new JLabel("ID");
        idLabel.setBounds(LABEL_X, 200, LABEL_WIDTH, LABEL_HEIGHT);
        add(idLabel);
        add(idTextField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(LABEL_X, 250, LABEL_WIDTH, LABEL_HEIGHT);
        add(passwordLabel);
        add(passwordTextField);
        
        // 에러 메시지 라벨들의 위치 미리 할당
     	for(int i = 0; i < errorMessageLabels.length; ++i) {
     		errorMessageLabels[i] = new JLabel("");
     		errorMessageLabels[i].setBounds(LABEL_X, 400 + i * 30, 350, LABEL_HEIGHT);
     	    add(errorMessageLabels[i]);	
     	}
        
        add(loginButton);
        add(signupButton);
        add(exitButton);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == loginButton) {
			// 로그인 처리
			String[] sendData = RequestData.createLoginRequest(
					idTextField.getText(), new String(passwordTextField.getPassword()));
			String[] receiveData = null;
			
			TCPTx tcp = new TCPTx();
			receiveData = tcp.request(sendData);
			
			// 로그인 성공 시 userNumber 저장 후 메뉴로 이동
			if(receiveData[1].equals(TCPCommand.SUCCESS.name())) {
				mainFrame.setUserNumber(Integer.parseInt(receiveData[2]));
				mainFrame.changeScreen(Screen.MENU);
			}
			// 실패 시 에러 메시지 출력
			else {
				this.displayErrorMessage(receiveData);
				System.out.println(receiveData[1]);
			}
		}
		else if (ae.getSource() == signupButton) {
			// 회원가입 화면 이동
			mainFrame.changeScreen(Screen.SIGNUP);
		}
		else if (ae.getSource() == exitButton) {
			// 게임 종료
			System.exit(1);
		}
	}
	
	// TextField에 있던 값 지우기
	public void initLabel() {
		idTextField.setText("");
		passwordTextField.setText("");
	}
	
	private void displayErrorMessage(String[] receiveData) {
		// 기존 정보를 먼저 지우고
		for(int i = 0; i < errorMessageLabels.length; ++i) {
			errorMessageLabels[i].setText("");
		}
		// 새 정보를 입력
		int idx = 1;
		while(receiveData[idx] != null) {
			errorMessageLabels[idx - 1].setText(receiveData[idx++]);
		}
	}
}

class SignupPanel extends JPanel implements ActionListener {
	private final MainFrame mainFrame;
	
	private final JTextField idTextField;
	private final JPasswordField passwordTextField;
	private final JPasswordField repasswordTextField;
	private final JTextField nicknameTextField;
	
	private final JButton confirmButton; // 회원가입 확인 버튼
	private final JButton backButton; // 돌아가기 버튼
	
	private JLabel[] errorMessageLabels = new JLabel[6];
	
	private final int TEXT_FIELD_X = 250;
	private final int TEXT_FIELD_WIDTH = 300;
	private final int TEXT_FIELD_HEIGHT = 40;
	
	private final int BUTTON_WIDTH = 150;
	private final int BUTTON_HEIGHT = 50;
	
	private final int LABEL_X = 80;
	private final int LABEL_WIDTH = 170;
	private final int LABEL_HEIGHT = 40;
	
	SignupPanel(MainFrame mainFrame) {
		setLayout(null);
		this.mainFrame = mainFrame;
		
		idTextField = new JTextField(20);
		passwordTextField = new JPasswordField(20);
		repasswordTextField = new JPasswordField(20);
		nicknameTextField = new JTextField(20);
		
		confirmButton = new JButton("회원가입 하기");
		backButton = new JButton("돌아가기");
		
		confirmButton.addActionListener(this);
		backButton.addActionListener(this);
		
		idTextField.setBounds(TEXT_FIELD_X, 150, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
		passwordTextField.setBounds(TEXT_FIELD_X, 200, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
		repasswordTextField.setBounds(TEXT_FIELD_X, 250, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
		nicknameTextField.setBounds(TEXT_FIELD_X, 300, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
		
		confirmButton.setBounds(320, 350, BUTTON_WIDTH, BUTTON_HEIGHT);
		backButton.setBounds(600, 580, BUTTON_WIDTH, BUTTON_HEIGHT);
		
		JLabel idLabel = new JLabel("사용할 ID(6~12자)");
        idLabel.setBounds(LABEL_X, 150, LABEL_WIDTH, LABEL_HEIGHT);
        add(idLabel);		
		add(idTextField);
		
		JLabel pwLabel = new JLabel("사용할 Password(8~16자)");
		pwLabel.setBounds(LABEL_X, 200, LABEL_WIDTH, LABEL_HEIGHT);
        add(pwLabel);		
		add(passwordTextField);
		
		JLabel repwLabel = new JLabel("Password 재입력");
		repwLabel.setBounds(LABEL_X, 250, LABEL_WIDTH, LABEL_HEIGHT);
        add(repwLabel);		
		add(repasswordTextField);
		
		JLabel nicknameLabel = new JLabel("사용할 닉네임(2~8자)");
		nicknameLabel.setBounds(LABEL_X, 300, LABEL_WIDTH, LABEL_HEIGHT);
        add(nicknameLabel);		
		add(nicknameTextField);
		
		// 에러 메시지 라벨들의 위치 미리 할당
		for(int i = 0; i < errorMessageLabels.length; ++i) {
			errorMessageLabels[i] = new JLabel("");
			errorMessageLabels[i].setBounds(LABEL_X, 400 + i * 30, 350, LABEL_HEIGHT);
	        add(errorMessageLabels[i]);	
		}
        
		add(confirmButton);
		add(backButton);
	}	
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		
		if(ae.getSource() == confirmButton) {
			// 회원가입 처리
			String[] sendData = RequestData.createSignupRequest(
					idTextField.getText(), new String(passwordTextField.getPassword()), 
					new String(repasswordTextField.getPassword()), nicknameTextField.getText());
			String[] receiveData = null;
			
			TCPTx tcp = new TCPTx();
			receiveData = tcp.request(sendData);
			
			// 성공 시 로그인 화면으로 이동
			if(receiveData[1].equals(TCPCommand.SUCCESS.name())) {
				mainFrame.changeScreen(Screen.LOGIN);
			}
			// 실패 시 에러 메시지 출력
			else {
				this.displayErrorMessage(receiveData);
			}
		}
		else if (ae.getSource() == backButton) {
			// 로그인 화면으로 돌아가기
			mainFrame.changeScreen(Screen.LOGIN);
		}
	}
	
	// TextField에 있던 값 지우기
	public void initLabel() {
		idTextField.setText("");
		passwordTextField.setText("");
		repasswordTextField.setText("");
		nicknameTextField.setText("");
	}
	
	private void displayErrorMessage(String[] receiveData) {
		// 기존 정보를 먼저 지우고
		for(int i = 0; i < errorMessageLabels.length; ++i) {
			errorMessageLabels[i].setText("");
		}
		// 새 정보를 입력
		int idx = 2;
		while(receiveData[idx] != null) {
			errorMessageLabels[idx - 2].setText(receiveData[idx++]);
		}
	}
}

class MenuPanel extends JPanel implements ActionListener {
	private final MainFrame mainFrame;
	
	private final JButton gameButton;
	private final JButton scoreButton;
	private final JButton userinfoButton;
	private final JButton logoutButton;
	
	private final int BUTTON_X = 300;
	private final int BUTTON_WIDTH = 200;
	private final int BUTTON_HEIGHT = 50;

	public MenuPanel(MainFrame mainFrame) {
		setLayout(null);
		this.mainFrame = mainFrame;
		
		gameButton = new JButton("게임 시작");
		scoreButton = new JButton("순위표");
		userinfoButton = new JButton("사용자 정보");
		logoutButton = new JButton("로그아웃");
		
		gameButton.setBounds(BUTTON_X, 150, BUTTON_WIDTH, BUTTON_HEIGHT);
		scoreButton.setBounds(BUTTON_X, 250, BUTTON_WIDTH, BUTTON_HEIGHT);
		userinfoButton.setBounds(BUTTON_X, 350, BUTTON_WIDTH, BUTTON_HEIGHT);
		logoutButton.setBounds(600, 580, 150, 50);
		
		gameButton.addActionListener(this);
		scoreButton.addActionListener(this);
		userinfoButton.addActionListener(this);
		logoutButton.addActionListener(this);
		
		add(gameButton);
		add(scoreButton);
		add(userinfoButton);
		add(logoutButton);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		
		if(ae.getSource() == gameButton) {
			// 게임 화면으로 이동
			mainFrame.changeScreen(Screen.GAME);
		}
		else if (ae.getSource() == scoreButton) {
			// 순위표 화면으로 이동
			mainFrame.changeScreen(Screen.SCORE);
		}
		else if (ae.getSource() == userinfoButton) {
			// 사용자 정보 화면으로 이동
			mainFrame.changeScreen(Screen.USERINFO);
		}
		else if (ae.getSource() == logoutButton) {
			// 로그인 화면으로 이동
			mainFrame.logout();
			mainFrame.changeScreen(Screen.LOGIN);
		}
		
	}	
}

class GamePanel extends JPanel implements ActionListener {
	private final MainFrame mainFrame;
	
	private final GameDisplay gameDisplay;
	
	private final JButton backButton;
	
	private final JLabel nextBlockLabel;
	private JLabel scoreLabel;
	
	// 키 이벤트 리스너 동작 테스트
	private final KeyEventListener keyEL;
	
	
	// 블럭 움직이기
	public Timer gameTimer;
    private int currentX = 150;
    private int currentY = 0;
    
    Random random = new Random();
    BlockType blockType;
    
    // 게임 점수
    public int score = 0;
	
	public GamePanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(null);
		
		gameDisplay = new GameDisplay(this.selectionBlockType(), this.selectionBlockType());
		gameDisplay.setLayout(null);
	
		gameDisplay.setBounds(50, 110, 510, 600);
		gameDisplay.setBackground(Color.BLACK);
		
		backButton = new JButton("뒤로가기");
		backButton.setBounds(600, 580, 150, 50);
		backButton.addActionListener(this);
		
		nextBlockLabel = new JLabel("Next Block");
		nextBlockLabel.setForeground(Color.WHITE);
		nextBlockLabel.setBounds(390, 5, 100, 30);
		
		scoreLabel = new JLabel("현재 점수: " + String.valueOf(score));
		scoreLabel.setForeground(Color.WHITE);
		scoreLabel.setBounds(390, 300, 100, 30);
		
		
		// 키 입력
		keyEL = new KeyEventListener(gameDisplay.fallingBlock, this);
		gameDisplay.setFocusable(true);
		gameDisplay.addKeyListener(keyEL);
		
		// 화면 움직이기(500ms마다 갱신)
		gameTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// 해당 윈도우에서 키 입력을 받도록 설정
            	gameDisplay.requestFocusInWindow();
            	
            	// Y좌표 30 증가(한 칸 아래로)
            	if (gameDisplay.fallingBlock.canMove(GameBoard.getBoard(), 
            			gameDisplay.fallingBlock.shape, 0, 1)) {
            		gameDisplay.fallingBlock.setBlockY(gameDisplay.fallingBlock.getBlockY() + 30);
            	}
            	// 블럭이 내려갈 수 없게 되면 모양 변경
            	else {
            		// 추가적으로 윗 공간이 없다면 게임 오버(lock 메서드로 현재 위치에 블럭을 놓을 수 있는지 확인)
            		if(!gameDisplay.fallingBlock.lock(GameBoard.getBoard())) {
            			gameTimer.stop();
            			System.out.println("최종 점수: " + score);
            			
            			// 점수 보내기
            			TCPTx tcp = new TCPTx();
            			String[] sendData = RequestData.createScoreRequest(String.valueOf(score), String.valueOf(mainFrame.getUserNumber()));
            			tcp.request(sendData);
            		
            			score = 0;
            			gameDisplay.fallingBlock.setBlockY(0);
                    	gameDisplay.fallingBlock.setBlockX(120);
                    	GameBoard.initBoard();
                    	return;
            		}
            		
            		// 모양 변경
                    gameDisplay.setFallingBlockType(gameDisplay.nextBlock.getBlockType());
                    gameDisplay.setNextBlockType(selectionBlockType());
                    gameDisplay.fallingBlock.setBlockY(0);
                    gameDisplay.fallingBlock.setBlockX(120);
            	}
            	
            	// 완성된 행이 있는지 체크
            	GameBoard.lineCheck(GamePanel.this);
                
            	// 블럭 좌표 최신화
                gameDisplay.updateBlockXY(gameDisplay.fallingBlock.getBlockX(), 
                		gameDisplay.fallingBlock.getBlockY());
                
                // 점수 최신화
                scoreLabel.setText("현재 점수: " + String.valueOf(score));
            }
        });
		
		add(gameDisplay);	
		add(backButton);
		gameDisplay.add(nextBlockLabel);
		gameDisplay.add(scoreLabel);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		
		if(ae.getSource() == backButton) {
			// 게임을 일시 정지 상태로 만든다.
			this.gameTimer.stop();
			new ConfirmationWindow(mainFrame, this);
		}
	}
	
	private BlockType selectionBlockType() {
		int num = random.nextInt(9);
		switch(num) {
		case 0:
			return BlockType.I;
		case 1:
			return BlockType.O;
		case 2:
			return BlockType.Z;
		case 3:
			return BlockType.S;
		case 4:
			return BlockType.J;
		case 5:
			return BlockType.L;
		case 6:
			return BlockType.T;
		case 7:
			return BlockType.X;
		case 8:
			return BlockType.A_T;
		default:
			return BlockType.A_T;
		}
	}
}

// 게임 중간에 나갈 때 출력되는 창
class ConfirmationWindow extends JFrame implements ActionListener {
	private final MainFrame mainFrame;
	private final GamePanel gamePanel;
	
	private final JButton button1;
	private final JButton button2;

	ConfirmationWindow(MainFrame mainFrame, GamePanel gamePanel) {
		this.mainFrame = mainFrame;
		this.gamePanel = gamePanel;
		
		new JFrame("확인 창");
		setLayout(new GridLayout(2, 1));
		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(null);
		add(topPanel);
		add(bottomPanel);
		
		JLabel label = new JLabel("정말로 나가시겠습니까? 현재 내용은 저장되지 않습니다.");
		topPanel.add(label);
		
		button1 = new JButton("게임 계속하기");
		button2 = new JButton("나가기");
		button1.setBounds(50, 0, 150, 50);
		button2.setBounds(250, 0, 150, 50);
		button1.addActionListener(this);
		button2.addActionListener(this);
		bottomPanel.add(button1);
		bottomPanel.add(button2);
		
		setResizable(false); // 사용자가 창 크기 변경을 못 하게 변경
		setSize(450, 150); // 기본 창 크기 450x150
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		
		if(ae.getSource() == button1) {
			this.dispose(); // 해당 창만 종료
			
			// 2초 뒤 게임 재개하도록 설정
			try {
				Thread.sleep(2000);
			} catch(Exception e) {
				System.out.println("Thread.sleep() 호출 에러");
			}
			gamePanel.gameTimer.start();					
		}
		else {
			this.dispose(); // 해당 창만 종료
			// 인게임 내용 초기화 코드 추가 해야 됨
			GameBoard.initBoard();
			
			// 메뉴 화면으로 나가기
			mainFrame.changeScreen(Screen.MENU);
		}
	}
}

class ScorePanel extends JPanel implements ActionListener {
	private final MainFrame mainFrame;
	private final JButton backButton;
	private String[] rankingTable;
	
	private final JLabel[] rankLabels = new JLabel[10];
	
	public ScorePanel(MainFrame mainFrame) {
		setLayout(null);
		this.mainFrame = mainFrame;
		
		// 라벨 위치 미리 조정
		for (int i = 0; i < 10; ++i) {
		    rankLabels[i] = new JLabel("");
		    rankLabels[i].setBounds(300, 30 + i * 30, 300, 30);
		    add(rankLabels[i]);
		}
		
		backButton = new JButton("돌아가기");
		
		backButton.addActionListener(this);
		backButton.setBounds(600, 580, 150, 50);
	
		add(backButton);				
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		
		if(ae.getSource() == backButton) {
			// 메인 화면으로 이동
			mainFrame.changeScreen(Screen.MENU);
		}		
	}
	
	public void setRankingTable(String[] rankingTable) {
		this.rankingTable = rankingTable;
	}
	
	// 랭킹 정보를 라벨에 입력
	public void displayRankingTable() {
		int count = Integer.parseInt(rankingTable[2]);
		
	    for (int i = 0; i < 10; ++i) {
	        if (i < count) {
	            rankLabels[i].setText((i + 1) + "위  " + rankingTable[3 + i] + "  " + rankingTable[13 + i] + "점");
	        } else {
	            rankLabels[i].setText((i + 1) + "위  -"); // 데이터가 없으면 '-'
	        }
	    }
	}
}

class UserInfoPanel extends JPanel implements ActionListener {
	private final MainFrame mainFrame;
	
	private final JButton button;
	private final JButton backButton;
	
	private String[] userInfo;
	private final JLabel[] userInfoLabels = new JLabel[3];
	
	public UserInfoPanel(MainFrame mainFrame) {
		setLayout(null); // 레이아웃 매니저를 사용하지 않는다.
		this.mainFrame = mainFrame;
		
		// 라벨 위치 미리 조정
		for (int i = 0; i < 3; ++i) {
			userInfoLabels[i] = new JLabel("");
			userInfoLabels[i].setBounds(280, 260 + i * 30, 300, 30);
		    add(userInfoLabels[i]);
		}
		
		button = new JButton("사용자 정보 화면");
		backButton = new JButton("돌아가기");
		
		backButton.addActionListener(this);
		backButton.setBounds(600, 580, 150, 50);
		
		add(button);
		add(backButton);
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		
		if(ae.getSource() == backButton) {
			// 메인 화면으로 이동
			mainFrame.changeScreen(Screen.MENU);
		}
		
	}
	
	public void setUserInfo(String[] userInfo) {
		this.userInfo = userInfo;
	}
	
	// 사용자 정보를 라벨에 입력
	public void displayUserInfo() {
		userInfoLabels[0].setText("Id: " + userInfo[2]);
		userInfoLabels[1].setText("Nickname: " + userInfo[3]);
		userInfoLabels[2].setText("Best Score: " + userInfo[4]);
	}
}

