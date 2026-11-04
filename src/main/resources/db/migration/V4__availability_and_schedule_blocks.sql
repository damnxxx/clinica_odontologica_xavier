CREATE TABLE disponibilidade_profissional (
    id UUID PRIMARY KEY,
    profissional_id UUID NOT NULL REFERENCES profissionais(id) ON DELETE CASCADE,
    dia_semana SMALLINT NOT NULL CHECK (dia_semana BETWEEN 1 AND 7),
    inicio TIME NOT NULL,
    fim TIME NOT NULL,
    intervalo_minutos INTEGER NOT NULL CHECK (intervalo_minutos BETWEEN 5 AND 240),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (fim > inicio)
);

CREATE INDEX idx_disponibilidade_profissional_dia
    ON disponibilidade_profissional(profissional_id, dia_semana) WHERE ativo=TRUE;

CREATE TABLE bloqueios_agenda (
    id UUID PRIMARY KEY,
    profissional_id UUID NOT NULL REFERENCES profissionais(id) ON DELETE CASCADE,
    inicio TIMESTAMP NOT NULL,
    fim TIMESTAMP NOT NULL,
    motivo VARCHAR(250),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (fim > inicio)
);

CREATE INDEX idx_bloqueios_profissional_periodo ON bloqueios_agenda(profissional_id,inicio,fim);

ALTER TABLE agendamentos ADD COLUMN fim TIMESTAMP;
UPDATE agendamentos a SET fim=a.inicio+(s.duracao_minutos * INTERVAL '1 minute') FROM servicos s WHERE s.id=a.servico_id;
ALTER TABLE agendamentos ALTER COLUMN fim SET NOT NULL;
ALTER TABLE agendamentos ADD CONSTRAINT chk_agendamento_fim_posterior CHECK (fim>inicio);

CREATE EXTENSION IF NOT EXISTS btree_gist;
ALTER TABLE agendamentos ADD CONSTRAINT ex_agendamento_profissional_conflito
    EXCLUDE USING gist (profissional_id WITH =, tsrange(inicio,fim,'[)') WITH &&)
    WHERE (status IN ('SOLICITADO','CONFIRMADO'));
