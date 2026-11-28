# Capstone_Tetris

## 목차
- [1. 프로젝트 소개](#1-프로젝트-소개)
- [2. 주요 기능](#2-주요-기능)
- [3. 사용 기술](#3-사용-기술)
- [4. 설치 및 실행 방법](#4-설치-및-실행-방법)
- [5. 조작법](#5-조작법)

### 1. 프로젝트 소개



### 2. 주요 기능



### 3. 사용 기술

---

### 4. 설치 및 실행 방법

본문에서 설명하는 설치 및 실행 방법은 다음과 같은 사용자 환경을 요구합니다.<br>

**·** Java가 설치된 환경<br>
**·** MySQL이 설치된 환경<br>

---
<br>

1\. `TetrisClient/src` 폴더와 `TetrisServer` 폴더를 각각 다운로드합니다. 

\- git이 설치된 환경에서는 다음 명령어를 이용하여 다운로드 할 수 있습니다.<br>

```bash
git clone --depth 1 https://github.com/CodingDooPal/Capstone_Tetris.git
```

\- 만약 git이 설치된 환경이 아니거나 그냥 명령어를 치기가 귀찮다면 아래 사이트에 들어가면 바로 다운로드가 됩니다. 만약 안 된다면 직접 다운로드 버튼을 누르세요.<br>

**·** [Down Git 이동](https://downgit.github.io/#/home?url=https:%2F%2Fgithub.com%2FCodingDooPal%2FCapstone_Tetris%2Ftree%2Fmain)<br>

2\. 다운로드 받은 폴더의 다음 경로로 들어가 `DBConnector.java`라는 이름의 파일을 엽니다.

\- 파일 경로<br>
`Capstone_Tetris\TetrisServer\DBConnector.java`

![DBConnector.png](DBConnector.png)<br>

5행에 위치한 PW 변수를 자신이 설정한 MySQL ROOT 비밀번호로 변경 후 저장합니다.<br>
+) 추가적으로 MySQL이 3306이 아닌 다른 PORT를 사용 중이라면 DB_SERVER_URL 변수에서 PORT 번호를 변경하면 됩니다.

3\. cmd에서 다운로드 받은 TetrisServer 폴더의 경로로 이동합니다. 이후 아래의 명령어를 입력하여 서버를 컴파일 후 실행할 수 있습니다.<br>

\- 폴더 경로<br>
`Capstone_Tetris\TetrisServer>`

\- 서버 컴파일 및 실행 명령어

```bash
# 컴파일 명령어
javac -cp ".;lib/mysql-connector-j-26.7.0.jar" *.java

# 실행 명령어
java -cp ".;lib/mysql-connector-j-26.7.0.jar" TetrisServer
```

4\. 다른 cmd 창에서 다운로드 받은 TetrisClient폴더의 .\src 경로로 이동합니다. 이후 아래의 명령어를 입력하여 클라이언트를 컴파일 후 실행할 수 있습니다.

\- 폴더 경로<br>
`Capstone_Tetris\TetrisClient\src>`

\- 클라이언트 컴파일 및 실행 명령어

```bash
# 컴파일 명령어
javac *.java

# 실행 명령어
java TetrisClientMain
```

5\. 마지막으로 회원가입 후 로그인을 진행하면 됩니다. 회원 정보는 localhost에 위치한 MySQL 서버에 저장이 됩니다. ~(어차피 회원가입 과정에서 실제 개인정보는 필요 없다.)~





### 5. 조작법
