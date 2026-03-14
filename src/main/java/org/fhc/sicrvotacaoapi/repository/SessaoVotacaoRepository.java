package org.fhc.sicrvotacaoapi.repository;

import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    //boolean existsByPautaId(Long pautaId);
    // Verifica se existe sessão ativa para uma pauta
    boolean existsByPautaIdAndFimAfter(Long pautaId, LocalDateTime now);

    //SessaoVotacao findByPautaId(Long pautaId);
    // Buscar sessão por pauta
    SessaoVotacao findTopByPautaIdOrderByFimDesc(Long pautaId);
}
