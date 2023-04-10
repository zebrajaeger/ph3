export class JoystickAxis {
    public rawValue!: number;
    public mappedValue!: number;
    public cutValue!: number;
    public cutBorder!: number;
}

export class JoystickPosition {
    public x!: JoystickAxis;
    public y!: JoystickAxis;
}
