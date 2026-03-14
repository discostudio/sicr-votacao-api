package org.fhc.sicrvotacaoapi.exception;

public class PautaSemSessoesException extends RuntimeException {

    public PautaSemSessoesException(Long pautaId) {
        super("A pauta " + pautaId + " não possui sessões. Não há resultado disponível.");
    }

    public PautaSemSessoesException(String message) {
        super(message);
    }
}
