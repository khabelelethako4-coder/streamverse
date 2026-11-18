#!/usr/bin/env python3
"""
Generates the short UI sound effects used by StreamVerse (no external assets,
no copyright issues). Output: src/main/resources/sounds/*.wav
Run:  python3 tools/generate_sounds.py
"""
import math, os, random, struct, wave

RATE = 22050
OUT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "sounds")
os.makedirs(OUT, exist_ok=True)


def write(name, samples):
    peak = max(1e-9, max(abs(s) for s in samples))
    data = b"".join(struct.pack("<h", int(max(-1, min(1, s / peak * 0.9)) * 32767)) for s in samples)
    with wave.open(os.path.join(OUT, name + ".wav"), "wb") as w:
        w.setnchannels(1); w.setsampwidth(2); w.setframerate(RATE); w.writeframes(data)


def env(i, n, attack=0.01, power=2.5):
    t = i / n
    a = min(1.0, t / attack) if attack > 0 else 1.0
    return a * (1 - t) ** power


def tone(freq, dur, vol=1.0, power=2.5, harm=0.0):
    n = int(RATE * dur)
    return [vol * env(i, n, 0.02, power) *
            (math.sin(2 * math.pi * freq * i / RATE) + harm * math.sin(4 * math.pi * freq * i / RATE))
            for i in range(n)]


def sweep(f0, f1, dur, vol=1.0, power=1.5):
    n = int(RATE * dur); phase = 0.0; out = []
    for i in range(n):
        f = f0 + (f1 - f0) * (i / n) ** 1.6
        phase += 2 * math.pi * f / RATE
        out.append(vol * env(i, n, 0.08, power) * math.sin(phase))
    return out


def mix(*tracks):
    n = max(len(t) for t in tracks)
    return [sum(t[i] if i < len(t) else 0 for t in tracks) for i in range(n)]


def delay(track, seconds):
    return [0.0] * int(RATE * seconds) + track


random.seed(7)
# hover: very short soft tick
write("hover", tone(1250, 0.05, power=3, harm=0.2))
# nav: slightly lower, rounder tick
write("nav", tone(760, 0.07, power=3, harm=0.3))
# click: two quick rising notes
write("click", mix(tone(660, 0.07, power=2), delay(tone(990, 0.09, power=2.5), 0.05)))
# open: rising cinematic sweep + low thump + airy noise
noise = [random.uniform(-1, 1) * 0.15 * env(i, int(RATE * 0.45), 0.2, 1.2) for i in range(int(RATE * 0.45))]
write("open", mix(sweep(180, 900, 0.45, 0.8), tone(70, 0.35, 1.0, power=2.0), noise))
# close: falling sweep
write("close", sweep(800, 160, 0.3, 0.9))
# error: two low descending notes
write("error", mix(tone(330, 0.14, power=2), delay(tone(247, 0.22, power=2), 0.13)))
print("sounds written to", os.path.abspath(OUT))
