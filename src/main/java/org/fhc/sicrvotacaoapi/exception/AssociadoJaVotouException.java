package org.fhc.sicrvotacaoapi.exception;

public class AssociadoJaVotouException extends RuntimeException{

    public AssociadoJaVotouException(Long sessaoId, Long associadoId) {
        super("Associado " + associadoId + " já votou na sessão " + sessaoId);
    }
}
