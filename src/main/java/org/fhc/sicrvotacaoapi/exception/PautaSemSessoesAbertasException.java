package org.fhc.sicrvotacaoapi.exception;

public class PautaSemSessoesAbertasException extends RuntimeException {

    public PautaSemSessoesAbertasException(Long pautaId) {
        super("A pauta " + pautaId + " não possui sessões abertas. Não é possível registrar o voto.");
    }

    public PautaSemSessoesAbertasException(String message) {
        super(message);
    }
}
