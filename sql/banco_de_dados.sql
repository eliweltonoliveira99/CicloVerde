CREATE DATABASE IF NOT EXISTS cicloverde;
USE cicloverde;
CREATE TABLE IF NOT EXISTS comercio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cnpj VARCHAR(20) UNIQUE,
    cidade VARCHAR(100) NOT NULL DEFAULT '',
    responsavel VARCHAR(100) NOT NULL DEFAULT '',
    endereco VARCHAR(255) NOT NULL DEFAULT '',
    descricao VARCHAR(500) NOT NULL DEFAULT '',
    demonstracao BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE TABLE IF NOT EXISTS alimento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    comercio_id INT,
    nome_alimento VARCHAR(100) NOT NULL,
    quantidade_kg DOUBLE NOT NULL,
    data_validade DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'Disponível',
    FOREIGN KEY (comercio_id) REFERENCES comercio(id)
);
