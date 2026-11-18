# Putting the project on GitHub (5 members)

## A. Team lead – one time

1. Install Git for Windows: https://git-scm.com/download/win (accept the defaults), then reopen the terminal.
2. Tell Git who you are (use the same e-mail as your GitHub account so commits are credited to you):
   ```powershell
   git config --global user.name  "Your Name"
   git config --global user.email "you@example.com"
   ```
3. On https://github.com click **New repository**:
   * Name: `StreamVerse`
   * Visibility: **Private** (recommended for coursework, so only your group can see it)
   * Do **not** tick "Add a README" (the project already has one)
4. Add your 4 teammates: repository **Settings -> Collaborators -> Add people** -> type each GitHub username
   (they must accept the e-mail invitation).
5. In the project folder:
   ```powershell
   cd C:\Users\khabe\.vscode-shared\StreamVerse
   git init -b main
   git add .
   git commit -m "chore: initial StreamVerse project"
   git remote add origin https://github.com/<your-username>/StreamVerse.git
   git push -u origin main
   git checkout -b develop
   git push -u origin develop
   ```
   If GitHub asks you to sign in, use the browser prompt or a Personal Access Token (Settings -> Developer settings).

## B. Everyone else

```powershell
git clone https://github.com/<team-lead-username>/StreamVerse.git
cd StreamVerse
git checkout develop
git checkout -b feature/<your-topic>      # see docs/GIT_WORKFLOW.md for your branch name
```
Run it: `mvn javafx:run` (or the `Launcher` class in VS Code).

## C. Daily routine

```powershell
git checkout develop ; git pull                  # start from the latest team code
git checkout feature/<your-topic>
git merge develop                                # bring in your teammates' work
# ...edit, run the app, then:
git add <the files you changed>
git commit -m "feat: short description"
git push -u origin feature/<your-topic>
```
Then on GitHub click **Compare & pull request**, ask a teammate to review, and **Merge** into `develop`.

## D. Rules that avoid merge conflicts
* Only edit the files of your own part (table in `docs/GIT_WORKFLOW.md`); ask before touching someone else's file.
* Pull `develop` before you start work each day.
* Never commit `target/` (already in `.gitignore`).
* If Git reports a conflict, don't panic: open the file, keep the correct lines, remove the `<<<<<<<` markers,
  `git add` the file and `git commit`.
