INSERT INTO departments (name, description, established_date) VALUES
  ('개발팀', '소프트웨어 개발 및 시스템 유지보수를 담당하는 부서입니다.',
   '2020-01-15'),
  ('인사팀', '인사 관리, 채용, 교육 및 복리후생을 담당하는 부서입니다.',
   '2019-03-01'),
  ('마케팅팀', '마케팅 전략 수립, 홍보 및 브랜딩을 담당하는 부서입니다.',
   '2020-06-10'),
  ('영업팀', '고객 관리, 영업 활동 및 매출 관리를 담당하는 부서입니다.',
   '2019-11-20'),
  ('기획팀', '사업 기획, 전략 수립 및 프로젝트 관리를 담당하는 부서입니다.',
   '2021-02-05'),
  ('재무팀', '회계, 예산 관리 및 재무 계획을 담당하는 부서입니다.',
   '2019-08-12'),
  ('총무팀', '사무 관리, 시설 운영 및 법무를 담당하는 부서입니다.',
   '2019-05-30'),
  ('IT팀', 'IT 인프라 구축, 보안 및 기술 지원을 담당하는 부서입니다.',
   '2020-09-01'),
  ('품질관리팀', '제품 품질 검증, QA 및 품질 개선을 담당하는 부서입니다.',
   '2020-04-15'),
  ('고객서비스팀', '고객 상담, A/S 및 고객 만족도 관리를 담당하는 부서입니다.',
   '2019-12-01'),
  ('구매팀', '자재 구매, 공급업체 관리 및 조달을 담당하는 부서입니다.',
   '2020-02-20'),
  ('생산팀', '제품 생산, 공정 관리 및 생산성 향상을 담당하는 부서입니다.',
   '2019-07-10'),
  ('디자인팀', 'UI/UX 디자인, 브랜드 디자인 및 크리에이티브를 담당하는 부서입니다.',
   '2020-11-05'),
  ('법무팀', '계약서 검토, 법적 자문 및 컴플라이언스를 담당하는 부서입니다.',
   '2021-01-12'),
  ('홍보팀', '대외 홍보, 언론 대응 및 이벤트 기획을 담당하는 부서입니다.',
   '2020-08-18'),
  ('연구개발팀', '신제품 연구, 기술 개발 및 특허 관리를 담당하는 부서입니다.',
   '2020-03-25'),
  ('물류팀', '재고 관리, 배송 및 창고 운영을 담당하는 부서입니다.',
   '2019-10-08'),
  ('보안팀', '정보 보안, 물리적 보안 및 위험 관리를 담당하는 부서입니다.',
   '2021-04-22'),
  ('교육훈련팀', '직원 교육, 역량 개발 및 교육 프로그램 운영을 담당하는 부서입니다.',
   '2020-07-03'),
  ('감사팀', '내부 감사, 리스크 평가 및 컴플라이언스 점검을 담당하는 부서입니다.',
   '2021-03-16'),
  ('경영기획팀', '중장기 경영 전략 수립, 성과 관리 및 조직 운영을 담당하는 부서입니다.',
   '2021-05-10');


