CREATE DATABASE vendaingressos;

USE vendaingressos;

CREATE TABLE usuarios(
	id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    usuario VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone CHAR(20) NOT NULL,
    dt_nascimento DATE NOT NULL
);

CREATE TABLE eventos(
	id_evento INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    dt_evento DATE NOT NULL,
    local_evento VARCHAR(255),
    descricao VARCHAR(500),
    sts ENUM('A', 'I') DEFAULT 'A'
);

CREATE TABLE ingressos(
	id_ingresso INT PRIMARY KEY AUTO_INCREMENT,
    id_evento INT,
    valor FLOAT NOT NULL,
    quantidade_disponivel INT NOT NULL,
    FOREIGN KEY(id_evento) REFERENCES eventos(id_evento)
);

CREATE TABLE compras(
	id_compra INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,
    id_evento INT,
    id_ingresso INT,
    data_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    quantidade INT NOT NULL,
    valor_total FLOAT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_evento) REFERENCES eventos(id_evento),
    FOREIGN KEY (id_ingresso) REFERENCES ingressos(id_ingresso)
);

CREATE TABLE log_compras (
    id_log INT AUTO_INCREMENT PRIMARY KEY,
    id_compra INT NOT NULL,
    id_usuario INT NOT NULL,
    data_log TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    descricao VARCHAR(500)
);

-- sempre que a quantidade de ingressos de um evento chegar a 0, ele fica inativo
DELIMITER $$

CREATE TRIGGER atualizar_status_evento
AFTER UPDATE ON ingressos
FOR EACH ROW
BEGIN
    IF NEW.quantidade_disponivel = 0 THEN
        UPDATE eventos
        SET sts = 'I'
        WHERE id_evento = NEW.id_evento;
    END IF;
END$$

DELIMITER ;

-- evita o cadastro de eventos com data passada
DELIMITER $$

CREATE TRIGGER antes_de_inserir_evento
BEFORE INSERT ON eventos
FOR EACH ROW
BEGIN
    IF NEW.dt_evento < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A data do evento não pode ser anterior à data atual.';
    END IF;
END$$

DELIMITER ;

-- registra um log sempre que uma compra for feita
DELIMITER $$

CREATE TRIGGER log_apos_comprar
AFTER INSERT ON compras
FOR EACH ROW
BEGIN
    INSERT INTO log_compras (id_compra, id_usuario, descricao)
    VALUES (NEW.id_compra, NEW.id_usuario, CONCAT('Compra registrada para o evento ', NEW.id_evento, ' com ', NEW.quantidade, ' ingressos.'));
END$$

DELIMITER ;

-- impede a duplicação de compras para o mesmo evento
DELIMITER $$

CREATE TRIGGER antes_de_inserir_compra
BEFORE INSERT ON compras
FOR EACH ROW
BEGIN
    DECLARE qtd_compras INT;

    SELECT COUNT(*) INTO qtd_compras
    FROM compras
    WHERE id_usuario = NEW.id_usuario AND id_evento = NEW.id_evento;

    IF qtd_compras > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'O usuário já realizou uma compra para este evento.';
    END IF;
END$$

DELIMITER ;

-- valida o preço do ingresso
DELIMITER $$

CREATE TRIGGER antes_de_inserir_ingresso
BEFORE INSERT ON ingressos
FOR EACH ROW
BEGIN
    IF NEW.valor <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'O valor do ingresso deve ser maior que zero.';
    END IF;
END$$

DELIMITER ;

-- lista todos os eventos ativos
DELIMITER $$

CREATE PROCEDURE listar_eventos_ativos(
    IN p_nome VARCHAR(255)
)
BEGIN
    SELECT id_evento, nome, dt_evento, local_evento, descricao
    FROM eventos
    WHERE sts = 'A'
      AND nome LIKE CONCAT('%', p_nome, '%');
END$$

DELIMITER ;

-- mostra os detalhes de um evento
DELIMITER $$

