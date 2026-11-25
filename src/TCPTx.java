import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

enum TCPCommand {
	LOGIN, SIGN_UP, UPDATE_SCORE, GET_USER_INFO, GET_RANKING_TABLE,
	SUCCESS
}

public class TCPTx {
	private InputStream is;
	private OutputStream os;
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private String[] receiveData;
	
	public TCPTx() {
		try {
			socket = new Socket("localhost", 8090);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 데이터 송수신 호출 함수
	public String[] request(String[] sendData) {
		sendMessage(sendData);
    	receiveData = receiveMessage();
    	
    	return receiveData;
	}
	
	// 데이터 송신
	private void sendMessage(String[] sendData) {
		try {
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
			oos.writeObject(sendData);
			oos.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	// 데이터 수신
	private String[] receiveMessage() {
		String[] receiveData = null;
		try {
			is = socket.getInputStream();
			ois = new ObjectInputStream(is);
			receiveData = (String[])ois.readObject();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return receiveData;
	}
}