-- employees 데이터
INSERT INTO employees (department_id, name, email, position, hire_date, status) VALUES
    (1,  '홍길동',   'hong@example.com',   '주임',   '2021-04-15', 'ACTIVE'),   -- 개발팀
    (2,  '김철수',   'kim@example.com',    '대리',   '2020-08-20', 'ACTIVE'),   -- 인사팀
    (3,  '이영희',   'lee@example.com',    '과장',   '2019-11-05', 'ACTIVE'),   -- 마케팅팀
    (4,  '박민수',   'park@example.com',   '사원',   '2022-01-10', 'ACTIVE'),   -- 영업팀
    (5,  '최수진',   'choi@example.com',   '차장',   '2018-07-01', 'ACTIVE'),   -- 기획팀
    (6,  '정우성',   'jung@example.com',   '부장',   '2017-05-22', 'ACTIVE'),   -- 재무팀
    (7,  '한가영',   'han@example.com',    '사원',   '2021-09-14', 'ACTIVE'),   -- 총무팀
    (8,  '오지훈',   'oh@example.com',     '대리',   '2020-06-03', 'ACTIVE'),   -- IT팀
    (9,  '서지혜',   'seo@example.com',    '과장',   '2019-03-28', 'ACTIVE'),   -- 품질관리팀
    (10, '양현석',   'yang@example.com',   '사원',   '2022-02-11', 'ACTIVE'),   -- 고객서비스팀
    (11, '강민호',   'kang@example.com',   '대리',   '2021-07-20', 'ACTIVE'),   -- 구매팀
    (12, '신은주',   'shin@example.com',   '과장',   '2019-05-16', 'ACTIVE'),   -- 생산팀
    (13, '조윤아',   'cho@example.com',    '사원',   '2022-03-05', 'ACTIVE'),   -- 디자인팀
    (14, '배성훈',   'bae@example.com',    '차장',   '2018-09-11', 'ACTIVE'),   -- 법무팀
    (15, '임하늘',   'lim@example.com',    '대리',   '2020-10-22', 'ACTIVE'),   -- 홍보팀
    (16, '문지호',   'moon@example.com',   '연구원', '2021-12-02', 'ACTIVE'),   -- 연구개발팀
    (17, '장유진',   'jang@example.com',   '사원',   '2022-06-19', 'ACTIVE'),   -- 물류팀
    (18, '황지민',   'hwang@example.com',  '대리',   '2020-08-30', 'ACTIVE'),   -- 보안팀
    (19, '권수연',   'kwon@example.com',   '사원',   '2021-11-08', 'ACTIVE'),   -- 교육훈련팀
    (20, '유재석',   'yoo@example.com',    '과장',   '2019-02-17', 'ACTIVE'),   -- 감사팀
    (21, '이서준',   'lee.seo@example.com','부장',   '2018-04-23', 'ACTIVE');   -- 경영기획팀

INSERT INTO employees (department_id, profile_image_id, name, email, position, hire_date, status) VALUES
-- 1) 개발팀 (5)
((SELECT id FROM departments WHERE name='개발팀'), NULL, '개발자01', 'emp001@hrbank.local', '주니어 개발자', '2021-01-10', 'ACTIVE'),
((SELECT id FROM departments WHERE name='개발팀'), NULL, '개발자02', 'emp002@hrbank.local', '백엔드 개발자', '2021-05-22', 'ACTIVE'),
((SELECT id FROM departments WHERE name='개발팀'), NULL, '개발자03', 'emp003@hrbank.local', '프론트엔드 개발자', '2022-02-14', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='개발팀'), NULL, '개발자04', 'emp004@hrbank.local', '시니어 개발자', '2023-07-03', 'ACTIVE'),
((SELECT id FROM departments WHERE name='개발팀'), NULL, '개발자05', 'emp005@hrbank.local', '팀장', '2020-11-01', 'ACTIVE'),

-- 2) 인사팀 (5)
((SELECT id FROM departments WHERE name='인사팀'), NULL, '인사담당01', 'emp006@hrbank.local', '인사 담당', '2020-03-11', 'ACTIVE'),
((SELECT id FROM departments WHERE name='인사팀'), NULL, '인사담당02', 'emp007@hrbank.local', '채용 매니저', '2021-08-19', 'ACTIVE'),
((SELECT id FROM departments WHERE name='인사팀'), NULL, '인사담당03', 'emp008@hrbank.local', '교육 담당', '2022-11-07', 'ACTIVE'),
((SELECT id FROM departments WHERE name='인사팀'), NULL, '인사담당04', 'emp009@hrbank.local', 'HR 매니저', '2023-04-04', 'RESIGNED'),
((SELECT id FROM departments WHERE name='인사팀'), NULL, '인사담당05', 'emp010@hrbank.local', '팀장', '2024-06-15', 'ACTIVE'),

-- 3) 마케팅팀 (5)
((SELECT id FROM departments WHERE name='마케팅팀'), NULL, '마케터01', 'emp011@hrbank.local', '마케팅 스페셜리스트', '2021-02-18', 'ACTIVE'),
((SELECT id FROM departments WHERE name='마케팅팀'), NULL, '마케터02', 'emp012@hrbank.local', '브랜드 매니저', '2022-05-25', 'ACTIVE'),
((SELECT id FROM departments WHERE name='마케팅팀'), NULL, '마케터03', 'emp013@hrbank.local', '콘텐츠 마케터', '2020-09-09', 'ACTIVE'),
((SELECT id FROM departments WHERE name='마케팅팀'), NULL, '마케터04', 'emp014@hrbank.local', '디지털 마케터', '2023-10-12', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='마케팅팀'), NULL, '마케터05', 'emp015@hrbank.local', '팀장', '2024-01-31', 'ACTIVE'),

