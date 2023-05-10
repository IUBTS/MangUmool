# Spring-Project_MangUmool
SpringBoot+Thymeleaf+Python_전통주 쇼핑몰
##
## 🖥️ Project 소개
다양한 브랜드가 전통주를 판매할 수 있게 등록하고, 소비자들이 구매할 수 있는 전통주 쇼핑몰입니다.
##
## 🕰️ 개발 기간
2/12~3/14
##
## ⚙️ 개발 환경
- Windows 10
- Eclipse IDE for Enterprise Java and Web Developers 2022-6
- Apache Tomcat v9.0
- Oracle
- Jupyter Notebook 
- Anaconda3
## ⚙️ 기술스택
- HTML5
- CSS3
- Java
- JavaScript
- JQuery
- Spring Framework
- Mybatis
- Python
- Flask
---
## ERD
![image](https://github.com/IUBTS/MangUmool/assets/116045365/81da56cb-adf5-4129-b020-216407b1f020)

---
## FLOWCHART
- FLOWCHART(Customer)
![image](https://github.com/IUBTS/MangUmool/assets/116045365/e4c9a4ac-1bce-491f-b360-29afbf1e82a5)
###
- FLOWCHART(Vendor)
![image](https://github.com/IUBTS/MangUmool/assets/116045365/c6df13a4-7bd6-4901-bff4-ef2c508dae90)
###
- FLOWCHART(Admin)
![image](https://github.com/IUBTS/MangUmool/assets/116045365/c8818ac0-296b-4e52-8052-fead8c20a700)
###

## 📌 주요 기능

## Customer 
#### 회원가입 
- 주소 API 연동
- ID 중복 확인
- PWD 일치여부 확인
- 간편본인인증 API 연동  

#### 로그인 
- DB 검증 
- SpringSecurity 적용으로 권한 Session에 저장

#### 메인페이지 
- 검색 
- 카테고리 분류
- 슬라이드 배너

#### 검색결과 페이지
- 검색결과 Pagination 처리 (PageHelperDependency 사용)
- 각 상품 리뷰점수 별점으로 구현
- Python 연동하여 개인맞춤추천(잠재요인 협업 필터링) 시스템 구현

#### 상품상세정보
- 수량에 따른 구매하기 및 장바구니 구현
- 상품정보 표시
- review 목록
- Q&A 목록 및 문의하기 modal 구현
- Python 연동하여 해당 상품과 유사한 상품 추천(코사인 유사도 계산) 시스템 구현

#### 장바구니
- 수량 변경 및 삭제 (DB연동)
- 체크된 상품의 총액 표기 및 구매 구현
- 상품 전체선택, 전체 선택해제 구현

#### 상품구매
- 배송정보 입력
- 결제 API 연동 (KAKAO 간편결제_테스트용 , KG이니시스_테스트용)

#### 주문목록
- 날짜별 주문목록 구현
- 배송정보 표시
- 리뷰작성
- 주문취소
- 주문내역 상세보기 (실시간 택배조회 API 연동)

#### 내정보
- 비밀번호 수정 : 암호화 저장
- Email 수정 : SpringEmail 인증 구현
- 주소수정 : 주소찾기 API 연동
---
## Vendor

#### 회원가입
- ID 중복체크
- 주소찾기 API연동

#### 로그인
- 관리자 허가에 따른 판매자 회원가입 승인여부 체크

#### 주문요청목록
- 등록한 상품들의 주문목록 Pagination으로 구현
- 송장번호, 택배사 입력 기능
- 배송완료 버튼 활성화
- 배송조회 API 연동
- 미배송된 주문목록 Excel 파일 다운로드 구현 (Excel download poi dependency)

#### 상품등록
- 상품 사진 등록
- 썸네일 사진과 상세정보 사진 구분해서 저장
- 저장 시 판매창 게시 대기 설정

#### 등록상품 목록
- 등록상품 Pagination 구현
- 판매여부 승인상태 표시

#### 등록상품 상세보기
- 상세정보 수정
- 첨부파일 추가 삭제
- 수정완료 후 상품 게시 대기

#### 취소요청목록
- 취소요청 날짜 및 내용 
- 승인버튼

#### Q&A목록
- 등록한 상품에 대한 Question 목록
- Answer등록 modal로 구현
---
## Admin

#### 메인페이지
- 상품판매 요청 리스트 조회 및 승인
- 판매자 가입 요청 리스트 조회 및 승인
- 취소된 주문 리스트 조회
- 기간별 주문 리스트 조회
- 주문번호로 주문내역 조회
- 고객ID, 판매자ID 로 정보 조회
