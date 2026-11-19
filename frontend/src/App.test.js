import { render, screen } from '@testing-library/react';
import App from './App';

jest.mock('./services/api', () => ({
  autorService: { listar: () => Promise.resolve({ data: [] }) },
  livroService: { listar: () => Promise.resolve({ data: [] }) },
}));

test('renderiza o menu da Biblioteca Web', () => {
  render(<App />);
  expect(screen.getByText(/Biblioteca Web/i)).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'Autores' })).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'Livros' })).toBeInTheDocument();
});