-- 4) 영업팀 (5)
((SELECT id FROM departments WHERE name='영업팀'), NULL, '영업사원01', 'emp016@hrbank.local', '영업 사원', '2019-12-10', 'ACTIVE'),
((SELECT id FROM departments WHERE name='영업팀'), NULL, '영업사원02', 'emp017@hrbank.local', '영업 매니저', '2021-06-06', 'ACTIVE'),
((SELECT id FROM departments WHERE name='영업팀'), NULL, '영업사원03', 'emp018@hrbank.local', '주임', '2022-03-13', 'ACTIVE'),
((SELECT id FROM departments WHERE name='영업팀'), NULL, '영업사원04', 'emp019@hrbank.local', '과장', '2023-01-20', 'RESIGNED'),
((SELECT id FROM departments WHERE name='영업팀'), NULL, '영업사원05', 'emp020@hrbank.local', '팀장', '2024-08-02', 'ACTIVE'),

-- 5) 기획팀 (5)
((SELECT id FROM departments WHERE name='기획팀'), NULL, '기획자01', 'emp021@hrbank.local', '기획 매니저', '2020-02-02', 'ACTIVE'),
((SELECT id FROM departments WHERE name='기획팀'), NULL, '기획자02', 'emp022@hrbank.local', 'PM', '2021-09-09', 'ACTIVE'),
((SELECT id FROM departments WHERE name='기획팀'), NULL, '기획자03', 'emp023@hrbank.local', '전략 기획', '2022-12-17', 'ACTIVE'),
((SELECT id FROM departments WHERE name='기획팀'), NULL, '기획자04', 'emp024@hrbank.local', '사업 기획', '2023-05-05', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='기획팀'), NULL, '기획자05', 'emp025@hrbank.local', '팀장', '2024-03-03', 'ACTIVE'),

-- 6) 재무팀 (5)
((SELECT id FROM departments WHERE name='재무팀'), NULL, '재무담당01', 'emp026@hrbank.local', '재무 담당', '2019-09-01', 'ACTIVE'),
((SELECT id FROM departments WHERE name='재무팀'), NULL, '재무담당02', 'emp027@hrbank.local', '회계 담당', '2020-12-12', 'ACTIVE'),
((SELECT id FROM departments WHERE name='재무팀'), NULL, '재무담당03', 'emp028@hrbank.local', '예산 분석가', '2021-04-18', 'ACTIVE'),
((SELECT id FROM departments WHERE name='재무팀'), NULL, '재무담당04', 'emp029@hrbank.local', '세무 담당', '2023-02-22', 'RESIGNED'),
((SELECT id FROM departments WHERE name='재무팀'), NULL, '재무담당05', 'emp030@hrbank.local', '팀장', '2024-10-01', 'ACTIVE'),

-- 7) 총무팀 (5)
((SELECT id FROM departments WHERE name='총무팀'), NULL, '총무담당01', 'emp031@hrbank.local', '총무 담당', '2019-06-20', 'ACTIVE'),
((SELECT id FROM departments WHERE name='총무팀'), NULL, '총무담당02', 'emp032@hrbank.local', '자산 관리', '2021-01-15', 'ACTIVE'),
((SELECT id FROM departments WHERE name='총무팀'), NULL, '총무담당03', 'emp033@hrbank.local', '시설 관리', '2022-04-04', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='총무팀'), NULL, '총무담당04', 'emp034@hrbank.local', '법무 보조', '2023-08-08', 'ACTIVE'),
((SELECT id FROM departments WHERE name='총무팀'), NULL, '총무담당05', 'emp035@hrbank.local', '팀장', '2024-11-11', 'ACTIVE'),

-- 8) IT팀 (5)
((SELECT id FROM departments WHERE name='IT팀'), NULL, 'IT엔지니어01', 'emp036@hrbank.local', 'IT 엔지니어', '2020-10-10', 'ACTIVE'),
((SELECT id FROM departments WHERE name='IT팀'), NULL, 'IT엔지니어02', 'emp037@hrbank.local', '시스템 관리자', '2021-12-01', 'ACTIVE'),
((SELECT id FROM departments WHERE name='IT팀'), NULL, 'IT엔지니어03', 'emp038@hrbank.local', '보안 엔지니어', '2022-06-21', 'ACTIVE'),
((SELECT id FROM departments WHERE name='IT팀'), NULL, 'IT엔지니어04', 'emp039@hrbank.local', '네트워크 엔지니어', '2023-03-03', 'RESIGNED'),
((SELECT id FROM departments WHERE name='IT팀'), NULL, 'IT엔지니어05', 'emp040@hrbank.local', '팀장', '2024-02-02', 'ACTIVE'),

