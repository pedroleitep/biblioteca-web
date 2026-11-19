package com.biblioteca;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long criarAutor(String nome) throws Exception {
        String json = "{\"nome\":\"" + nome + "\",\"nacionalidade\":\"Brasileira\",\"dataNascimento\":\"1839-06-21\"}";
        MvcResult resultado = mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode node = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return node.get("id").asLong();
    }

    @Test
    @DisplayName("Deve vincular livro a autor (POST autor -> POST livro com autor.id)")
    void deveVincularLivroAAutor() throws Exception {
        Long autorId = criarAutor("Machado de Assis");

        String livroJson = "{\"titulo\":\"Dom Casmurro\",\"isbn\":\"978-8535910667\","
                + "\"anoPublicacao\":1899,\"genero\":\"Romance\",\"autor\":{\"id\":" + autorId + "}}";

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livroJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor.id").value(autorId))
                .andExpect(jsonPath("$.autor.nome").value("Machado de Assis"))
                .andExpect(jsonPath("$.criadoEm").exists());

        // a data de nascimento deve voltar do banco exatamente como foi enviada (AAAA-MM-DD)
        mockMvc.perform(get("/api/autores/" + autorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataNascimento").value("1839-06-21"));

        // (extra) endpoint de livros por autor
        mockMvc.perform(get("/api/livros/autor/" + autorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"));
    }

    @Test
    @DisplayName("Deve executar fluxo completo de autor: criar, buscar, atualizar, deletar (extra)")
    void deveExecutarFluxoCompletoAutor() throws Exception {
        Long id = criarAutor("Clarice Lispector");

        mockMvc.perform(get("/api/autores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Clarice Lispector"));

        mockMvc.perform(put("/api/autores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Clarice Lispector (atualizada)\",\"nacionalidade\":\"Ucraniana-Brasileira\",\"dataNascimento\":\"1920-12-10\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Clarice Lispector (atualizada)"))
                .andExpect(jsonPath("$.dataNascimento").value("1920-12-10"));

        mockMvc.perform(delete("/api/autores/" + id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/autores/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 409 ao excluir autor que possui livros (extra)")
    void deveRetornar409AoExcluirAutorComLivros() throws Exception {
        Long autorId = criarAutor("Jorge Amado");
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Capitaes da Areia\",\"autor\":{\"id\":" + autorId + "}}"))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/autores/" + autorId))
                .andExpect(status().isConflict());
    }
}
