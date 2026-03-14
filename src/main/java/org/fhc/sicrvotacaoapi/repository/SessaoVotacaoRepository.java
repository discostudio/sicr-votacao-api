package org.fhc.sicrvotacaoapi.repository;

import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    //boolean existsByPautaId(Long pautaId);
    // Verifica se existe sessão ativa para uma pauta
    boolean existsByPautaIdAndFimAfter(Long pautaId, LocalDateTime now);

    //SessaoVotacao findByPautaId(Long pautaId);
    // Recupera a última sessão da pauta
    //SessaoVotacao findTopByPautaIdOrderByFimDesc(Long pautaId);

    // Recupera todas as sessões de uma pauta em ordem de fim (mais antiga → mais recente)
    List<SessaoVotacao> findAllByPautaIdOrderByFimAsc(Long pautaId);
}