CREATE PROCEDURE detalhes_evento(
    IN p_id_evento INT
)
BEGIN
    SELECT e.nome AS evento,
           e.dt_evento AS data,
           e.local_evento AS local,
           e.descricao AS descricao,
           i.valor AS valor_ingresso,
           i.quantidade_disponivel AS ingressos_disponiveis
    FROM eventos e
    LEFT JOIN ingressos i ON e.id_evento = i.id_evento
    WHERE e.id_evento = p_id_evento;
END$$

DELIMITER ;

-- mostra o historico de compras de um usuario
DELIMITER $$

CREATE PROCEDURE historico_compras_usuario(
    IN p_id_usuario INT
)
BEGIN
    SELECT c.id_compra,
           e.nome AS evento,
           c.quantidade,
           c.valor_total,
           c.data_compra
    FROM compras c
    INNER JOIN eventos e ON c.id_evento = e.id_evento
    WHERE c.id_usuario = p_id_usuario
    ORDER BY c.data_compra DESC;
END$$

DELIMITER ;

-- calcula o valor total de ingressos vendidos para um evento
DELIMITER $$

CREATE FUNCTION total_vendas_evento(p_id_evento INT)
RETURNS FLOAT
DETERMINISTIC
BEGIN
    DECLARE v_total FLOAT;

    SELECT IFNULL(SUM(valor_total), 0) INTO v_total
    FROM compras
    WHERE id_evento = p_id_evento;

    RETURN v_total;
END$$

DELIMITER ;

-- calcula a quantidade de ingressos restantes para um evento
DELIMITER $$

CREATE FUNCTION ingressos_restantes(p_id_evento INT)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE v_restante INT;

    SELECT IFNULL(SUM(quantidade_disponivel), 0) INTO v_restante
    FROM ingressos
    WHERE id_evento = p_id_evento;

    RETURN v_restante;
END$$

DELIMITER ;

-- verifica se um usuário já comprou ingressos para um evento
DELIMITER $$

CREATE FUNCTION usuario_comprou(p_id_usuario INT, p_id_evento INT)
RETURNS BOOLEAN
DETERMINISTIC
BEGIN
    DECLARE v_existe BOOLEAN;

    SELECT COUNT(*) > 0 INTO v_existe
    FROM compras
    WHERE id_usuario = p_id_usuario AND id_evento = p_id_evento;

    RETURN v_existe;
END$$

DELIMITER ;

-- usuarios e dados basicos
CREATE OR REPLACE VIEW usuarios_basicos AS
SELECT id_usuario, nome, email, telefone
FROM usuarios;

-- eventos ativos
CREATE OR REPLACE VIEW eventos_ativos AS
SELECT id_evento, nome AS nome_evento, dt_evento, local_evento
FROM eventos
WHERE sts = 'A';

-- ingressos disponiveis por evento
CREATE OR REPLACE VIEW ingressos_disponiveis AS
SELECT e.id_evento, e.nome AS nome_evento, i.valor AS valor_ingresso, i.quantidade_disponivel
FROM ingressos i
JOIN eventos e ON i.id_evento = e.id_evento
WHERE i.quantidade_disponivel > 0;

-- compras realizadas por usuarios
CREATE OR REPLACE VIEW compras_por_usuario AS
SELECT c.id_usuario, u.nome AS nome_usuario, e.nome AS nome_evento, c.quantidade, c.valor_total, c.data_compra
FROM compras c
JOIN usuarios u ON c.id_usuario = u.id_usuario
JOIN eventos e ON c.id_evento = e.id_evento;

-- relatorio de vendas por evento
CREATE OR REPLACE VIEW vendas_por_evento AS
SELECT e.id_evento, e.nome AS nome_evento, 
       SUM(c.quantidade) AS total_ingressos_vendidos, 
       SUM(c.valor_total) AS total_arrecadado
FROM compras c
JOIN eventos e ON c.id_evento = e.id_evento
GROUP BY e.id_evento, e.nome
HAVING total_arrecadado > 500
ORDER BY total_arrecadado DESC;

