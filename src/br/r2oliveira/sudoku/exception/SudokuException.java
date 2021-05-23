package br.r2oliveira.sudoku.exception;

public class SudokuException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SudokuException(String mensagem) {
		super(mensagem);
	}
}
