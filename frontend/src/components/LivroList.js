import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { livroService } from '../services/api';

function LivroList() {
  const [livros, setLivros] = useState([]);
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

  const carregar = () => {
    livroService
      .listar()
      .then((res) => setLivros(res.data))
      .catch(() => setErro('Erro ao carregar livros. O backend está rodando?'));
  };

  useEffect(carregar, []);

  const excluir = (id) => {
    if (!window.confirm('Deseja realmente excluir este livro?')) return;
    livroService
      .deletar(id)
      .then(carregar)
      .catch(() => setErro('Erro ao excluir livro.'));
  };

  return (
    <div>
      <div className="cabecalho">
        <h2>Livros</h2>
        <Link to="/livros/novo" className="btn btn-primario">+ Novo Livro</Link>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Título</th>
            <th>Autor</th>
            <th>ISBN</th>
            <th>Ano</th>
            <th>Gênero</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {livros.length === 0 ? (
            <tr><td colSpan="7" className="vazio">Nenhum livro cadastrado.</td></tr>
          ) : (
            livros.map((l) => (
              <tr key={l.id}>
                <td>{l.id}</td>
                <td>{l.titulo}</td>
                <td>{l.autor ? l.autor.nome : '-'}</td>
                <td>{l.isbn || '-'}</td>
                <td>{l.anoPublicacao || '-'}</td>
                <td>{l.genero || '-'}</td>
                <td>
                  <button className="btn" onClick={() => navigate(`/livros/editar/${l.id}`)}>Editar</button>
                  <button className="btn btn-perigo" onClick={() => excluir(l.id)}>Excluir</button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export default LivroList;
