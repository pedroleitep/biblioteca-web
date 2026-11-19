import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { autorService } from '../services/api';

function formatarData(data) {
  if (!data) return '-';
  const [ano, mes, dia] = data.split('-');
  return `${dia}/${mes}/${ano}`;
}

function AutorList() {
  const [autores, setAutores] = useState([]);
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

  const carregar = () => {
    autorService
      .listar()
      .then((res) => setAutores(res.data))
      .catch(() => setErro('Erro ao carregar autores. O backend está rodando?'));
  };

  useEffect(carregar, []);

  const excluir = (id) => {
    if (!window.confirm('Deseja realmente excluir este autor?')) return;
    autorService
      .deletar(id)
      .then(carregar)
      .catch((e) => setErro(e.response?.data?.mensagem || 'Erro ao excluir autor.'));
  };

  return (
    <div>
      <div className="cabecalho">
        <h2>Autores</h2>
        <Link to="/autores/novo" className="btn btn-primario">+ Novo Autor</Link>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Nacionalidade</th>
            <th>Nascimento</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {autores.length === 0 ? (
            <tr><td colSpan="5" className="vazio">Nenhum autor cadastrado.</td></tr>
          ) : (
            autores.map((a) => (
              <tr key={a.id}>
                <td>{a.id}</td>
                <td>{a.nome}</td>
                <td>{a.nacionalidade || '-'}</td>
                <td>{formatarData(a.dataNascimento)}</td>
                <td>
                  <button className="btn" onClick={() => navigate(`/autores/editar/${a.id}`)}>Editar</button>
                  <button className="btn btn-perigo" onClick={() => excluir(a.id)}>Excluir</button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export default AutorList;
