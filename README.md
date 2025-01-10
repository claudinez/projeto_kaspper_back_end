# Sistema de Serviços

Este é o repositório do projeto **Sistema de Serviços**, um sistema para controle de serviços e orçamentos.

## Tecnologias usadas

- Java
- Spring Boot
- MySQL
- Angular

## Como rodar

1. Clone o repositório:
   ```bash
   https://github.com/claudinez/projeto_kaspper_back_end.git

2. http://localhost:8080/clientes

-- Criação do banco de dados
CREATE DATABASE IF NOT EXISTS dborcamento;

-- Seleciona o banco de dados para uso
USE dborcamento;

-- Criação da tabela clientes
CREATE TABLE clientes (
    id BIGINT(11) AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    sobrenome VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    telefone VARCHAR(15),
    cargo VARCHAR(30),
    status varchar(50),
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela servicos
CREATE TABLE servicos (
    id BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    cliente_id BIGINT(20) NOT NULL,
    linguagem VARCHAR(50),
    status VARCHAR(20),
    data_solicitacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE ON UPDATE CASCADE
);


