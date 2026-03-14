package org.fhc.sicrvotacaoapi.repository;

import org.fhc.sicrvotacaoapi.model.Voto;
import org.fhc.sicrvotacaoapi.model.VotoValor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsBySessaoIdAndAssociadoId(Long sessaoId, Long associadoId);

    Optional<Voto> findBySessaoIdAndAssociadoId(Long sessaoId, Long associadoId);

    long countBySessaoIdAndValor(Long sessaoId, VotoValor valor);
}
