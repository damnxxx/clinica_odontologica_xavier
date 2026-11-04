INSERT INTO disponibilidade_profissional (id, profissional_id, dia_semana, inicio, fim, intervalo_minutos)
SELECT gen_random_uuid(), p.id, dia.dia_semana, TIME '09:00', TIME '17:00', 30
FROM profissionais p
CROSS JOIN generate_series(3, 6) AS dia(dia_semana)
WHERE p.ativo = TRUE
  AND NOT EXISTS (
      SELECT 1
      FROM disponibilidade_profissional existente
      WHERE existente.profissional_id = p.id
  );
