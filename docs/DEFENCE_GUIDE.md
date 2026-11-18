# Defence guide – questions you should be able to answer

Read the code listed next to each question, run the app, and explain it in your own words.

## JavaFX basics
1. **What is the scene graph and how is the UI layered?** – `App` stacks `HomeView`, `PlayerOverlay`, `IntroOverlay`
   in one `StackPane`; the last child is drawn on top. `PlayerOverlay` is invisible until a movie is opened.
2. **Why `Launcher` and `App`?** – a class extending `Application` can't be the main class when JavaFX is on the
   classpath (fat JAR / some IDEs); `Launcher` has a plain `main`.
3. **What do the Maven dependencies do?** – `javafx-controls` (layouts/labels), `javafx-media` (`AudioClip`), `javafx-web` (`WebView`).

## Requirements
4. **How do you show exactly 5 thumbnails?** (`CategoryRow`) – viewport `Pane` with a `Rectangle` clip; card width =
   (viewport width − 4 gaps) / 5 via a binding; all cards live in an `HBox` that is moved with `translateX`.
5. **How does scrolling work?** – `pageBy()` computes the target offset (`offsetFor`), animates `translateX` with a
   `Timeline` (EASE_BOTH). Triggers: arrows, Shift+wheel/trackpad (`ScrollEvent`), drag-swipe, plus dashes show the page.
6. **How is the hover effect built?** (`MovieCard.apply`) – one `DoubleProperty hover` animated 0→1; a listener sets scale,
   `DropShadow` radius/colour, overlay opacities and title colour from that single value.
7. **Why are edge cards nudged on hover?** – the viewport clips; `shiftFor()` moves the card inward so the zoom isn't cut.
8. **How did you avoid `Button`?** – `CustomButton extends StackPane`: background `Region` whose colour is bound to an
   animated `ObjectProperty<Color>`, optional `Icon` (SVG path) and `Label`; mouse/key handlers call a `Runnable`.
9. **Custom loading animation?** (`LoadingAnimation`) – `RotateTransition` (reel), `Timeline` on `Arc.startAngle`,
   `ScaleTransition`+`FadeTransition` ripples, a gradient `Rectangle` sweeping in a clipped track, dots via a `Timeline`.
   Starts/stops with `visibleProperty` so it costs nothing when hidden.
10. **Sound?** (`SoundManager`) – singleton with `AudioClip`s; hover is rate-limited; the WAVs are synthesised by
    `tools/generate_sounds.py` (sine sweeps + envelopes) so there are no copyright issues; all failures are swallowed.

## Video playback
11. **How do you play YouTube inside JavaFX without storing video?** – `WebView` loads an iframe embed; only the video id is stored.
12. **Why the local HTTP server?** – YouTube rejects embeds from pages without a real origin (error 153). `LocalPlayerServer`
    (JDK `HttpServer`, loopback only) serves the page from `http://127.0.0.1:<port>`; the id is validated by regex.
13. **How do custom controls control YouTube?** – the page exposes `cmd(fn,args)` which `postMessage`s to the iframe
    (`enablejsapi=1`); Java calls it with `engine.executeScript`. State comes back through `snapshot()` polled every 250 ms.
14. **What happens when a trailer can't be embedded?** – YouTube sends an error code → `State.ERROR` → overlay shows
    the message with *Watch on YouTube* (`HostServices.showDocument`) and *Try again*. A 25 s timeout also triggers it.

## Code quality / OOP
15. **Where is abstraction?** – `MovieRepository` interface; UI depends on it, not on the sample data.
16. **Encapsulation/immutability?** – `Movie`, `Category` are records; `YouTubePlayerPane` exposes read-only state properties.
17. **Single responsibility?** – each class has one job (card, row, hero, nav, player pane, overlay, sound, server).
18. **Where do you use observer/binding?** – JavaFX properties: card sizes bound to viewport width, player state listeners
    in `PlayerOverlay.wireState()`.

## New features (be ready to explain)
19. **How does My List persist?** (`UserLibrary`) – two `ObservableList<String>` of YouTube ids; every change writes
    `F:<id>` / `R:<id>` lines to `~/.streamverse/library.txt`; loaded again at start; corrupt lines are ignored by a regex.