-- 9) 품질관리팀 (5)
((SELECT id FROM departments WHERE name='품질관리팀'), NULL, 'QA엔지니어01', 'emp041@hrbank.local', 'QA 엔지니어', '2020-05-05', 'ACTIVE'),
((SELECT id FROM departments WHERE name='품질관리팀'), NULL, 'QA엔지니어02', 'emp042@hrbank.local', '테스트 리더', '2021-07-17', 'ACTIVE'),
((SELECT id FROM departments WHERE name='품질관리팀'), NULL, 'QA엔지니어03', 'emp043@hrbank.local', '품질 분석가', '2022-01-01', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='품질관리팀'), NULL, 'QA엔지니어04', 'emp044@hrbank.local', '검수 담당', '2023-09-09', 'ACTIVE'),
((SELECT id FROM departments WHERE name='품질관리팀'), NULL, 'QA엔지니어05', 'emp045@hrbank.local', '팀장', '2024-04-14', 'ACTIVE'),

-- 10) 고객서비스팀 (5)
((SELECT id FROM departments WHERE name='고객서비스팀'), NULL, 'CS담당01', 'emp046@hrbank.local', '고객 상담', '2019-12-24', 'ACTIVE'),
((SELECT id FROM departments WHERE name='고객서비스팀'), NULL, 'CS담당02', 'emp047@hrbank.local', 'A/S 담당', '2021-02-02', 'ACTIVE'),
((SELECT id FROM departments WHERE name='고객서비스팀'), NULL, 'CS담당03', 'emp048@hrbank.local', 'VOC 분석', '2022-10-10', 'ACTIVE'),
((SELECT id FROM departments WHERE name='고객서비스팀'), NULL, 'CS담당04', 'emp049@hrbank.local', '상담 매니저', '2023-06-06', 'RESIGNED'),
((SELECT id FROM departments WHERE name='고객서비스팀'), NULL, 'CS담당05', 'emp050@hrbank.local', '팀장', '2024-07-07', 'ACTIVE'),

-- 11) 구매팀 (5)
((SELECT id FROM departments WHERE name='구매팀'), NULL, '구매담당01', 'emp051@hrbank.local', '구매 담당', '2020-02-20', 'ACTIVE'),
((SELECT id FROM departments WHERE name='구매팀'), NULL, '구매담당02', 'emp052@hrbank.local', '공급업체 관리', '2021-03-15', 'ACTIVE'),
((SELECT id FROM departments WHERE name='구매팀'), NULL, '구매담당03', 'emp053@hrbank.local', '조달 담당', '2022-08-23', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='구매팀'), NULL, '구매담당04', 'emp054@hrbank.local', '원가 분석', '2023-11-11', 'ACTIVE'),
((SELECT id FROM departments WHERE name='구매팀'), NULL, '구매담당05', 'emp055@hrbank.local', '팀장', '2024-05-05', 'ACTIVE'),

-- 12) 생산팀 (5)
((SELECT id FROM departments WHERE name='생산팀'), NULL, '생산담당01', 'emp056@hrbank.local', '라인 매니저', '2019-07-15', 'ACTIVE'),
((SELECT id FROM departments WHERE name='생산팀'), NULL, '생산담당02', 'emp057@hrbank.local', '공정 관리자', '2020-01-28', 'ACTIVE'),
((SELECT id FROM departments WHERE name='생산팀'), NULL, '생산담당03', 'emp058@hrbank.local', '생산 기획', '2021-10-10', 'ACTIVE'),
((SELECT id FROM departments WHERE name='생산팀'), NULL, '생산담당04', 'emp059@hrbank.local', '품질 개선', '2023-07-22', 'RESIGNED'),
((SELECT id FROM departments WHERE name='생산팀'), NULL, '생산담당05', 'emp060@hrbank.local', '팀장', '2024-09-09', 'ACTIVE'),

