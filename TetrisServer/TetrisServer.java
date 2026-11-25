import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

enum TCPCommand {
	LOGIN, SIGN_UP, UPDATE_SCORE, GET_USER_INFO, GET_RANKING_TABLE,
    SUCCESS // SUCCESS: 결과 메시지
}

/* cmd 실행 시 
    컴파일: javac -cp ".;lib/mysql-connector-j-26.7.0.jar" *.java
    실행: java -cp ".;lib/mysql-connector-j-26.7.0.jar" TetrisServer
*/

public class TetrisServer extends Thread {
    private InputStream is;
    private OutputStream os;
    private ServerSocket serverSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final UserManagement um = new UserManagement();
    Socket socket;
    
    public void run() {
        DBInitializer initializer = new DBInitializer();
        try {
            initializer.init();
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            serverSocket = new ServerSocket(8090);
            while(true) {
                System.out.println("수신가능");
                socket = serverSocket.accept();
                System.out.println("Client: " + socket.getInetAddress());
                is = socket.getInputStream();
                os = socket.getOutputStream();
                ois = new ObjectInputStream(is);
                oos = new ObjectOutputStream(os);

                // 수신할 데이터가 있으면 받는다.
                String[] receiveData = (String[])ois.readObject();
                dispatch(receiveData);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // 배열 첫 번째 원소 -> TCPCommand
    // TCPCommand에 맞는 작업을 진행하는 함수
    private void dispatch(String[] receiveData) {
    TCPCommand cmd;
    // 알 수 없는 명령어는 예외 처리
    try {
        cmd = TCPCommand.valueOf(receiveData[0]);
    } catch (Exception e) {
        System.out.println("알 수 없는 명령: " + receiveData[0]);
        return;
    }

    switch (cmd) {
        case LOGIN:
            sendMessage(um.userLogin(receiveData));
            break;
        case SIGN_UP:
            sendMessage(um.userSignup(receiveData));
            break;
        case UPDATE_SCORE:
            sendMessage(um.updateScore(receiveData));
            break;
        case GET_USER_INFO:
            sendMessage(um.getUserInfo(receiveData));
            break;
        case GET_RANKING_TABLE:
            sendMessage(um.getRankingTable());
            break;
        default:
            break;
        }
    }

    // 데이터 전송함수
    private void sendMessage(String[] sendData) {
        try {
            oos.writeObject(sendData);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        new TetrisServer().start();
    }
}