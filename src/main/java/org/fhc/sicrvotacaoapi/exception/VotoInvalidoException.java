package org.fhc.sicrvotacaoapi.exception;

public class VotoInvalidoException extends RuntimeException{

    public VotoInvalidoException(String valor) {
        super("Valor do voto inválido: '" + valor + "'. Use 'SIM' ou 'NAO'.");
    }
}
