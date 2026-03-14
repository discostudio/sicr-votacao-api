package org.fhc.sicrvotacaoapi.exception;

public class AssociadoJaVotouException extends RuntimeException{

    public AssociadoJaVotouException(Long pautaId, Long associadoId) {
        super("Associado " + associadoId + " já votou na pauta " + pautaId);
    }
}