-- historico de compras por vento
CREATE OR REPLACE VIEW historico_compras_evento AS
SELECT e.id_evento, e.nome AS nome_evento, 
       u.nome AS nome_usuario, 
       c.quantidade, c.valor_total, c.data_compra
FROM compras c
JOIN eventos e ON c.id_evento = e.id_evento
JOIN usuarios u ON c.id_usuario = u.id_usuario
ORDER BY c.data_compra DESC;

-- estoque de ingressos por evento
CREATE OR REPLACE VIEW estoque_ingressos AS
SELECT e.id_evento, e.nome AS nome_evento, 
       i.quantidade_disponivel AS ingressos_disponiveis,
       (SELECT IFNULL(SUM(c.quantidade), 0) 
        FROM compras c 
        WHERE c.id_evento = e.id_evento) AS ingressos_vendidos
FROM eventos e
JOIN ingressos i ON e.id_evento = i.id_evento
HAVING ingressos_disponiveis < 50
ORDER BY ingressos_disponiveis ASC;

-- usuarios aniversariantes do mes
CREATE OR REPLACE VIEW aniversariantes_mes AS
SELECT id_usuario, nome, email, telefone, dt_nascimento
FROM usuarios
WHERE MONTH(dt_nascimento) = MONTH(CURDATE())
ORDER BY dt_nascimento ASC;

-- detalhes completos de compras
CREATE OR REPLACE VIEW detalhes_compras AS
SELECT c.id_compra, u.nome AS nome_usuario, u.email AS email_usuario, 
       e.nome AS nome_evento, e.dt_evento AS data_evento, 
       i.valor AS valor_ingresso, c.quantidade, c.valor_total, c.data_compra
FROM compras c
JOIN usuarios u ON c.id_usuario = u.id_usuario
JOIN eventos e ON c.id_evento = e.id_evento
JOIN ingressos i ON c.id_ingresso = i.id_ingresso
HAVING valor_total > 100
ORDER BY valor_total DESC;

/* 
Comandos usados no Back-End:

INSERT INTO compras(id_usuario, id_evento, id_ingresso, quantidade, valor_total)
VALUES (?, ?, ?, ?, ?);

UPDATE ingressos
SET quantidade_disponivel = quantidade_disponivel - ? WHERE id_ingresso = ?;

SELECT eventos.nome, eventos.sts, eventos.dt_evento, eventos.local_evento, eventos.descricao, 
ingressos.id_ingresso, ingressos.valor, ingressos.quantidade_disponivel 
FROM eventos 
JOIN ingressos ON eventos.id_evento = ingressos.id_evento 
WHERE eventos.id_evento = ?;

INSERT INTO usuarios(nome, usuario, senha, email, telefone, dt_nascimento)
VALUES (?, ?, ?, ?, ?, ?);

SELECT c.id_compra, e.nome, c.quantidade, c.valor_total, e.descricao, e.local_evento, e.dt_evento 
FROM compras c 
JOIN eventos e ON c.id_evento = e.id_evento 
JOIN ingressos i ON c.id_ingresso = i.id_ingresso 
WHERE c.id_usuario = ?;

UPDATE eventos 
SET nome = ?, dt_evento = ?, local_evento = ?, descricao = ?, sts = ?
WHERE id_evento = ?;

UPDATE ingressos
SET valor = ?, quantidade_disponivel = ?
WHERE id_evento = ?;

SELECT eventos.id_evento, eventos.nome, eventos.dt_evento, eventos.local_evento, eventos.descricao, 
ingressos.valor, ingressos.quantidade_disponivel 
FROM eventos 
JOIN ingressos ON eventos.id_evento = ingressos.id_evento;

UPDATE usuarios
SET nome = ?, usuario = ?, senha = ?, email = ?, telefone = ?, dt_nascimento = ? 
WHERE id_usuario = ?;

DELETE FROM compras
WHERE id_usuario = ?;

DELETE FROM usuarios 
WHERE id_usuario = ?;

*/