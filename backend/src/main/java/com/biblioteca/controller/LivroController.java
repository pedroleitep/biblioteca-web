package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class LivroController {

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public LivroController(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    /** Carrega o autor completo a partir do {"autor": {"id": ...}} recebido no JSON. */
    private Optional<Autor> resolverAutor(Livro livro) {
        if (livro.getAutor() == null || livro.getAutor().getId() == null) {
            return Optional.empty();
        }
        return autorRepository.findById(livro.getAutor().getId());
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody Livro livro) {
        Optional<Autor> autor = resolverAutor(livro);
        if (autor.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", "Autor informado não existe"));
        }
        livro.setAutor(autor.get());
        Livro salvo = livroRepository.save(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Livro> listar() {
        return livroRepository.findAllByOrderByTituloAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscar(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** (extra) Lista os livros de um autor. */
    @GetMapping("/autor/{autorId}")
    public List<Livro> listarPorAutor(@PathVariable Long autorId) {
        return livroRepository.findByAutorId(autorId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Livro dados) {
        Optional<Livro> existente = livroRepository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<Autor> autor = resolverAutor(dados);
        if (autor.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", "Autor informado não existe"));
        }
        Livro livro = existente.get();
        livro.setTitulo(dados.getTitulo());
        livro.setIsbn(dados.getIsbn());
        livro.setAnoPublicacao(dados.getAnoPublicacao());
        livro.setGenero(dados.getGenero());
        livro.setAutor(autor.get()); // atualiza tambem o autor
        return ResponseEntity.ok(livroRepository.save(livro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        livroRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
