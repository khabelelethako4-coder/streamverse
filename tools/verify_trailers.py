#!/usr/bin/env python3
"""
Checks that every YouTube trailer ID in SampleMovieRepository.java is a real,
embeddable video, using YouTube's public oEmbed endpoint (no API key needed).

Run on a machine with internet access:   python3 tools/verify_trailers.py
Prints OK/FAIL per title together with the video's real YouTube title so you
can confirm it is the right trailer. Fix any FAIL line by replacing the ID in
src/main/java/com/streamverse/data/SampleMovieRepository.java.
"""
import json, os, re, sys, urllib.error, urllib.request

SRC = os.path.join(os.path.dirname(__file__), "..", "src", "main", "java",
                   "com", "streamverse", "data", "SampleMovieRepository.java")
text = open(SRC, encoding="utf-8").read()
# movie("Title", year, "Rating", "Genre", "Description", "YOUTUBE_ID")
pattern = re.compile(r'movie\(\s*"([^"]+)"\s*,\s*\d+\s*,\s*"[^"]*"\s*,\s*"[^"]*"\s*,\s*"(?:[^"\\]|\\.)*"\s*,\s*"([A-Za-z0-9_-]{11})"\s*\)')
items = pattern.findall(text)
bad = 0
for title, vid in items:
    url = "https://www.youtube.com/oembed?format=json&url=https://www.youtube.com/watch?v=" + vid
    try:
        with urllib.request.urlopen(url, timeout=10) as r:
            info = json.load(r)
        print(f"OK    {title:<34} {vid}  ->  {info.get('title')}")
    except urllib.error.HTTPError as e:
        bad += 1
        print(f"FAIL  {title:<34} {vid}  ->  HTTP {e.code} (not found / embedding disabled)")
    except Exception as e:
        bad += 1
        print(f"FAIL  {title:<34} {vid}  ->  {e}")
print(f"\n{len(items)} trailers checked, {bad} problem(s).")
sys.exit(1 if bad else 0)
