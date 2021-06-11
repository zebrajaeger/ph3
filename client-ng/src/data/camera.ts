export class Camera {
    public focus: boolean;
    public trigger: boolean;
}

export class Shot {
    public focusTimeMs: number;
    public triggerTimeMs: number;
}

export class Shots extends Map<string, Shot[]> {

}