20. **How does the heart update everywhere?** – `MovieCard`, `HeroBanner`, `PlayerOverlay` and `HomeView` all listen to the same
    list (cards use a weak listener so rebuilt rows can be garbage-collected).
21. **What is lazy loading and why?** – 120 thumbnails at start-up would flood the network. `MovieCard.loadImage()` is only called
    from `CategoryRow.ensureLoaded()`, which `HomeView.onScrolled()` triggers for rows within ~350 px of the viewport.
22. **How does the Genres menu close when you click elsewhere?** – an event *filter* on `HomeView` checks whether the click target
    is inside the menu or its nav link (`isInside`), otherwise it calls `genreMenu.close()`.
23. **How is "Next trailer" chosen?** – `MovieRepository.nextInGenre()` returns the following movie in the same genre, wrapping around.

25. **How does search work?** (`HomeView.search`) – at start-up a lower-case "haystack" string is built per movie (title, year,
    rating, every genre it is in, description). Each typed word must appear in it (AND); title-start matches score best.
    Typing is debounced 220 ms with a `PauseTransition`. Results appear in one `FlowPane` grid (`SearchResults`).
26. **How is the background picture done?** (`Backdrop`) – a `CoverImage` (cover-scaling) plus a gradient `Region` tint, put at the
    bottom of `HomeView`'s `StackPane`; the `ScrollPane` and its viewport are transparent via CSS so the page scrolls over it.
27. **Why can a movie be in several genres?** – each movie is registered once (`reg(movie(...))`) and genres reference titles,
    so one object (one trailer id, one favourite state) is shared everywhere.

28. **Are your buttons really "text and/or images"?** – yes: `CustomButton` = coloured `Region` + `Icon` (an `ImageView` of a PNG file in
    `resources/icons`, loaded through `Icon.urlOf(name)`) + a text `Label`. White PNGs are recoloured with a `Blend(SRC_ATOP, ColorInput)` effect.
29. **How do rewind/skip 10 s work?** – `PlayerOverlay.skip(±10)` → `YouTubePlayerPane.seekBy()` → page function `skip(d)` computes
    `CUR + d` (clamped to 0..duration) and sends `seekTo` through the YouTube iframe API; the seek bar updates immediately.
    Shortcuts: arrow keys / J / L; double-click on the left or right half of the video (a single click waits 260 ms to be sure it is not a double-click).

30. **How do you fix a dead trailer?** (`net/TrailerResolver`) - oEmbed health check (200 ok / 404 broken), YouTube search page for candidate ids,
    score each candidate (title has "trailer" + the movie's words, penalty for reaction/review/clip), store `oldCode=newCode`;
    `Movie.videoId()` returns the replacement, `Movie.key()` stays the old code so saved lists keep working.
31. **What does Download do and not do?** - saves the card picture and banner to `~/.streamverse/downloads/`, never the video; `ImageCache` reads the local file first.
32. **How is Continue Watching built?** - `PlayerOverlay.saveProgress()` stores `P:key:seconds:duration`; `LocalPlayerServer` passes `?t=seconds` -> `start=` in the embed; `MovieCard` draws the red bar from `UserLibrary.progressFraction`.
33. **Where are the button pictures and their URLs?** - `ButtonPictures` (enum: `file()`, `url()`, `image()`), `Buttons` (one function per button), `Icon.urlOf()`.
34. **How do the pages switch?** - `MainView` keeps all pages in one `StackPane`, shows one, and fades it in; the nav bar and Genres menu call `navigate(name)`.

## Likely "change this live" tasks
* Add a 17th genre → add one `genre("Name", "tagline", "Title1", ...)` line in `SampleMovieRepository` (titles must already be registered).
* Change how dark the background is → the two alpha numbers in `new Backdrop(0.52, 0.86)` in `HomeView`.
* Change the accent colour → `Theme.ACCENT` (and the `#E50914` hex in a few inline styles).
* Show 4 instead of 5 cards → `CategoryRow.VISIBLE`.
* Change the hover zoom → `MovieCard.HOVER_SCALE`.
* Replace a trailer → change the last argument of that `movie(...)` call.
