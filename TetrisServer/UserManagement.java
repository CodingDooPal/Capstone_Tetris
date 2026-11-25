import java.sql.*;

public class UserManagement {

    public String[] userLogin(String[] receiveData) {
        // receiveData[]
        // [0]: TCPCommand(LOGIN), [1]: id, [2]: pw

        // sendData[]
        // [0]: TCPCommand(LOGIN), [1]: Result Message, [2]: user_number

        String[] sendData = new String[3];
        sendData[0] = TCPCommand.LOGIN.name();

        try (Connection con = DBConnector.getConnection();
        Statement stmt = con.createStatement()) {
            // id로 pw, user_number를 반환
            String query = "SELECT pw, user_number FROM user_info WHERE id = '" + receiveData[1] + "'";
            ResultSet rs = stmt.executeQuery(query);

            // 값이 존재하면 비밀번호 매치 시도
            if(rs.next()) {
                String pw = rs.getString("pw");
                // 비밀번호가 일치하면 로그인 성공 및 user_number를 sendData에 저장
                if(pw.equals(receiveData[2])) {
                    sendData[1] = TCPCommand.SUCCESS.name();
                    sendData[2] = String.valueOf(rs.getInt("user_number"));
                }
                else {
                    sendData[1] = "비밀번호가 일치하지 않습니다.";
                }
            } 
            else {
                sendData[1] = "존재하지 않는 ID입니다.";
            }
        } catch(SQLException e) {
            System.out.println("DB 접속 실패");
            sendData[1] = "DB 접속에 실패했습니다.";
            return sendData;
        }

        return sendData;
    }

    public String[] userSignup(String[] receiveData) {
        // receiveData[]
        // [0]: TCPCommand(SIGN_UP), [1]: id, [2]: pw, [3]: repw, [4]: nickname

        /* sendData[]
        * 
        * 입력 조건을 충족하지 못 한 경우
        * [0]: TCPCommand(SIGN_UP), [1]: Result Message
        * [2]~ 충족하지 못 한 조건 반환
        *  
        * 각 필드의 입력 조건을 만족하고 중복 여부를 확인하는 경우
        * [0]: TCPCommand(SIGN_UP), [1]: Result Message
        * [2]~ 충족하지 못 한 조건 반환
        */

        String[] sendData = new String[10];
        sendData[0] = TCPCommand.SIGN_UP.name();
        int idx = 2;

        // ID에 내용을 작성했는지 확인
        if(receiveData[1].equals("")) {
            sendData[idx++] = "아이디를 입력해 주세요.";
        }
        // ID의 길이 조건을 충족했는지 확인
        else if(receiveData[1].length() < 6 || receiveData[1].length() > 12) {
            sendData[idx++] = "아이디는 6~12자 입니다.";
        }

        // 패스워드에 내용을 작성했는지 확인
        if(receiveData[2].equals("")) {
            sendData[idx++] = "패스워드를 입력해 주세요.";
        }
        // 패스워드의 길이 조건을 충족했는지 확인
        else if(receiveData[2].length() < 8 || receiveData[2].length() > 16) {
            sendData[idx++] = "패스워드는 8~16자 입니다.";
        }

        // 패스워드 재입력에 내용을 작성했는지 확인
        if(receiveData[3].equals("")) {
            sendData[idx++] = "패스워드를 재입력해 주세요.";
        }
         // 패스워드와 재입력한 패스워드가 동일한지 확인
        else if(!receiveData[2].equals(receiveData[3])) {
            sendData[idx++] = "패스워드가 일치하지 않습니다.";
        }

        // 닉네임을 작성했는지 확인
        if(receiveData[4].equals("")) {
            sendData[idx++] = "닉네임을 입력해 주세요.";
        }
        // 닉네임의 길이 조건을 충족했는지 확인
        else if(receiveData[4].length() < 2 || receiveData[4].length() > 8) {
            sendData[idx++] = "닉네임은 2~8자 입니다.";
        }

        if(sendData[2] == null) {
            try (Connection con = DBConnector.getConnection(); 
            Statement stmt = con.createStatement()) {
                // 같은 이름, 닉네임이 있는지 카운트 해서 반환
                String queryId = "SELECT COUNT(*) FROM user_info WHERE id = '" + receiveData[1] + "'";
                String queryNickname = "SELECT COUNT(*) FROM user_info WHERE nickname = '" + receiveData[4] + "'";

                ResultSet rsId = stmt.executeQuery(queryId);
                rsId.next();
                int idCount = rsId.getInt(1);

                ResultSet rsNickname = stmt.executeQuery(queryNickname);
                rsNickname.next();
                int nicknameCount = rsNickname.getInt(1);

                // id, nickname이 이미 존재한다면 sendData에 오류 메시지 저장
                if(idCount > 0) {
                    sendData[idx++] = "이미 존재하는 ID 입니다.";
                }

                if (nicknameCount > 0){
                    sendData[idx++] = "이미 존재하는 닉네임입니다.";
                }    

                // id, nickname이 중복되지 않는다면 데이터베이스에 새로운 회원정보 저장 후
                // sendData에 성공 메시지 저장
                if (sendData[2] == null) {
                    String querySaveInfo = "INSERT INTO user_info (id, pw, nickname) VALUES ('" +
                    receiveData[1] + "', '" +
                    receiveData[2] + "', '" +
                    receiveData[4] + "');";
                    stmt.executeUpdate(querySaveInfo);
                    sendData[1] = TCPCommand.SUCCESS.name();
                }
                else {
                    sendData[1] = "중복 에러 목록";
                }

            } catch(SQLException e) {
                System.out.println("DB 접속 실패");
                sendData[1] = "DB 접속에 실패했습니다.";
                return sendData;
            }
        }
        else {
            sendData[1] = "회원가입 에러 목록";
        }

        return sendData;
    }

