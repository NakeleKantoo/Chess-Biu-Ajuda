import { PieceSymbol } from "../../features/pages/game/components/game-session/components/board/components/piece/piece";
import { Position } from "./position.model";

export interface SquareView {
    piece: PieceSymbol | null;
    position: Position;
    isMove: boolean;
    isCapture: boolean;
    isLastMove: boolean;
    isCheck: boolean;
}