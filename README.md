## 🩺 시간과 공간의 제약이 없는 의사와의 채팅 & 게시글 상담 서비스

## 1. 팀원소개 
| 역할    | 이름   | 주요 담당 업무 |
|--------|--------|----------------|
| 팀장    | 김예나 | 알람, 비동기, 서버분리 |
| 부팀장 | 김민재 | 채팅 |
| 팀원 | 김수연 | 인증인가, CI/CD |
| 팀원    | 반효승 | 쿠폰, 동시성 제어, 비동기 |
| 팀원    | 원채빈 | PG, 동시성 제어, 비동기 |
| 팀원    | 홍수경 | 유료 질문 게시판, 포인트, 동시성 제어, 로그관리 |

## 2. 프로젝트 개요 
개발 기간 : 2025.04.01 - 2025.05.06

`Docconneting`은 사용자가 시간과 공간 제약 없이 언제 어디서든 의사와 채팅이나 게시글로 건강 관련 상담을 받을 수 있는 편리한 건강 및 피트니스 앱입니다. 특히 의사가 댓글을 달거나 사용자가 유료 게시물을 올렸을 때 의사와 환자에게 실시간으로 알람이 가기 때문에 즉각적인 피드백을 들을 수 있고, 48시간 이내에  유료 질문에 아무런 답변이 들리지 않는다면 사용자에게 결제 금액을 환불해주는 환불 정책을 가지고 있는 보상정책을 가지고 있는 서비스입니다.

## 3. 주요 기술 스택

<h3 align="center">애플리케이션</h3>

<p align="center">
  <img src="https://img.shields.io/badge/JAVA-FF7F00?style=for-the-badge&logo=&logoColor=white">
  <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/JPA-808080?style=for-the-badge&logo=&logoColor=white">
</p>

<h3 align="center">인증 및 보안</h3>

<p align="center">
  <img src="https://img.shields.io/badge/JWT-000?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
</p>

<h3 align="center">메시징 및 비동기 처리</h3>

<p align="center">
  <img src="https://img.shields.io/badge/RABBITMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=FFFFFF">
  <img src="https://img.shields.io/badge/Spring Webflux-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
  <img src="https://img.shields.io/badge/Websocket-5cffd1?style=for-the-badge&logo=&logoColor=white">
</p>

<h3 align="center">데이터베이스</h3>

<p align="center">
  <img src="https://img.shields.io/badge/MYSQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white">
</p>

<h3 align="center">클라우드 및 CI/CD</h3>

<p align="center">
  <img src="https://img.shields.io/badge/DOCKER-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">
  <img src="https://img.shields.io/badge/AMAZON S3-569A31?style=for-the-badge&logo=amazonwebservices&logoColor=white">
  <img src="https://img.shields.io/badge/AMAZON EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
</p>

<h3 align="center">로그관리</h3>

<p align="center">
  <img src="https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white">
  <img src="https://img.shields.io/badge/logstash-005571?style=for-the-badge&logo=logstash&logoColor=white">
  <img src="https://img.shields.io/badge/kibana-005571?style=for-the-badge&logo=kibana&logoColor=white">
</p>

<h3 align="center">협업 도구</h3>

<p align="center">
  <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=Slack&logoColor=white">
  <img src="https://img.shields.io/badge/ Git-F05032?style=for-the-badge&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/ GitHub-000000?style=for-the-badge&logo=GitHub&logoColor=white">
  <img src="https://img.shields.io/badge/ Notion-E92063?style=for-the-badge&logo=Notion&logoColor=white">
</p>

## 4. 서비스 플로우

1. **회원가입**  
   사용자는 의사 회원, 환자 회원 2가지로 나누어져 있습니다.

1. **의사조회**  
   사용자는 의사들의 정보를 조회하면서 채팅하고 싶은 의사를 고를 수 있습니다.

1. **결제**  
   결제는 총 2가지 방식(채팅결제, 포인트 충전)이 존재합니다. 의사와의 채팅을 하기 위해서 사용자는 소지금으로 결제하고, 유료 게시물 같은 경우는 오직 포인트를 사용해서 결제할 수 있기 때문에 포인트를 충전하기 위한 결제가 존재합니다.
     
1. **채팅**  
     사용자는 원하는 의사를 고른  후, 결제를 완료하면 해당 의사와의 채팅방이 생성되며 이때 의사는 채팅진료 요청이 들어왔다는 알람을 받습니다

1. **게시글**  
     게시글은 2가지 종류가 있습니다. 유료게시물의 경우, 진료 고민을 입력하고 게시하면 해당 게시들의 카테고리에 해당되는 의사들에게 알람이 가며, 48시간 이내에 아무런 답변이 오지 않는다면 결제에 사용된 포인트는 소멸됩니다. 무료게시물의 경우는 알람이 가지 않고 게시판에 업로드만 됩니다.

1. **쿠폰**  
     사용자는 한정된 수량의 쿠폰을 발급받아 유료 게시물을 올릴 때 포인트 대신 사용할 수 있습니다. 또한 자신이 소유하고 있는 쿠폰 목록을 조회할 수 있습니다.


<h3>[결제 로직]</h3>

<p align="center">
  <img src="https://github.com/user-attachments/assets/78634d5b-4b12-405a-bebf-13bf48654425" width="580px">
</p>

<h3>[알림 로직]</h3>

<p align="center">
  <img src="https://github.com/user-attachments/assets/619ff468-02fd-44e4-804c-4d271ac90981" width="580px">
</p>

<h3>[채팅 로직]</h3>

<p align="center">
  <img src="https://github.com/user-attachments/assets/fbc2ef1f-1ad9-45e3-99d0-78c06eb8e6f5" width="580px">
</p>


## 5. 아키텍쳐
<p align="center">
  <img src="https://github.com/user-attachments/assets/e0ca9c0f-d859-461a-a9a1-a3ee1c0745c0" width="580px">
</p>

## 6. ERD
<p align="center">
  <img src="https://github.com/user-attachments/assets/441265a6-1a1c-4943-8d97-1683784f1fdf" width="580px">
</p>

## 7. [API 명세서](https://linen-town-985.notion.site/API-1e68f60328ad80699326f21c4a97ef3e)

## 8. [기술적 의사결정](https://www.notion.so/1e697548af5f8070a6c3faa6c7e5380a)

## 9. [트러블 슈팅 & 최적화 전략](https://www.notion.so/1e697548af5f80a4b299db76146051c9)