-- 13) 디자인팀 (5)
((SELECT id FROM departments WHERE name='디자인팀'), NULL, '디자이너01', 'emp061@hrbank.local', 'UI 디자이너', '2020-11-05', 'ACTIVE'),
((SELECT id FROM departments WHERE name='디자인팀'), NULL, '디자이너02', 'emp062@hrbank.local', 'UX 디자이너', '2021-04-14', 'ACTIVE'),
((SELECT id FROM departments WHERE name='디자인팀'), NULL, '디자이너03', 'emp063@hrbank.local', '브랜드 디자이너', '2022-03-03', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='디자인팀'), NULL, '디자이너04', 'emp064@hrbank.local', '그래픽 디자이너', '2023-12-12', 'ACTIVE'),
((SELECT id FROM departments WHERE name='디자인팀'), NULL, '디자이너05', 'emp065@hrbank.local', '팀장', '2024-08-18', 'ACTIVE'),

-- 14) 법무팀 (5)
((SELECT id FROM departments WHERE name='법무팀'), NULL, '법무담당01', 'emp066@hrbank.local', '법무 담당', '2021-01-12', 'ACTIVE'),
((SELECT id FROM departments WHERE name='법무팀'), NULL, '법무담당02', 'emp067@hrbank.local', '계약서 검토', '2022-02-22', 'ACTIVE'),
((SELECT id FROM departments WHERE name='법무팀'), NULL, '법무담당03', 'emp068@hrbank.local', '규정 준수', '2020-10-30', 'ACTIVE'),
((SELECT id FROM departments WHERE name='법무팀'), NULL, '법무담당04', 'emp069@hrbank.local', '법률 자문', '2023-05-19', 'RESIGNED'),
((SELECT id FROM departments WHERE name='법무팀'), NULL, '법무담당05', 'emp070@hrbank.local', '팀장', '2024-12-01', 'ACTIVE'),

-- 15) 홍보팀 (5)
((SELECT id FROM departments WHERE name='홍보팀'), NULL, '홍보담당01', 'emp071@hrbank.local', '홍보 매니저', '2020-08-18', 'ACTIVE'),
((SELECT id FROM departments WHERE name='홍보팀'), NULL, '홍보담당02', 'emp072@hrbank.local', '미디어 대응', '2021-03-03', 'ACTIVE'),
((SELECT id FROM departments WHERE name='홍보팀'), NULL, '홍보담당03', 'emp073@hrbank.local', '이벤트 기획', '2022-06-16', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='홍보팀'), NULL, '홍보담당04', 'emp074@hrbank.local', 'PR 스페셜리스트', '2023-09-25', 'ACTIVE'),
((SELECT id FROM departments WHERE name='홍보팀'), NULL, '홍보담당05', 'emp075@hrbank.local', '팀장', '2024-10-10', 'ACTIVE'),

-- 16) 연구개발팀 (5)
((SELECT id FROM departments WHERE name='연구개발팀'), NULL, '연구원01', 'emp076@hrbank.local', '연구원', '2020-03-25', 'ACTIVE'),
((SELECT id FROM departments WHERE name='연구개발팀'), NULL, '연구원02', 'emp077@hrbank.local', '선임 연구원', '2021-07-07', 'ACTIVE'),
((SELECT id FROM departments WHERE name='연구개발팀'), NULL, '연구원03', 'emp078@hrbank.local', '주임 연구원', '2022-05-05', 'ACTIVE'),
((SELECT id FROM departments WHERE name='연구개발팀'), NULL, '연구원04', 'emp079@hrbank.local', '책임 연구원', '2023-11-30', 'RESIGNED'),
((SELECT id FROM departments WHERE name='연구개발팀'), NULL, '연구원05', 'emp080@hrbank.local', '팀장', '2024-01-15', 'ACTIVE'),

-- 17) 물류팀 (4)
((SELECT id FROM departments WHERE name='물류팀'), NULL, '물류담당01', 'emp081@hrbank.local', '물류 담당', '2019-10-08', 'ACTIVE'),
((SELECT id FROM departments WHERE name='물류팀'), NULL, '물류담당02', 'emp082@hrbank.local', '배송 관리', '2021-09-01', 'ACTIVE'),
((SELECT id FROM departments WHERE name='물류팀'), NULL, '물류담당03', 'emp083@hrbank.local', '창고 운영', '2022-12-12', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='물류팀'), NULL, '물류담당04', 'emp084@hrbank.local', '팀장', '2024-04-01', 'ACTIVE'),

