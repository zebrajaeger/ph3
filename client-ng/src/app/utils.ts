export function revToString(rev: number): string {
    if (rev === null || rev === undefined) {
        return '-';
    }
    const size = Math.abs(rev);
    return size.toFixed(3) + ' (' + (size * 360).toFixed(1) + 'deg)';
}
