package org.fhc.sicrvotacaoapi.repository;

import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    // Verifica se existe sessão aberta para uma pauta
    //boolean existsByPautaIdAndFimAfter(Long pautaId, LocalDateTime now);

    // Busca a sessão de votação aberta para uma pauta
    Optional<SessaoVotacao> findByPautaIdAndFimAfter(Long pautaId, LocalDateTime agora);

    // Recupera todas as sessões de uma pauta em ordem de fim (mais antiga → mais recente)
    List<SessaoVotacao> findAllByPautaIdOrderByFimAsc(Long pautaId);
    Page<SessaoVotacao> findAllByPautaIdOrderByFimAsc(Long pautaId, Pageable pageable);

    // Verifica se existem sessão abertas para a pauta
    @Query("SELECT COUNT(s) > 0 FROM SessaoVotacao s WHERE s.pauta.id = :pautaId AND s.fim > :dataNow")
    boolean existsSessaoAberta(@Param("pautaId") Long pautaId, @Param("dataNow") LocalDateTime now);
}
