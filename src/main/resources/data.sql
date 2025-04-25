CREATE TABLE usuario (
    id SERIAL,
    name VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    role VARCHAR(200) NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id)
);

INSERT INTO usuario (name, email, password, role) 
VALUES
    ('João Pedro', 'jp2017@uol.com.br', '12345jaum', 'Cliente'),
    ('Amara Silva', 'amarasil@bol.com.br', 'amara82', 'Cliente'),
    ('Maria Pereira', 'mariape@terra.com.br', '145aektm', 'Cliente'),
    ('Taniro Rodrigues', 'tanirocr@gmail.com', '123456abc', 'Lojista'),
    ('Lorena Silva', 'lore_sil@yahoo.com.br', '12uhuuu@', 'Lojista');

CREATE TABLE produtos (
            id SERIAL PRIMARY KEY,
            nome VARCHAR(255) NOT NULL,
            descricao TEXT,
            preco DECIMAL(10, 2) NOT NULL,
            estoque INT NOT NULL
);

