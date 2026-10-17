CREATE DATABASE IF NOT EXISTS cicloverde;
USE cicloverde;

-- Tabela que representa o comercio local (Padaria, Mercado, etc)
CREATE TABLE comercio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cnpj VARCHAR(20) UNIQUE NOT NULL
);

-- Tabela que registra os alimentos excedentes para doação
CREATE TABLE alimento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    comercio_id INT,
    nome_alimento VARCHAR(100) NOT NULL,
    quantidade_kg DOUBLE NOT NULL,
    data_validade DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'Disponível',
    FOREIGN KEY (comercio_id) REFERENCES comercio(id)
);

-- Inserindo um comércio teste para o nosso protótipo
INSERT INTO comercio (nome, cnpj) VALUES ('Padaria do Roberto', '12.345.678/0001-99');