-- 18) 보안팀 (4)
((SELECT id FROM departments WHERE name='보안팀'), NULL, '보안담당01', 'emp085@hrbank.local', '정보 보안', '2021-04-22', 'ACTIVE'),
((SELECT id FROM departments WHERE name='보안팀'), NULL, '보안담당02', 'emp086@hrbank.local', '보안 관제', '2022-03-08', 'ACTIVE'),
((SELECT id FROM departments WHERE name='보안팀'), NULL, '보안담당03', 'emp087@hrbank.local', '침해 대응', '2023-08-18', 'RESIGNED'),
((SELECT id FROM departments WHERE name='보안팀'), NULL, '보안담당04', 'emp088@hrbank.local', '팀장', '2024-06-06', 'ACTIVE'),

-- 19) 교육훈련팀 (4)
((SELECT id FROM departments WHERE name='교육훈련팀'), NULL, '교육담당01', 'emp089@hrbank.local', '교육 매니저', '2020-07-03', 'ACTIVE'),
((SELECT id FROM departments WHERE name='교육훈련팀'), NULL, '교육담당02', 'emp090@hrbank.local', '커리큘럼 개발', '2021-10-10', 'ACTIVE'),
((SELECT id FROM departments WHERE name='교육훈련팀'), NULL, '교육담당03', 'emp091@hrbank.local', '강사', '2022-09-09', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='교육훈련팀'), NULL, '교육담당04', 'emp092@hrbank.local', '팀장', '2024-02-20', 'ACTIVE'),

-- 20) 감사팀 (4)
((SELECT id FROM departments WHERE name='감사팀'), NULL, '감사담당01', 'emp093@hrbank.local', '감사 담당', '2021-03-16', 'ACTIVE'),
((SELECT id FROM departments WHERE name='감사팀'), NULL, '감사담당02', 'emp094@hrbank.local', '리스크 평가', '2022-01-21', 'ACTIVE'),
((SELECT id FROM departments WHERE name='감사팀'), NULL, '감사담당03', 'emp095@hrbank.local', '컴플라이언스', '2023-05-05', 'RESIGNED'),
((SELECT id FROM departments WHERE name='감사팀'), NULL, '감사담당04', 'emp096@hrbank.local', '팀장', '2024-03-03', 'ACTIVE'),

-- 21) 경영기획팀 (4)
((SELECT id FROM departments WHERE name='경영기획팀'), NULL, '경영기획담당01', 'emp097@hrbank.local', '경영기획 매니저', '2021-05-10', 'ACTIVE'),
((SELECT id FROM departments WHERE name='경영기획팀'), NULL, '경영기획담당02', 'emp098@hrbank.local', '성과 관리', '2022-07-07', 'ACTIVE'),
((SELECT id FROM departments WHERE name='경영기획팀'), NULL, '경영기획담당03', 'emp099@hrbank.local', '조직 운영', '2023-10-01', 'ON_LEAVE'),
((SELECT id FROM departments WHERE name='경영기획팀'), NULL, '경영기획담당04', 'emp100@hrbank.local', '팀장', '2024-05-15', 'ACTIVE');

-- === 직원 추이/분포 시드 데이터 ===
-- 목적: 일/주/월/분기 추이 시각화 + 부서/직함 상위 분포 강조

WITH
-- 분포(상위 강조용) 가중치: IT/개발 비중 ↑
dept_weighted(name, ord) AS (
    SELECT * FROM unnest(ARRAY[
        'IT팀','IT팀','IT팀',          -- IT팀 가중 3
        '개발팀','개발팀',             -- 개발팀 가중 2
        '영업팀','인사팀','재무팀',
        '총무팀','품질관리팀','홍보팀','연구개발팀'
        ]) WITH ORDINALITY
),
pos_weighted(position, ord) AS (
    SELECT * FROM unnest(ARRAY[
        '팀장','팀장','팀장',          -- 팀장 가중 3 (상위로 보이게)
        '사원','대리','과장','차장','연구원','주임'
        ]) WITH ORDINALITY
),

dept_map AS (
    SELECT d.id, d.name
    FROM "departments" d
    WHERE d.name IN (SELECT name FROM dept_weighted)
),

