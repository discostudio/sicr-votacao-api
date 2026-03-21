package org.fhc.sicrvotacaoapi.repository;

import org.fhc.sicrvotacaoapi.model.Voto;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.fhc.sicrvotacaoapi.repository.projection.TotaisPorSessaoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    // Verifica se o associado já votou na pauta
    boolean existsBySessaoPautaIdAndAssociadoCpf(Long pautaId, String associadoCpf);

    @Query("SELECT v.valor, COUNT(v) FROM Voto v WHERE v.sessao.pauta.id = :pautaId GROUP BY v.valor")
    List<Object[]> countVotosGroupByValor(@Param("pautaId") Long pautaId);

    // Quantidade de votos nas sessões
    @Query("""
        SELECT 
            v.sessao.id AS sessaoId,
            SUM(CASE WHEN v.valor = 'SIM' THEN 1 ELSE 0 END) AS totalSim,
            SUM(CASE WHEN v.valor = 'NAO' THEN 1 ELSE 0 END) AS totalNao
        FROM Voto v
        WHERE v.sessao.id IN :idsSessoes
        GROUP BY v.sessao.id
    """)
    List<TotaisPorSessaoProjection> countVotosPorSessao(List<Long> idsSessoes);
}
