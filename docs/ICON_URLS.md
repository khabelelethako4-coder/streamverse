# Button pictures and their URLs

Every icon on a button is a **picture file** (white PNG, transparent background, 96x96) stored at
`src/main/resources/icons/<name>.png`. The code reaches each picture **through functions that return its URL**:

```java
ButtonPictures.PLAY.url()        // -> file:/.../icons/play.png   (jar:file:...!/icons/play.png inside a packaged jar)
ButtonPictures.PLAY.image()      // -> the Image, loaded from that URL (cached)
ButtonPictures.logAll();         // prints NAME -> URL for every button picture (App calls it at start-up)

Buttons.playTrailer(action)      // ready-made custom button: panel + picture + text
Buttons.rewind10(action)         // round picture button
Buttons.downloadAll(action) ...  // see ui/controls/Buttons.java for the full list
```

| Class | Job |
|---|---|
| `ui/controls/ButtonPictures.java` | register of every button picture: `file()`, `url()`, `image()`, `logAll()` |
| `ui/controls/Buttons.java` | one factory function per button (`playPause`, `rewind10`, `forward10`, `next`, `heart`, `watchTrailer`, `remindMe`, `downloadAll`, ...) - each takes its picture from `ButtonPictures` |
| `ui/controls/Icon.java` | loads a picture by URL (`Icon.urlOf(name)`) and recolours it (white / black / red / green) |
| `ui/controls/CustomButton.java` | the button itself: coloured panel + `Icon` picture + optional text label |

## Every picture and its URL

Run the app and read the console: it prints one line per picture, e.g. `[pictures]   PLAY   file:/<project>/src/main/resources/icons/play.png`.

| Picture file | URL pattern |
|---|---|
| `add.png` | `file:/<project>/src/main/resources/icons/add.png` |
| `bell.png` | `file:/<project>/src/main/resources/icons/bell.png` |
| `bell_outline.png` | `file:/<project>/src/main/resources/icons/bell_outline.png` |
| `calendar.png` | `file:/<project>/src/main/resources/icons/calendar.png` |
| `check.png` | `file:/<project>/src/main/resources/icons/check.png` |
| `chevron_left.png` | `file:/<project>/src/main/resources/icons/chevron_left.png` |
| `chevron_right.png` | `file:/<project>/src/main/resources/icons/chevron_right.png` |
| `close.png` | `file:/<project>/src/main/resources/icons/close.png` |
| `delete.png` | `file:/<project>/src/main/resources/icons/delete.png` |
| `download.png` | `file:/<project>/src/main/resources/icons/download.png` |
| `download_done.png` | `file:/<project>/src/main/resources/icons/download_done.png` |
| `drop_down.png` | `file:/<project>/src/main/resources/icons/drop_down.png` |
| `exit_fullscreen.png` | `file:/<project>/src/main/resources/icons/exit_fullscreen.png` |
| `external.png` | `file:/<project>/src/main/resources/icons/external.png` |
| `forward.png` | `file:/<project>/src/main/resources/icons/forward.png` |
| `forward_10.png` | `file:/<project>/src/main/resources/icons/forward_10.png` |
| `fullscreen.png` | `file:/<project>/src/main/resources/icons/fullscreen.png` |
| `heart.png` | `file:/<project>/src/main/resources/icons/heart.png` |
| `heart_outline.png` | `file:/<project>/src/main/resources/icons/heart_outline.png` |
| `info.png` | `file:/<project>/src/main/resources/icons/info.png` |
| `mute.png` | `file:/<project>/src/main/resources/icons/mute.png` |
| `pause.png` | `file:/<project>/src/main/resources/icons/pause.png` |
| `play.png` | `file:/<project>/src/main/resources/icons/play.png` |
| `replay.png` | `file:/<project>/src/main/resources/icons/replay.png` |
| `replay_10.png` | `file:/<project>/src/main/resources/icons/replay_10.png` |
| `search.png` | `file:/<project>/src/main/resources/icons/search.png` |
| `shuffle.png` | `file:/<project>/src/main/resources/icons/shuffle.png` |
| `skip_next.png` | `file:/<project>/src/main/resources/icons/skip_next.png` |
| `volume.png` | `file:/<project>/src/main/resources/icons/volume.png` |

## Recreating or replacing the pictures

```bash
pip install cairosvg
python tools/generate_icons.py      # redraws all PNGs
```
To use your own artwork, replace any file with another white-on-transparent PNG of the same name.

## Movie pictures (not stored in the project)

Movie pictures are YouTube pictures loaded by URL: `https://img.youtube.com/vi/<CODE>/hqdefault.jpg` (cards) and `.../maxresdefault.jpg` (hero banner) - see `docs/TRAILER_LINKS.md`.
A **downloaded** movie is read from the saved copy in `~/.streamverse/downloads/` instead. The page background is `src/main/resources/images/stage.jpg`.
