지도위의 발자취 웹앱 프로젝트
기본 : [Spring Boot, JPA(Java Persistence API), Thymeleaf] 
[Google OAuth 2.0] : Google 인증을 위해 사용
[Google Drive API v3] : 노트북 용량 없어서 임시사용
[gson] : JSON 객체 <-> java 객체
[metadata-extractor] : img metadata추출

02/~  : html, css, js 템플릿 수정+적용 완료.

02/~  : thymeleaf 적용 완료.

02/~  : 회원가입 로직 구현 완료.               

[email 인증, 카카오, 네이버 로그인 구현 필요]

02/~  : 로그인 구현 완료.

02/~  : 비회원 페이지 컨트롤러 제작 완료.

02/~  : 세션 인증 방식의 회원 페이지 컨트롤러 제작 완료.


03/01 : 구글 드라이브 api 연동 완료.


03/04 : 구글 드라이브 api CRUD 중 C, R 구현 완료.

03/04 : 구글 드라이브 api img url => html 이슈 해결, 외부 테스트중 https -> http 포워딩시 파일 업로드 불가 오류 해결

03/05 : 3. sgis api 사용법 알아보기  https://sgis.kostat.go.kr/developer/html/newOpenApi/api/intro.html#2

03/10 : 
구글 드라이브 이미지 url 호스팅 지원 종료(2024.01부터 점차적으로 막는듯 함)
외부 저장소로 변경

todo list

1. 구글 드라이브 delete 구현하기
2. 이미지 업로드시 gps정보 [wgs84 좌표] => utm-k 변환 db 저장 구현하기
