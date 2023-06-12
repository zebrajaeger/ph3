export class Camera {
    public focus!: boolean;
    public trigger!: boolean;
}

export class Shot {
    public focusTimeMs: number = 0;
    public triggerTimeMs: number = 0;

    public setFromJson(jsonObj: any): Shot {
        this.focusTimeMs = jsonObj.focusTimeMs;
        this.triggerTimeMs = jsonObj.triggerTimeMs;
        return this;
    }

    public clone(): Shot {
        const r = new Shot();
        r.triggerTimeMs = this.triggerTimeMs;
        r.focusTimeMs = this.focusTimeMs;
        return r;
    }
}

export class Shots extends Array<Shot> {
    public setFromJson(jsonObj: any): Shots {
        for (let e of jsonObj) {
            this.push(new Shot().setFromJson(e))
        }
        return this;
    }

    public clone() {
        const r = new Shots();
        for (let shot of this) {
            r.push(shot.clone())
        }
        return r;
    }
}

export class ShotsPresets extends Map<string, Shots> {
    setFromJson(jsonObj: any): ShotsPresets {
        Object.entries(jsonObj).forEach(entry => {
            const [key, value] = entry;
            const shots = new Shots().setFromJson(value);
            this.set(key, shots);
        });
        return this;
    }

    public clone() {
        const r = new ShotsPresets();

        this.forEach((value, key) => {
            r.set(key, value.clone());
        })
        return r;
    }
}
