export class Position {
    row: number;
    col: number;

    constructor(row: number, col: number) {
        this.row = row;
        this.col = col;
    }

    toNotation(): string {
        const files = 'abcdefgh';
        const ranks = '87654321';
        return `${files[this.col]}${ranks[this.row]}`;
    }

    equals(other: Position): boolean {
        return this.row === other.row && this.col === other.col;
    }

    static fromNotation(notation: string): Position {
        const files = 'abcdefgh';
        const ranks = '87654321';
        const col = files.indexOf(notation[0]);
        const row = ranks.indexOf(notation[1]);
        return new Position(row, col);
    }
}