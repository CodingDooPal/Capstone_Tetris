// TCPCommand에 따른 형식별 RequestData 제작
public class RequestData {
	public static String[] createLoginRequest(String id, String pw) {
		String[] data = new String[3];
		data[0] = TCPCommand.LOGIN.name();
		data[1] = id;
		data[2] = pw;
		
		return data;
	}
	
	public static String[] createSignupRequest(String id, String pw, String repw, String nickname) {
		String[] data = new String[5];
		data[0] = TCPCommand.SIGN_UP.name();
		data[1] = id;
		data[2] = pw;
		data[3] = repw;
		data[4] = nickname;
		
		return data;
	}
	
	public static String[] createScoreRequest(String score, String userNumber) {
		String[] data = new String[3];
		data[0] = TCPCommand.UPDATE_SCORE.name();
		data[1] = score;
		data[2] = userNumber;
		
		return data;
	}
	
	public static String[] createUserInfoRequest(String userNumber) {
		String[] data = new String[2];
		data[0] = TCPCommand.GET_USER_INFO.name();
		data[1] = userNumber;
		
		return data;
	}
	
	public static String[] createRankingTableRequest() {
		String[] data = new String[1];
		data[0] = TCPCommand.GET_RANKING_TABLE.name();
		
		return data;
	}
	
	private RequestData() {
		
	}
}
