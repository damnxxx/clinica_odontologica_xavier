CREATE TABLE profissionais (
    id UUID PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    especialidade VARCHAR(120) NOT NULL,
    formacao VARCHAR(240) NOT NULL,
    experiencia_anos INTEGER NOT NULL CHECK (experiencia_anos >= 0),
    biografia VARCHAR(1000) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE servicos (
    id UUID PRIMARY KEY,
    nome VARCHAR(120) NOT NULL UNIQUE,
    descricao VARCHAR(500) NOT NULL,
    duracao_minutos INTEGER NOT NULL CHECK (duracao_minutos BETWEEN 15 AND 240),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE agendamentos (
    id UUID PRIMARY KEY,
    paciente_id UUID REFERENCES clientes(id) ON DELETE SET NULL,
    profissional_id UUID NOT NULL REFERENCES profissionais(id),
    servico_id UUID NOT NULL REFERENCES servicos(id),
    paciente_nome VARCHAR(120) NOT NULL,
    paciente_email VARCHAR(160) NOT NULL,
    paciente_telefone VARCHAR(20) NOT NULL,
    inicio TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('SOLICITADO','CONFIRMADO','CANCELADO','CONCLUIDO')),
    observacao VARCHAR(500),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_agendamentos_inicio ON agendamentos(inicio);
CREATE INDEX idx_agendamentos_profissional_inicio ON agendamentos(profissional_id, inicio);
CREATE INDEX idx_agendamentos_paciente ON agendamentos(paciente_id);

INSERT INTO profissionais (id,nome,especialidade,formacao,experiencia_anos,biografia) VALUES
('10000000-0000-0000-0000-000000000001','Dr. Lucas Almeida','Clínica Geral e Dentística','Graduado em Odontologia pela Universidade São Paulo do Vale.',8,'Atua na prevenção e no tratamento de problemas bucais, com foco em restaurações, estética dental e cuidados preventivos. Atendimento humanizado para a saúde e a beleza do sorriso.'),
('10000000-0000-0000-0000-000000000002','Dra. Adriana Moreira','Ortodontia','Graduada em Odontologia pela Faculdade Metropolitana de Saúde, com especialização em Ortodontia.',10,'Trabalha com alinhamento dentário, correção da mordida e tratamentos ortodônticos para melhorar a funcionalidade e a harmonia do sorriso.'),
('10000000-0000-0000-0000-000000000003','Dra. Lúcia Ferreira','Odontopediatria','Graduada em Odontologia pela Universidade do Interior Paulista, com especialização em Odontopediatria.',12,'Especializada no atendimento infantil, com cuidados preventivos e tratamentos para crianças, sempre com atenção e carinho.'),
('10000000-0000-0000-0000-000000000004','Dra. Linda Carvalho','Endodontia','Graduada em Odontologia pela Faculdade de Ciências da Saúde Paulista, com especialização em Endodontia.',7,'Atua em tratamentos de canal e procedimentos voltados à preservação dos dentes naturais.'),
('10000000-0000-0000-0000-000000000005','Dr. Carlos Ribeiro','Implantodontia','Graduado em Odontologia pela Universidade Paulista de Medicina, com especialização em Implantodontia.',9,'Especialista em reabilitação oral, implantes dentários e próteses para recuperar função, conforto e confiança.');

INSERT INTO servicos (id,nome,descricao,duracao_minutos) VALUES
('20000000-0000-0000-0000-000000000001','Avaliação odontológica','Consulta inicial para conhecer suas necessidades e orientar o plano de cuidado.',45),
('20000000-0000-0000-0000-000000000002','Prevenção e limpeza','Avaliação, orientações de higiene e limpeza profissional.',45),
('20000000-0000-0000-0000-000000000003','Restauração','Recuperação da forma e função dos dentes, conforme avaliação profissional.',60),
('20000000-0000-0000-0000-000000000004','Tratamento de canal','Tratamento endodôntico para cuidado da parte interna do dente.',90),
('20000000-0000-0000-0000-000000000005','Ortodontia','Avaliação e acompanhamento ortodôntico.',45),
('20000000-0000-0000-0000-000000000006','Odontopediatria','Atendimento odontológico infantil preventivo e clínico.',45),
('20000000-0000-0000-0000-000000000007','Implante dentário','Avaliação para reabilitação oral com implantes e próteses.',60);
