package com.biblioteca.repository;

import com.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findAllByOrderByTituloAsc();

    List<Livro> findByAutorId(Long autorId);

    boolean existsByAutorId(Long autorId);
}
