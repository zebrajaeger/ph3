export function revToString(rev: number): string {
    if (rev === null || rev === undefined) {
        return '-';
    }
    const size = Math.abs(rev);
    return size.toFixed(2) + ' (' + (size * 360).toFixed(1) + 'deg)';
}

export function degToString(deg: number): string {
    if (deg === null || deg === undefined) {
        return '-';
    }
    return deg.toFixed(2);
}
