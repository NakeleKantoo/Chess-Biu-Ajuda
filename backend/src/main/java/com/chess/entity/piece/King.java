package com.chess.entity.piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.chess.entity.base.*;
import com.chess.entity.board.Board;

public class King extends Piece {

    protected King(Color color) {
        super(color);
    }

    private List<Position> getNearMoves(Board board, Position from) {
        List<Position> nearMoves = new ArrayList<>();

        Direction[] directions = Direction.getAllDirections();

        // Verifica cada direção possível
        for (Direction dir : directions) {
            Position to = from.getNext(dir);
            if (to == null) continue;

            // Adiciona o movimento se a posição de destino estiver vazia ou tiver uma peça adversária
            if (!board.hasPieceAt(to) || board.hasPieceAt(to, color.opposite())) {
                nearMoves.add(to);
            }
        }

        return nearMoves;
    }

    private List<Position> getCastleMoves(Board board, Position from) {
        List<Position> castleMoves = new ArrayList<>();

        // Verifica possibilidade de roque na ala do rei
        if (board.canCastleKingSide(this.color)) {
            Position kingSideCastlePos = this.color.isWhite()
                ? Position.at("g1")  // Posição do roque do rei para peças brancas
                : Position.at("g8"); // Posição do roque do rei para peças pretas
            castleMoves.add(kingSideCastlePos);
        }

        // Verifica possibilidade de roque na ala da dama
        if (board.canCastleQueenSide(this.color)) {
            Position queenSideCastlePos = this.color.isWhite()
                ? Position.at("c1")  // Posição do roque da dama para peças brancas
                : Position.at("c8"); // Posição do roque da dama para peças pretas
            castleMoves.add(queenSideCastlePos);
        }

        return castleMoves;
    }

    @Override
    public char getSymbol() {
        return this.color.isWhite() ? 'R' : 'r';
    }

    @Override
    public boolean isAttacking(Board board, Position from, Position to) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");
        Objects.requireNonNull(to, "A posição de destino não pode ser nula.");

        // Verifica se a posição "to" está adjacente à posição "from"
        boolean isAttacking = from.isNear(to) && !from.equals(to);

        return isAttacking;
    }

    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        Objects.requireNonNull(board, "O tabuleiro não pode ser nulo.");
        Objects.requireNonNull(from, "A posição de origem não pode ser nula.");

        List<Position> moves = new ArrayList<>();

        // Adiciona movimentos próximos e movimentos de roque
        moves.addAll(getNearMoves(board, from));
        moves.addAll(getCastleMoves(board, from));

        return moves;
    }

}
