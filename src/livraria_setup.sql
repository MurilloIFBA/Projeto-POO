CREATE DATABASE IF NOT EXISTS livraria
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE livraria;

CREATE TABLE IF NOT EXISTS livros (
    id       INT           NOT NULL AUTO_INCREMENT,
    titulo   VARCHAR(150)  NOT NULL,
    autor    VARCHAR(100)  NOT NULL,
    preco    DECIMAL(10,2) NOT NULL,
    estoque  INT           NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS clientes (
    id     INT          NOT NULL AUTO_INCREMENT,
    nome   VARCHAR(100) NOT NULL,
    email  VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS vendas (
    id          INT      NOT NULL AUTO_INCREMENT,
    livro_id    INT      NOT NULL,
    cliente_id  INT      NOT NULL,
    quantidade  INT      NOT NULL,
    data_venda  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_venda_livro
        FOREIGN KEY (livro_id)   REFERENCES livros(id)   ON DELETE RESTRICT,
    CONSTRAINT fk_venda_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT
);

INSERT INTO livros (titulo, autor, preco, estoque) VALUES
    ('Clean Code',      'Robert C. Martin', 89.90, 10),
    ('O Hobbit',        'J.R.R. Tolkien',   49.90,  5),
    ('Design Patterns', 'Gang of Four',    120.00,  8);

INSERT INTO clientes (nome, email) VALUES
    ('Ana Souza',  'ana@email.com'),
    ('Bruno Lima', 'bruno@email.com');

INSERT INTO vendas (livro_id, cliente_id, quantidade) VALUES
    (1, 1, 2),
    (2, 2, 1);

CREATE OR REPLACE VIEW vw_vendas AS
SELECT
    v.id                       AS venda_id,
    c.nome                     AS cliente,
    l.titulo                   AS livro,
    v.quantidade,
    l.preco                    AS preco_unitario,
    (v.quantidade * l.preco)   AS total,
    v.data_venda
FROM vendas v
JOIN livros   l ON l.id = v.livro_id
JOIN clientes c ON c.id = v.cliente_id;

DELIMITER $$

CREATE PROCEDURE sp_criar_livro(
    IN p_titulo  VARCHAR(150),
    IN p_autor   VARCHAR(100),
    IN p_preco   DECIMAL(10,2),
    IN p_estoque INT
)
BEGIN
    INSERT INTO livros (titulo, autor, preco, estoque)
    VALUES (p_titulo, p_autor, p_preco, p_estoque);
END$$

CREATE PROCEDURE sp_atualizar_livro(
    IN p_id      INT,
    IN p_titulo  VARCHAR(150),
    IN p_autor   VARCHAR(100),
    IN p_preco   DECIMAL(10,2),
    IN p_estoque INT
)
BEGIN
    UPDATE livros
    SET titulo = p_titulo, autor = p_autor,
        preco  = p_preco,  estoque = p_estoque
    WHERE id = p_id;
END$$

CREATE PROCEDURE sp_remover_livro(IN p_id INT)
BEGIN
    DELETE FROM livros WHERE id = p_id;
END$$

CREATE PROCEDURE sp_criar_cliente(
    IN p_nome  VARCHAR(100),
    IN p_email VARCHAR(100)
)
BEGIN
    INSERT INTO clientes (nome, email) VALUES (p_nome, p_email);
END$$

CREATE PROCEDURE sp_atualizar_cliente(
    IN p_id    INT,
    IN p_nome  VARCHAR(100),
    IN p_email VARCHAR(100)
)
BEGIN
    UPDATE clientes SET nome = p_nome, email = p_email WHERE id = p_id;
END$$

CREATE PROCEDURE sp_remover_cliente(IN p_id INT)
BEGIN
    DELETE FROM clientes WHERE id = p_id;
END$$

CREATE PROCEDURE sp_registrar_venda(
    IN p_livro_id   INT,
    IN p_cliente_id INT,
    IN p_quantidade INT
)
BEGIN
    DECLARE v_estoque INT;
    SELECT estoque INTO v_estoque FROM livros WHERE id = p_livro_id;
    IF v_estoque < p_quantidade THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Estoque insuficiente';
    ELSE
        INSERT INTO vendas (livro_id, cliente_id, quantidade)
        VALUES (p_livro_id, p_cliente_id, p_quantidade);
        UPDATE livros SET estoque = estoque - p_quantidade
        WHERE id = p_livro_id;
    END IF;
END$$

DELIMITER ;
