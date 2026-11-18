#!/usr/bin/env python3
"""
Finds and (optionally) repairs broken YouTube trailer ids in SampleMovieRepository.java.

  python tools/fix_trailers.py            # dry run: shows which ids are broken and what it would use instead
  python tools/fix_trailers.py --apply    # writes the replacements (a backup *.bak is saved first)

How it works (needs internet, no API key):
  1. every id is checked with YouTube's public oEmbed endpoint (HTTP 200 = real AND embeddable);
  2. for each broken one it searches YouTube for "<title> <year> official trailer", takes the first results and
     keeps the first video that is embeddable and whose title mentions "trailer" and the movie's name.
ALWAYS read the printed video titles before using --apply: the search is best-effort and can pick the wrong video.
"""
import json, os, re, shutil, sys, urllib.error, urllib.parse, urllib.request

SRC = os.path.join(os.path.dirname(__file__), "..", "src", "main", "java",
                   "com", "streamverse", "data", "SampleMovieRepository.java")
UA = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                    "Chrome/124.0 Safari/537.36", "Accept-Language": "en-US,en;q=0.9"}
ENTRY = re.compile(r'movie\(\s*"([^"]+)"\s*,\s*(\d+)\s*,\s*"[^"]*"\s*,\s*"[^"]*"\s*,\s*"(?:[^"\\]|\\.)*"\s*,\s*"([A-Za-z0-9_-]{11})"\s*\)')


def get(url, timeout=12):
    with urllib.request.urlopen(urllib.request.Request(url, headers=UA), timeout=timeout) as r:
        return r.read().decode("utf-8", "replace")


def oembed_title(video_id):
    """Title of the video if it exists and may be embedded, else None."""
    url = "https://www.youtube.com/oembed?format=json&url=" + urllib.parse.quote(
        "https://www.youtube.com/watch?v=" + video_id, safe="")
    try:
        return json.loads(get(url, 10)).get("title")
    except Exception:
        return None


def candidates(query, limit=14):
    html = get("https://www.youtube.com/results?search_query=" + urllib.parse.quote(query))
    seen = []
    for vid in re.findall(r'"videoId":"([A-Za-z0-9_-]{11})"', html):
        if vid not in seen:
            seen.append(vid)
    return seen[:limit]


def find_replacement(title, year):
    clean = re.sub(r"\s*\(.*?\)", "", title)                      # "Aladdin (2019)" -> "Aladdin"
    key = re.sub(r"[^a-z0-9 ]", "", clean.lower()).split(" ")[0] if clean else ""
    for vid in candidates(f"{clean} {year} official trailer"):
        t = oembed_title(vid)
        if t and "trailer" in t.lower() and key in re.sub(r"[^a-z0-9 ]", "", t.lower()):
            return vid, t
    return None, None


def main():
    apply = "--apply" in sys.argv
    text = open(SRC, encoding="utf-8").read()
    entries = ENTRY.findall(text)
    print(f"Checking {len(entries)} trailers...\n")
    broken, fixes = [], []
    for title, year, vid in entries:
        if oembed_title(vid) is None:
            broken.append((title, year, vid))
    if not broken:
        print("All trailers are fine - nothing to fix.")
        return
    print(f"{len(broken)} broken id(s):")
    for title, year, vid in broken:
        try:
            new, new_title = find_replacement(title, year)
        except Exception as e:
            new, new_title = None, None
            print(f"  ?? {title} ({year}) {vid}  search failed: {e}")
            continue
        if new:
            print(f"  FIX {title} ({year}): {vid} -> {new}   [{new_title}]")
            fixes.append((vid, new))
        else:
            print(f"  --- {title} ({year}): {vid}  no replacement found, fix it by hand")
    if apply and fixes:
        shutil.copyfile(SRC, SRC + ".bak")
        for old, new in fixes:
            text = text.replace(f'"{old}"', f'"{new}"')
        open(SRC, "w", encoding="utf-8").write(text)
        print(f"\nApplied {len(fixes)} fix(es). Backup saved as SampleMovieRepository.java.bak - rebuild the app.")
    elif fixes:
        print("\nDry run only. Re-run with --apply to write these changes.")


if __name__ == "__main__":
    main()