    public String[] updateScore(String[] receiveData) {
        // receiveData[]
        // [0]: TCPCommand(UPDATE_SCORE), [1]: Score, [2]: User_Number

        // sendData[]
        // [0]: TCPCommand(UPDATE_SCORE), [1]: Result Message

        String[] sendData = new String[2];
        sendData[0] = TCPCommand.UPDATE_SCORE.name();

        try (Connection con = DBConnector.getConnection(); 
        Statement stmt = con.createStatement()) {
            // user_number로 기존 최고 점수를 반환
            String queryScore = "SELECT best_score FROM user_info WHERE user_number = '" + receiveData[2] +
            "';";
            ResultSet rs = stmt.executeQuery(queryScore);

            // 사용자 정보가 있으면
            if(rs.next()) {
                int currentScore = rs.getInt("best_score");
                // 기존 최고 점수와 비교하여 더 클 경우에만 best_score를 update
                if(Integer.parseInt(receiveData[1]) > currentScore) {
                    String queryUpdate = "UPDATE user_info SET best_score = " + receiveData[1] +
                    " where user_number = " + receiveData[2] + ";";
                    stmt.executeUpdate(queryUpdate);
                }
                sendData[1] = TCPCommand.SUCCESS.name();
            }
            else {
                sendData[1] = "사용자 정보를 찾을 수 없습니다.";
            }
        } catch(SQLException e) {
            System.out.println("DB 접속 실패");
            sendData[1] = "DB 접속에 실패했습니다.";
            return sendData;
        }

        return sendData;
    }

    String[] getUserInfo(String[] receiveData) {
        // receiveData[]
        // [0]: TCPCommand(UPDATE_SCORE), [1]: User_Number

        // sendData[]
        // [0]: TCPCommand(GET_USER_INFO), [1]: Result Message
        // [2]: id, [3]: nickname, [4]: best_score 
        String[] sendData = new String[5];
        sendData[0] = TCPCommand.GET_USER_INFO.name();

        try (Connection con = DBConnector.getConnection();
        Statement stmt = con.createStatement()) {
            // user_number로 해당 유저의 id, nickname, best_score 반환
            String query = "SELECT id, nickname, best_score FROM user_info WHERE user_number = '" + receiveData[1] +
            "';";
            ResultSet rs = stmt.executeQuery(query);

            // 데이터가 있으면 sendData에 저장
            if(rs.next()) {
                sendData[1] = TCPCommand.SUCCESS.name();
                sendData[2] = rs.getString("id");
                sendData[3] = rs.getString("nickname");
                sendData[4] = String.valueOf(rs.getInt("best_score"));
            }
            else {
                sendData[1] = "사용자 정보를 찾을 수 없습니다.";
            }
        } catch(SQLException e) {
            System.out.println("DB 접속 실패");
            sendData[1] = "DB 접속에 실패했습니다.";
            return sendData;
        }

        return sendData;
    }

    String[] getRankingTable() {
        // sendData[]
        // [0]: TCPCommand(GET_RANKING_TABLE), [1]: Result Message, [2]: Data Count
        // [3]~[12]: 순위에 따른 User Nickname 저장
        // [13]~[22]: 내림차순으로 User Score 저장
        String[] sendData = new String[23];
        sendData[0] = TCPCommand.GET_RANKING_TABLE.name();

        try (Connection con = DBConnector.getConnection();
        Statement stmt = con.createStatement()) {
            // best_score를 내림차순으로 최대 10개 뽑아서 사용
            // 동점일 경우 nickname을 오름차순으로 정렬 => nickname이 UNIQUE라서 괜찮다.
            String query = "SELECT nickname, best_score FROM user_info ORDER BY best_score DESC, nickname ASC LIMIT 10";
            ResultSet rs = stmt.executeQuery(query);

            // 찾은 데이터를 sendData에 저장
            int count = 0;
            while(rs.next()) {
                sendData[3 + count] = rs.getString("nickname");
                sendData[13 + count] = String.valueOf(rs.getInt("best_score"));
                ++count;
            }
            sendData[1] = TCPCommand.SUCCESS.name();
            sendData[2] = String.valueOf(count);
        } catch(SQLException e) {
            System.out.println("DB 접속 실패");
            sendData[1] = "DB 접속에 실패했습니다.";
            return sendData;
        }

        return sendData;
    }

    public UserManagement() {

    }

}