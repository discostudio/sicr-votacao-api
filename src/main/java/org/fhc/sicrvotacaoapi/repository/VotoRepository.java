package org.fhc.sicrvotacaoapi.repository;

import org.fhc.sicrvotacaoapi.model.Voto;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    // Conta todos os votos de uma sessão
    long countBySessaoId(Long sessaoId);

    // Verifica se o associado já votou na pauta
    boolean existsBySessaoPautaIdAndAssociadoCpf(Long pautaId, String associadoCpf);

    // quantos votos de um tipo específico (“SIM” ou “NÃO”) foram registrados em uma sessão de votação
    long countBySessaoIdAndValor(Long sessaoId, VotoValor valor);

    @Query("SELECT v.valor, COUNT(v) FROM Voto v WHERE v.sessao.pauta.id = :pautaId GROUP BY v.valor")
    List<Object[]> countVotosGroupByValor(@Param("pautaId") Long pautaId);
}
