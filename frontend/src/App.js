import React from 'react';
import { BrowserRouter, NavLink, Route, Routes } from 'react-router-dom';
import LivroList from './components/LivroList';
import LivroForm from './components/LivroForm';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <header className="menu">
        <h1>📚 Biblioteca Web</h1>
        <nav>
          <NavLink to="/autores">Autores</NavLink>
          <NavLink to="/livros">Livros</NavLink>
        </nav>
      </header>
      <main className="conteudo">
        <Routes>
          <Route path="/livros" element={<LivroList />} />
          <Route path="/livros/novo" element={<LivroForm />} />
          <Route path="/livros/editar/:id" element={<LivroForm />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;
