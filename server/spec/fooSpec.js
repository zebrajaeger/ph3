const {Row} = require('../src/calc');

describe("A suite", function () {
    it("contains spec with an expectation", function () {
        const row = new Row();
        row.overlap = 0.3;
        row.targetSize = 3;
        row.sourceSize = 1;

        {
            // partial test
            row.isPartial = true;
            const res = row.calc();
            expect(res.n).toBe(4);
            expect(res.o).toBe(1 / 3);
        }

        {
            // full circle
            row.isPartial = false;
            const res = row.calc();
            expect(res.n).toBe(5);
            expect(res.o).toBe(0.4);
        }


        expect(true).toBe(true);
    });
});
