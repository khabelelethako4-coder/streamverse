# Git workflow (worth 10 + 15 marks)

The rubric rewards **frequent, meaningful commits from every member, branching, merges and clear messages**,
and the oral check asks each member to explain the code behind their commits. So split the work, and let each
person genuinely work on, run and understand their own part.

## 1. One-time setup (team lead)

```bash
cd StreamVerse
git init -b main
git add .gitignore pom.xml README.md
git commit -m "chore: add Maven project skeleton and README"
git remote add origin <your GitHub/GitLab URL>
git push -u origin main
git checkout -b develop && git push -u origin develop
```
Everyone else: `git clone <url>` and `git checkout develop`.

## 2. Branch model

* `main` – always runs; only receives merges from `develop` (milestones).
* `develop` – integration branch.
* `feature/<topic>` – one per task, merged into `develop` with a pull/merge request reviewed by another member.

## 3. Split for 5 members

| Member | Branch(es) | Files / topics | Be able to explain |
|---|---|---|---|
| 1 – Data & library | `feature/data-model`, `feature/my-list`, `feature/downloads` | `model/*`, `data/*`, `library/UserLibrary`, `downloads/DownloadService`, `DownloadsPage`, `MyListPage`, `Toast`, `Theme`, `style.css`, `pom.xml`, `App`, `Launcher` | records, repository interface, observable lists + file persistence, Maven/JavaFX setup, scene layering |
| 2 – Browse UI | `feature/movie-card`, `feature/category-row` | `MovieCard`, `CategoryRow`, `CoverImage`, `ImageCache` | bindings (card width = viewport/5), clip rectangle, hover animation, paging maths, lazy loading |
| 3 – Pages & navigation | `feature/home-page`, `feature/coming-soon`, `feature/genre-page` | `MainView`, `HomePage`, `GenrePage`, `ComingSoonPage`, `SearchResults`, `Backdrop`, `NavBar`, `NavLink`, `GenreMenu`, `HeroBanner`, `IntroOverlay` | scroll-spy, search index + ranking, background image layer, genre menu closing logic, hero cross-fade |
| 4 – Player & self-repair | `feature/player-core`, `feature/player-ui`, `feature/trailer-repair` | `net/TrailerResolver`, `LocalPlayerServer`, `YouTubePlayerPane`, `player.html`, `PlayerOverlay`, `SeekBar` | WebView, why a local server, JS bridge (`cmd` / `snapshot`), player states, error handling |
| 5 – Custom controls & audio | `feature/custom-controls`, `feature/loading-animation`, `feature/sound` | `CustomButton`, `Buttons`, `ButtonPictures`, `Icon`, `Chip`, `LoadingAnimation`, `SoundManager`, `resources/icons/*.png`, `tools/generate_icons.py`, `tools/generate_sounds.py` | why no `Button`, Timeline/Transition APIs, SVG icons, AudioClip, how the sound effects are synthesised |

## 4. Commit habits that score well

* Commit small and often – each commit = one idea that compiles. Aim for **at least 8–10 commits per member**
  spread across the weeks, not one big commit the night before.
* Message format: `type: short summary` – types: `feat`, `fix`, `style`, `refactor`, `docs`, `chore`.
  Examples:
  * `feat: add MovieCard with thumbnail and title`
  * `feat: hover zoom and glow on MovieCard`
  * `fix: stop edge cards being clipped when zoomed`
  * `feat: CategoryRow shows 5 cards and pages with arrows`
  * `feat: custom film-reel loading animation`
  * `refactor: extract CoverImage from MovieCard`
* Never commit `target/`, IDE folders or `.class` files (`.gitignore` already covers them).

## 5. Daily commands

```bash
git checkout develop && git pull
git checkout -b feature/category-row
# ... work, run the app, then:
git add src/main/java/com/streamverse/ui/CategoryRow.java
git commit -m "feat: CategoryRow shows 5 cards and pages with arrows"
git push -u origin feature/category-row
# open a merge request -> teammate reviews -> merge into develop
git checkout develop && git pull
```
Milestone: merge `develop` into `main` with `git merge --no-ff develop` (keeps a visible merge commit) and tag it
`git tag v1.0`.

## 6. Proof for the marker

Fill in `docs/TEAM_CONTRIBUTIONS.md` and, before the demo, run:
```bash
git shortlog -sn --all            # commits per member
git log --graph --oneline --all   # branches + merges
```
