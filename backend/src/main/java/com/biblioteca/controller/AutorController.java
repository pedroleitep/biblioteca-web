package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.repository.AutorRepository;
import com.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/autores")
@CrossOrigin(origins = "*")
public class AutorController {

    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;

    public AutorController(AutorRepository autorRepository, LivroRepository livroRepository) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
    }

    @PostMapping
    public ResponseEntity<Autor> criar(@Valid @RequestBody Autor autor) {
        Autor salvo = autorRepository.save(autor);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Autor> listar() {
        return autorRepository.findAllByOrderByNomeAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> buscar(@PathVariable Long id) {
        return autorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Autor> atualizar(@PathVariable Long id, @Valid @RequestBody Autor dados) {
        return autorRepository.findById(id)
                .map(autor -> {
                    autor.setNome(dados.getNome());
                    autor.setNacionalidade(dados.getNacionalidade());
                    autor.setDataNascimento(dados.getDataNascimento());
                    return ResponseEntity.ok(autorRepository.save(autor));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!autorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // (extra) autor com livros: em vez do erro da FK, devolve 409 com mensagem amigavel
        if (livroRepository.existsByAutorId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensagem",
                            "Não é possível excluir: o autor possui livros cadastrados. Exclua os livros antes."));
        }
        autorRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
