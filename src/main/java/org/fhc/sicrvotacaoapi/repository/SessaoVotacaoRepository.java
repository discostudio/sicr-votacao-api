package org.fhc.sicrvotacaoapi.repository;

import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    // Verifica se existe sessão aberta para uma pauta
    boolean existsByPautaIdAndFimAfter(Long pautaId, LocalDateTime now);

    // Busca a sessão de votação aberta para uma pauta, caso exista
    Optional<SessaoVotacao> findByPautaIdAndFimAfter(Long pautaId, LocalDateTime agora);

    // Recupera todas as sessões de uma pauta em ordem de fim (mais antiga → mais recente)
    List<SessaoVotacao> findAllByPautaIdOrderByFimAsc(Long pautaId);
}
