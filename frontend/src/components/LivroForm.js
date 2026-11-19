import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { autorService, livroService } from '../services/api';

const VAZIO = { titulo: '', isbn: '', anoPublicacao: '', genero: '', autorId: '' };

function LivroForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [livro, setLivro] = useState(VAZIO);
  const [autores, setAutores] = useState([]);
  const [erro, setErro] = useState('');
  const editando = Boolean(id);

  useEffect(() => {
    autorService
      .listar()
      .then((res) => setAutores(res.data))
      .catch(() => setErro('Erro ao carregar autores.'));

    if (editando) {
      livroService
        .buscar(id)
        .then((res) =>
          setLivro({
            titulo: res.data.titulo || '',
            isbn: res.data.isbn || '',
            anoPublicacao: res.data.anoPublicacao ?? '',
            genero: res.data.genero || '',
            autorId: res.data.autor ? String(res.data.autor.id) : '',
          })
        )
        .catch(() => setErro('Livro não encontrado.'));
    }
  }, [id, editando]);

  const alterar = (e) => setLivro({ ...livro, [e.target.name]: e.target.value });

  const salvar = (e) => {
    e.preventDefault();
    // Monta o objeto igual ao CompromissoForm: o autor vai só com o id
    const dados = {
      titulo: livro.titulo,
      isbn: livro.isbn || null,
      anoPublicacao: livro.anoPublicacao === '' ? null : Number(livro.anoPublicacao),
      genero: livro.genero || null,
      autor: { id: Number(livro.autorId) },
    };
    const requisicao = editando ? livroService.atualizar(id, dados) : livroService.criar(dados);
    requisicao
      .then(() => navigate('/livros'))
      .catch(() => setErro('Erro ao salvar livro. Verifique os campos e o autor.'));
  };

  return (
    <div>
      <h2>{editando ? 'Editar Livro' : 'Novo Livro'}</h2>
      {erro && <p className="erro">{erro}</p>}
      <form onSubmit={salvar} className="formulario">
        <label>
          Título *
          <input name="titulo" value={livro.titulo} onChange={alterar} maxLength={200} required />
        </label>
        <label>
          Autor *
          <select name="autorId" value={livro.autorId} onChange={alterar} required>
            <option value="">-- Selecione um autor --</option>
            {autores.map((a) => (
              <option key={a.id} value={a.id}>{a.nome}</option>
            ))}
          </select>
        </label>
        <label>
          ISBN
          <input name="isbn" value={livro.isbn} onChange={alterar} maxLength={20} />
        </label>
        <label>
          Ano de publicação
          <input type="number" name="anoPublicacao" value={livro.anoPublicacao} onChange={alterar} />
        </label>
        <label>
          Gênero
          <input name="genero" value={livro.genero} onChange={alterar} maxLength={50} />
        </label>
        <div className="acoes">
          <button type="submit" className="btn btn-primario">Salvar</button>
          <button type="button" className="btn" onClick={() => navigate('/livros')}>Cancelar</button>
        </div>
      </form>
    </div>
  );
}

export default LivroForm;
