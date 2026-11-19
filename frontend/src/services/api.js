import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
});

export const autorService = {
  listar: () => api.get('/autores'),
  buscar: (id) => api.get(`/autores/${id}`),
  criar: (autor) => api.post('/autores', autor),
  atualizar: (id, autor) => api.put(`/autores/${id}`, autor),
  deletar: (id) => api.delete(`/autores/${id}`),
};

export const livroService = {
  listar: () => api.get('/livros'),
  buscar: (id) => api.get(`/livros/${id}`),
  listarPorAutor: (autorId) => api.get(`/livros/autor/${autorId}`),
  criar: (livro) => api.post('/livros', livro),
  atualizar: (id, livro) => api.put(`/livros/${id}`, livro),
  deletar: (id) => api.delete(`/livros/${id}`),
};

export default api;
