function revToString(rev: number): string {
    if (rev === null || rev === undefined) {
        return '-';
    }
    const size = Math.abs(rev);
    return size.toFixed(2) + ' (' + (size * 360).toFixed(1) + 'deg)';
}

function degToString(deg: number): string {
    if (deg === null || deg === undefined) {
        return '-';
    }
    return deg.toFixed(2);
}


const layouts = {
    de: {
        default: [
            "^ 1 2 3 4 5 6 7 8 9 0 \u00DF \u00B4 {bksp}",
            "q w e r t z u i o p \u00FC +",
            " a s d f g h j k l \u00F6 \u00E4 # {enter}",
            "{shift} < y x c v b n m , . - {shift}",
            "@ {space}",
        ],
        shift: [
            '\u00B0 ! " \u00A7 $ % & / ( ) = ? ` {bksp}',
            "Q W E R T Z U I O P \u00DC *",
            "A S D F G H J K L \u00D6 \u00C4 ' {enter}",
            "{shift} > Y X C V B N M ; : _ {shift}",
            "@ {space}",
        ]
    },
    en: {
        default: [
            "` 1 2 3 4 5 6 7 8 9 0 - = {bksp}",
            "q w e r t y u i o p [ ] \\",
            "a s d f g h j k l ; ' {enter}",
            "{shift} z x c v b n m , . / {shift}",
            "@ {space}",
        ],
        shift: [
            "~ ! @ # $ % ^ & * ( ) _ + {bksp}",
            "Q W E R T Y U I O P { } |",
            'A S D F G H J K L : " {enter}',
            "{shift} Z X C V B N M < > ? {shift}",
            "@ {space}",
        ],
    }
}

export {layouts, degToString, revToString};
