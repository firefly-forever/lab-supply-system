-- ============================================================
-- 医疗实验室耗材管理系统 - 数据库初始化脚本
-- 项目：Major-Link Project
-- 作者：ZhengYi
-- 说明：建库建表 + 初始化测试数据，执行前请确认数据库版本 >= MySQL 8.0
-- ============================================================

CREATE DATABASE IF NOT EXISTS lab_supply_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE lab_supply_db;

-- ============================================================
-- 1. 用户表（sys_user）
--    存储系统用户，通过 role 字段区分角色：
--    STUDENT（学生）、TEACHER（教师）、ADMIN（管理员）
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密存储）',
    real_name   VARCHAR(50)  NOT NULL COMMENT '真实姓名',
    role        VARCHAR(20)  NOT NULL DEFAULT 'STUDENT' COMMENT '角色：STUDENT/TEACHER/ADMIN',
    department  VARCHAR(100) COMMENT '所属院系或科室',
    phone       VARCHAR(20) COMMENT '联系电话',
    email       VARCHAR(100) COMMENT '电子邮箱',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '系统用户表';

-- ============================================================
-- 2. 耗材分类表（supply_category）
--    支持两级分类，parent_id=0 表示顶级分类
-- ============================================================
CREATE TABLE IF NOT EXISTS supply_category
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    parent_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示顶级',
    name        VARCHAR(100) NOT NULL COMMENT '分类名称',
    code        VARCHAR(50) COMMENT '分类编码',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序（升序）',
    remark      VARCHAR(500) COMMENT '备注',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_parent_id (parent_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '耗材分类表';

-- ============================================================
-- 3. 耗材/试剂主表（supply_item）
--    存储耗材基本信息，quantity 为当前库存数量（实时维护）
-- ============================================================
CREATE TABLE IF NOT EXISTS supply_item
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '耗材ID',
    category_id     BIGINT         NOT NULL COMMENT '分类ID',
    name            VARCHAR(200)   NOT NULL COMMENT '耗材名称',
    code            VARCHAR(100)   NOT NULL UNIQUE COMMENT '耗材编号（唯一）',
    specification   VARCHAR(200) COMMENT '规格型号（如：500mL/瓶）',
    unit            VARCHAR(20)    NOT NULL DEFAULT '瓶' COMMENT '计量单位',
    manufacturer    VARCHAR(200) COMMENT '生产厂家',
    storage_condition VARCHAR(200) COMMENT '存储条件（如：2-8℃避光保存）',
    quantity        INT            NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    warning_quantity INT           NOT NULL DEFAULT 10 COMMENT '低库存预警阈值',
    is_hazardous    TINYINT        NOT NULL DEFAULT 0 COMMENT '是否危险品：1是 0否',
    status          TINYINT        NOT NULL DEFAULT 1 COMMENT '状态：1正常 0停用',
    remark          VARCHAR(500) COMMENT '备注',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id),
    INDEX idx_code (code),
    INDEX idx_quantity (quantity)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '耗材/试剂主表';

