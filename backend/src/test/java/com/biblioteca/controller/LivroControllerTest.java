package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LivroRepository livroRepository;

    @MockBean
    private AutorRepository autorRepository;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Autor autor() {
        Autor autor = new Autor();
        autor.setId(1L);
        autor.setNome("Machado de Assis");
        return autor;
    }

    private Livro novoLivro(Long id, String titulo, Autor autor) {
        Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(titulo);
        livro.setIsbn("978-8535910667");
        livro.setAnoPublicacao(1899);
        livro.setGenero("Romance");
        livro.setAutor(autor);
        return livro;
    }

    @Test
    @DisplayName("Deve criar livro vinculado a autor com sucesso (201)")
    void deveCriarLivroComSucesso() throws Exception {
        Autor somenteId = new Autor();
        somenteId.setId(1L);
        Livro entrada = novoLivro(null, "Dom Casmurro", somenteId);
        Livro salvo = novoLivro(10L, "Dom Casmurro", autor());

        when(autorRepository.findById(1L)).thenReturn(Optional.of(autor()));
        when(livroRepository.save(any(Livro.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor.id").value(1))
                .andExpect(jsonPath("$.autor.nome").value("Machado de Assis"));
    }

    @Test
    @DisplayName("Deve retornar 404 para id inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/livros/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 400 quando livro vier sem autor")
    void deveRetornar400QuandoSemAutor() throws Exception {
        Livro entrada = novoLivro(null, "Sem Autor", null);

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isBadRequest());
        verify(livroRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar livros ordenados por titulo (extra)")
    void deveListarLivrosOrdenados() throws Exception {
        when(livroRepository.findAllByOrderByTituloAsc()).thenReturn(List.of(
                novoLivro(2L, "Dom Casmurro", autor()),
                novoLivro(1L, "Memorias Postumas de Bras Cubas", autor())));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$[1].titulo").value("Memorias Postumas de Bras Cubas"));
    }

    @Test
    @DisplayName("Deve atualizar livro trocando o autor")
    void deveAtualizarLivroTrocandoAutor() throws Exception {
        Autor novoAutor = new Autor();
        novoAutor.setId(2L);
        novoAutor.setNome("Clarice Lispector");

        when(livroRepository.findById(5L)).thenReturn(Optional.of(novoLivro(5L, "Antigo", autor())));
        when(autorRepository.findById(2L)).thenReturn(Optional.of(novoAutor));
        when(livroRepository.save(any(Livro.class))).thenAnswer(inv -> inv.getArgument(0));

        Autor ref = new Autor();
        ref.setId(2L);
        Livro dados = novoLivro(null, "A Hora da Estrela", ref);

        mockMvc.perform(put("/api/livros/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("A Hora da Estrela"))
                .andExpect(jsonPath("$.autor.nome").value("Clarice Lispector"));
    }
}
