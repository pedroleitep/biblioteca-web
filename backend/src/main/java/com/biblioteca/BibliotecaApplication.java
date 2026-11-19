package com.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BibliotecaApplication {

    /*
     * Usa UTC como fuso padrao da JVM antes de qualquer conexao com o banco.
     * Sem isso, datas (ex.: 1839-06-21) podem voltar com um dia a menos por causa
     * da conversao LocalDate <-> java.sql.Date com o fuso historico de America/Sao_Paulo.
     * Fica num bloco static para rodar assim que a classe e carregada (antes do DataSource).
     */
    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
    }
}