-- ============================================================
-- 4. 入库记录表（stock_in_record）
--    每次入库操作记录一条，支持批次管理和有效期追踪
-- ============================================================
CREATE TABLE IF NOT EXISTS stock_in_record
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '入库记录ID',
    supply_id       BIGINT       NOT NULL COMMENT '耗材ID',
    batch_no        VARCHAR(100) COMMENT '批次号（生产批号）',
    quantity        INT          NOT NULL COMMENT '本次入库数量',
    unit_price      DECIMAL(10, 2) COMMENT '单价（元）',
    total_price     DECIMAL(12, 2) COMMENT '总价（元）',
    production_date DATE COMMENT '生产日期',
    expiry_date     DATE COMMENT '有效期至',
    supplier        VARCHAR(200) COMMENT '供应商名称',
    operator_id     BIGINT       NOT NULL COMMENT '操作人ID（入库员/管理员）',
    remark          VARCHAR(500) COMMENT '备注',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    INDEX idx_supply_id (supply_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_create_time (create_time),
    INDEX idx_expiry_date (expiry_date)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '入库记录表';

-- ============================================================
-- 5. 领用申请表（supply_application）
--    核心审批流：学生申请 -> 教师审批 -> 出库
--    status 状态机：PENDING（待审批）-> APPROVED/REJECTED
--    审批通过后自动生成出库记录
-- ============================================================
CREATE TABLE IF NOT EXISTS supply_application
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
    supply_id       BIGINT       NOT NULL COMMENT '申请的耗材ID',
    quantity        INT          NOT NULL COMMENT '申请数量',
    purpose         VARCHAR(500) NOT NULL COMMENT '用途说明（实验名称/目的）',
    applicant_id    BIGINT       NOT NULL COMMENT '申请人ID（学生）',
    approver_id     BIGINT COMMENT '审批人ID（教师，审批后填入）',
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/APPROVED/REJECTED',
    apply_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    approve_time    DATETIME COMMENT '审批时间',
    approve_remark  VARCHAR(500) COMMENT '审批意见',
    reject_reason   VARCHAR(500) COMMENT '拒绝原因',
    INDEX idx_applicant_id (applicant_id),
    INDEX idx_approver_id (approver_id),
    INDEX idx_status (status),
    INDEX idx_supply_id (supply_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '领用申请表（审批流核心表）';

-- ============================================================
-- 6. 出库记录表（stock_out_record）
--    审批通过后自动生成，或管理员直接出库
--    out_type：APPLY（申请领用）/ DIRECT（直接出库）/ SCRAP（报废）
-- ============================================================
CREATE TABLE IF NOT EXISTS stock_out_record
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '出库记录ID',
    supply_id       BIGINT       NOT NULL COMMENT '耗材ID',
    quantity        INT          NOT NULL COMMENT '出库数量',
    out_type        VARCHAR(20)  NOT NULL DEFAULT 'APPLY' COMMENT '出库类型：APPLY/DIRECT/SCRAP',
    application_id  BIGINT COMMENT '关联的申请ID（out_type=APPLY时必填）',
    receiver_id     BIGINT       NOT NULL COMMENT '领用人ID',
    operator_id     BIGINT       NOT NULL COMMENT '操作人ID（审批通过执行出库的人）',
    remark          VARCHAR(500) COMMENT '备注',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '出库时间',
    INDEX idx_supply_id (supply_id),
    INDEX idx_application_id (application_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_create_time (create_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '出库记录表';

-- ============================================================
-- 初始化数据：用户（密码均为 123456，BCrypt加密）
-- BCrypt hash of "123456" = $2a$10$...（以下为预计算值）
-- ============================================================
INSERT INTO sys_user (username, password, real_name, role, department, phone) VALUES
('admin',   '$2a$10$7JB720yubVSOftvVPn5p5OI9WCsbeM3ODYnMDEeP17X7VqkPNBVYO', '系统管理员', 'ADMIN',   '实验室管理中心', '13800000001'),
('teacher1','$2a$10$7JB720yubVSOftvVPn5p5OI9WCsbeM3OYnMDEeP17X7VqkPNBVYO', '李明教授',   'TEACHER', '基础医学实验室',  '13800000002'),
('teacher2','$2a$10$7JB720yubVSOftvVPn5p5OI9WCsbeM3OYnMDEeP17X7VqkPNBVYO', '王芳老师',   'TEACHER', '临床检验实验室',  '13800000003'),
('student1','$2a$10$7JB720yubVSOftvVPn5p5OI9WCsbeM3OYnMDEeP17X7VqkPNBVYO', '张三',       'STUDENT', '2022级临床医学',  '13800000004'),
('student2','$2a$10$7JB720yubVSOftvVPn5p5OI9WCsbeM3OYnMDEeP17X7VqkPNBVYO', '李四',       'STUDENT', '2022级医学检验',  '13800000005'),
('student3','$2a$10$7JB720yubVSOftvVPn5p5OI9WCsbeM3OYnMDEeP17X7VqkPNBVYO', '王五',       'STUDENT', '2023级生物医学',  '13800000006');

-- ⚠️ 注意：由于BCrypt的随机salt特性，以上hash值仅供演示。
-- 实际部署请使用 PasswordEncoderUtil 类生成真实hash，或在应用启动时通过 DataInitializer 初始化。

-- ============================================================
-- 初始化数据：耗材分类
-- ============================================================
INSERT INTO supply_category (parent_id, name, code, sort) VALUES
(0, '化学试剂',     'CHEM',  1),
(0, '生物试剂',     'BIO',   2),
(0, '耗材器具',     'CONS',  3),
(0, '防护用品',     'PPE',   4),
(1, '有机溶剂',     'CHEM-ORG', 11),
(1, '无机试剂',     'CHEM-INO', 12),
(1, '酸碱试剂',     'CHEM-AB',  13),
(2, '细胞培养试剂', 'BIO-CELL', 21),
(2, '分子生物学试剂','BIO-MOL', 22),
(2, '免疫检测试剂', 'BIO-IMM',  23),
(3, '离心管',       'CONS-TUB', 31),
(3, '移液管/吸头',  'CONS-TIP', 32),
(3, '培养皿',       'CONS-DIS', 33);

-- ============================================================
-- 初始化数据：耗材/试剂（涵盖医学实验室常用物品）
-- ============================================================
INSERT INTO supply_item (category_id, name, code, specification, unit, manufacturer, storage_condition, quantity, warning_quantity, is_hazardous) VALUES
-- 化学试剂
(5,  '无水乙醇',           'CHEM-001', '500mL/瓶，分析纯AR', '瓶', '国药集团化学试剂有限公司', '密封，阴凉干燥处保存，远离火源', 45, 10, 1),
(5,  '甲醇',               'CHEM-002', '500mL/瓶，分析纯AR', '瓶', '国药集团化学试剂有限公司', '密封，阴凉干燥处保存，远离火源', 8,  10, 1),
(6,  '氯化钠（NaCl）',     'CHEM-003', '500g/瓶，分析纯AR', '瓶', '上海阿拉丁生化科技股份有限公司', '密封，干燥处保存', 30, 5,  0),
(7,  '盐酸（HCl）',        'CHEM-004', '500mL/瓶，36-38%', '瓶', '国药集团化学试剂有限公司', '通风橱，防腐蚀专柜保存', 12, 5,  1),
(7,  '氢氧化钠（NaOH）',   'CHEM-005', '500g/瓶，分析纯AR', '瓶', '国药集团化学试剂有限公司', '密封，干燥处保存，防潮', 18, 5,  1),
-- 生物试剂
(8,  'DMEM细胞培养基',     'BIO-001',  '500mL/瓶',          '瓶', 'Gibco（赛默飞）',          '2-8℃避光保存',          6,  10, 0),
(8,  '胎牛血清（FBS）',    'BIO-002',  '500mL/瓶，澳洲来源', '瓶', 'Gibco（赛默飞）',          '-20℃冷冻保存',           3,  5,  0),
(9,  'Tris-HCl缓冲液',    'BIO-003',  '1mol/L，pH8.0，500mL','瓶','北京索莱宝科技有限公司',   '4℃保存',                 15, 5,  0),
(9,  'DNA聚合酶（Taq）',   'BIO-004',  '250U/管',            '管', 'TaKaRa宝生物',             '-20℃保存，避免反复冻融',  22, 10, 0),
(10, '兔抗人IgG-HRP',      'BIO-005',  '1mL/瓶',             '瓶', 'Abcam',                    '2-8℃保存，有效期12个月',  9,  10, 0),
-- 耗材器具
(11, '离心管（1.5mL）',    'CONS-001', '1.5mL，PP材质，100支/包','包','爱思进生物技术（杭州）',  '室温保存，无菌独立包装',  25, 10, 0),
(11, '离心管（50mL）',     'CONS-002', '50mL，PP材质，25支/包', '包','爱思进生物技术（杭州）',  '室温保存',                18, 5,  0),
(12, '移液枪吸头（1000μL）','CONS-003','1000μL，96支/包',    '包', '大龙兴创实验仪器（北京）', '室温，无菌包装',           7,  10, 0),
(13, '培养皿（60mm）',     'CONS-004', 'φ60mm，聚苯乙烯，20个/包','包','NEST（耐思生物）',       '室温，无菌包装',           12, 5,  0);

-- ============================================================
-- 初始化数据：入库记录（模拟历史数据）
-- ============================================================
INSERT INTO stock_in_record (supply_id, batch_no, quantity, unit_price, total_price, production_date, expiry_date, supplier, operator_id) VALUES
(1,  'CHEM20240101', 50, 38.50,  1925.00, '2024-01-01', '2026-12-31', '上海化学试剂采购供应站', 1),
(2,  'CHEM20240201', 10, 45.00,  450.00,  '2024-02-01', '2026-12-31', '上海化学试剂采购供应站', 1),
(6,  'BIO20240301',  10, 280.00, 2800.00, '2024-03-01', '2025-06-30', '赛默飞世尔科技授权代理', 1),
(7,  'BIO20240301',  5,  850.00, 4250.00, '2024-03-15', '2026-03-14', '赛默飞世尔科技授权代理', 1),
(10, 'BIO20240401',  10, 1200.00,12000.00,'2024-04-01', '2025-04-30', '爱博泰克（北京）生物技术', 1);

-- ============================================================
-- 初始化数据：模拟一些申请记录
-- ============================================================
INSERT INTO supply_application (supply_id, quantity, purpose, applicant_id, approver_id, status, apply_time, approve_time, approve_remark) VALUES
(1,  5,  '细胞固定实验，制备70%乙醇工作液', 4, 2, 'APPROVED', '2024-05-10 09:00:00', '2024-05-10 10:30:00', '已审批，请规范操作'),
(9,  2,  'PCR扩增实验，检测基因表达水平',   5, 2, 'APPROVED', '2024-05-12 14:00:00', '2024-05-12 15:00:00', '已审批'),
(6,  1,  'HeLa细胞培养，传代实验',           4, NULL, 'PENDING', '2024-05-15 08:30:00', NULL, NULL),
(10, 2,  'ELISA检测，免疫组化实验',          5, NULL, 'PENDING', '2024-05-15 09:00:00', NULL, NULL),
(2,  3,  'HPLC流动相配制',                   6, 3, 'REJECTED', '2024-05-08 10:00:00', '2024-05-08 14:00:00', NULL);

-- 更新被拒绝申请的拒绝原因
UPDATE supply_application SET reject_reason = '甲醇为管制危险品，请联系实验室主任说明具体用途后重新申请' WHERE id = 5;

-- ============================================================
-- 初始化数据：出库记录（已审批通过的申请对应的出库）
-- ============================================================
INSERT INTO stock_out_record (supply_id, quantity, out_type, application_id, receiver_id, operator_id) VALUES
(1, 5, 'APPLY', 1, 4, 2),
(9, 2, 'APPLY', 2, 5, 2);

-- 更新库存（模拟已出库后的实际库存状态，这里手动对齐）
UPDATE supply_item SET quantity = 45 WHERE id = 1;  -- 50入库-5出库=45
UPDATE supply_item SET quantity = 20 WHERE id = 9;  -- 22入库-2出库=20

SELECT '数据库初始化完成！' AS message;
SELECT '默认密码均为：123456' AS tip;
