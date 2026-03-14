package org.fhc.sicrvotacaoapi.repository;

import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    boolean existsByPautaId(Long pautaId);

    SessaoVotacao findByPautaId(Long pautaId);
}