-- 최근 12개월: 각 달의 '말일' 기준, 분기 초/말에 살짝 피크
month_ends(gs, d) AS (
    SELECT gs,
           (date_trunc('month', CURRENT_DATE) - (gs || ' months')::interval
               + interval '1 month - 1 day')::date AS d
    FROM generate_series(0, 11) AS gs
),
month_rows AS (
    SELECT d,
           n
    FROM month_ends m
             -- 0,3,6,9(분기 경계)에는 8명, 그 외 4명 -> 월별/분기별 변화가 도드라짐
             JOIN LATERAL generate_series(1, CASE WHEN m.gs IN (0,3,6,9) THEN 8 ELSE 4 END) s(n) ON true
),

-- 최근 16주: ISO 주(월~일), '주말(일요일)' 기준으로 2~3명
week_ends(gs, d) AS (
    SELECT gs,
           (date_trunc('week', CURRENT_DATE) + interval '6 days'
               - (gs || ' weeks')::interval)::date AS d
    FROM generate_series(0, 15) AS gs
),
week_rows AS (
    SELECT d, n
    FROM week_ends w
             JOIN LATERAL generate_series(1, CASE WHEN (w.gs % 4) = 0 THEN 3 ELSE 2 END) s(n) ON true
),

-- 최근 21일: 화/수/목 2명, 그 외 1명 -> 일별 추이도 볼륨 있게
day_rows AS (
    SELECT g.d::date AS d, n
    FROM generate_series(CURRENT_DATE - interval '20 days', CURRENT_DATE, interval '1 day') g(d)
             JOIN LATERAL generate_series(1,
                                          CASE WHEN extract(isodow FROM g.d) IN (2,3,4) THEN 2 ELSE 1 END) s(n) ON true
),

-- 합치고 순번 부여(이 순번으로 부서/직함/상태를 라운드로빈 할당)
all_rows AS (
    SELECT d FROM month_rows
    UNION ALL
    SELECT d FROM week_rows
    UNION ALL
    SELECT d FROM day_rows
),
numbered AS (
    SELECT d,
           row_number() OVER (ORDER BY d, d) AS seq
    FROM all_rows
),

dept_count AS (SELECT count(*) AS c FROM dept_weighted),
pos_count  AS (SELECT count(*) AS c FROM pos_weighted)

INSERT INTO "employees" ("department_id","profile_image_id","name","email","position","hire_date","status")
SELECT
    -- 부서: 가중 배열을 순환하면서 매핑
    (
        SELECT dm.id
        FROM dept_weighted dw
                 JOIN dept_map dm ON dm.name = dw.name
                 JOIN dept_count dc ON true
        WHERE (((n.seq - 1) % dc.c) + 1) = dw.ord
        LIMIT 1
    ) AS department_id,
    NULL AS profile_image_id,
    format('시드직원%03s', n.seq) AS name,
    format('seed%04s@hrbank.local', n.seq + 1000) AS email,
    -- 직함: 가중 배열을 순환하면서 매핑(팀장 비중↑)
    (
        SELECT pw.position
        FROM pos_weighted pw
                 JOIN pos_count pc ON true
        WHERE (((n.seq - 1) % pc.c) + 1) = pw.ord
        LIMIT 1
    ) AS position,
    n.d AS hire_date,
    -- 상태: 20번째 RESIGNED, 15번째 ON_LEAVE, 나머지 ACTIVE
    CASE
        WHEN (n.seq % 20) = 0 THEN 'RESIGNED'
        WHEN (n.seq % 15) = 0 THEN 'ON_LEAVE'
        ELSE 'ACTIVE'
        END AS status
FROM numbered n
ON CONFLICT ("email") DO NOTHING;

-- change_logs
INSERT INTO change_logs (type, employee_number, memo,
     ip_address, at) VALUES
     ('CREATED', '00000001', '신규 직원 등록',
      '192.168.1.100', '2021-04-15 09:00:00+09'),
     ('CREATED', '00000002', '신규 직원 등록',
      '192.168.1.101', '2020-08-20 10:30:00+09'),
     ('CREATED', '00000003', '신규 직원 등록',
      '192.168.1.102', '2019-11-05 14:20:00+09'),
     -- 일부 직원들의 정보 수정 이력
     ('UPDATED', '00000001', '부서 이동',
      '192.168.1.105', '2022-01-15 14:30:00+09'),
     ('UPDATED', '00000003', '직급 승진',
      '192.168.1.106', '2022-03-01 11:00:00+09'),
     ('UPDATED', '00000002', '이메일 변경',
      '192.168.1.107', '2022-05-10 09:20:00+09');
