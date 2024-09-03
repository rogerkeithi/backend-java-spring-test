--APENAS PARA TESTES
INSERT INTO _USER (USERNAME, NIVEL, PASSWORD) VALUES ('admin', 'ADMIN', '$2a$10$7So33nnkVuhJKMJb4UFxOuFd9vvOllJkJz1sr2u1g0ccf8VuMo1Kq'),('testuser', 'USER', '$2a$10$YrQwq.hoRYp8XHNFWM/vP.IcYg2OMCcPpqomrOkdCUstz95o6Ow86');
INSERT INTO TASK (TITLE, DESCRIPTION, DUE_DATE, STATUS, USER_ID, CREATED_AT) VALUES ('Tarefa 1', 'Esta é a primeira tarefa', '2024-09-06 18:45:32.123456', 'PENDENTE', '1', CURRENT_TIMESTAMP())
,('Tarefa 2', 'Esta é a segunda tarefa', '2024-09-06 16:45:32.123456', 'EM_ANDAMENTO', '1', CURRENT_TIMESTAMP())
,('Tarefa 3', 'Esta é a terceira tarefa', '2024-09-06 17:45:32.123456', 'CONCLUIDA', '1', CURRENT_TIMESTAMP())
,('Tarefa 4', 'Esta é a quarta tarefa', '2024-09-06 16:45:32.123456', 'CONCLUIDA', '2', CURRENT_TIMESTAMP());
--admin
--senha: 123

--testuser
--senha:456