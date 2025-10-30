package com.automacao.model;

/**
 * Record para representar dados do aluno.
 * Usando Java 21 Records para imutabilidade e simplicidade.
 */
public record Aluno(String nome, String cpf) {

    /**
     * Construtor compacto com validação
     */
    public Aluno {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF não pode ser nulo ou vazio");
        }
    }

    @Override
    public String toString() {
        return "Aluno[nome=%s, cpf=%s]".formatted(nome, cpf);
    }
}