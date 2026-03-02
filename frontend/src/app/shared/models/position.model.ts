export class Position {
    static readonly FILES = 'abcdefgh';
    static readonly RANKS = '87654321';

    private static readonly cache: Position[][] = this.createCache();

    private constructor(
        readonly row: number,
        readonly col: number
    ) {}

    private static createCache(): Position[][] {
        const cache: Position[][] = [];
        for (let row = 0; row < 8; row++) {
            const rowCache: Position[] = [];
            for (let col = 0; col < 8; col++) {
                rowCache.push(new Position(row, col));
            }
            cache.push(rowCache);
        }
        return cache;
    }

    static at(row: number, col: number): Position {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new Error(`Posição inválida: (${row}, ${col})`);
        }
        return this.cache[row][col];
    }

    toNotation(): string {
        return `${Position.FILES[this.col]}${Position.RANKS[this.row]}`;
    }

    static fromNotation(notation: string): Position {
        const colIndex = Position.FILES.indexOf(notation[0]);
        const rowIndex = Position.RANKS.indexOf(notation[1]);
        if (colIndex === -1 || rowIndex === -1) {
            throw new Error(`Notação inválida: ${notation}`);
        }
        return this.at(rowIndex, colIndex);
    }

    static fromUci(uci: string): { from: Position, to: Position } {
        const from = Position.fromNotation(uci.slice(0, 2));
        const to = Position.fromNotation(uci.slice(2, 4));
        return { from, to };
    }

}