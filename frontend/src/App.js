import React from 'react';
import { BrowserRouter, NavLink, Routes } from 'react-router-dom';
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
        <Routes>{/* rotas de autores e livros entram nas branches feature */}</Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;
