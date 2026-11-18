# Stand-in for YouTube used ONLY for offline testing of the app's self-repair and downloads.
import http.server, io, json, re, sys, urllib.parse
from PIL import Image, ImageDraw
BAD = {"89Kq8SDyvfg", "uJfLoE6hanc", "LjHORRHXtyI"}          # dead codes: Pursuit of Happyness, Birdman, Dead Poets Society
TITLES = {
  "GoodPursuit": "The Pursuit of Happyness (2006) Official Trailer - Will Smith",
  "ReactDecoy1": "Pursuit of Happyness REACTION trailer",
  "GoodBirdman": "BIRDMAN Official Trailer",
  "GoodDeadPoe": "Dead Poets Society (1989) Official Trailer",
}
SEARCH = {"pursuit of happyness": ["ReactDecoy1", "GoodPursuit"], "birdman": ["GoodBirdman"], "dead poets society": ["GoodDeadPoe"]}
def pic(id_, w, h, bars):
    im = Image.new("RGB", (w, h), (0, 0, 0)); d = ImageDraw.Draw(im)
    top, bot = (int(h * .125), int(h * .875)) if bars else (0, h)
    hue = sum(map(ord, id_)) % 255
    for y in range(top, bot):
        t = (y - top) / max(1, bot - top)
        d.line([(0, y), (w, y)], fill=(int(40 + hue * .5 + 80 * t), int(60 + 90 * (1 - t)), int(120 + hue * .3 - 60 * t)))
    d.text((w // 2 - 40, h // 2 - 5), id_, fill=(255, 255, 255))
    b = io.BytesIO(); im.save(b, "JPEG", quality=85); return b.getvalue()
class H(http.server.BaseHTTPRequestHandler):
    def log_message(self, *a): pass
    def send(self, code, body=b"", ctype="text/plain"):
        self.send_response(code); self.send_header("Content-Type", ctype); self.send_header("Content-Length", str(len(body))); self.end_headers(); self.wfile.write(body)
    def do_GET(self):
        u = urllib.parse.urlparse(self.path); q = urllib.parse.parse_qs(u.query)
        m = re.match(r"/vi/([A-Za-z0-9_-]{11})/(hqdefault|maxresdefault)\.jpg", u.path)
        if m:
            vid, kind = m.groups()
            if vid in BAD: return self.send(404)
            if kind == "maxresdefault": return self.send(200, pic(vid, 1280, 720, False), "image/jpeg")
            return self.send(200, pic(vid, 480, 360, True), "image/jpeg")
        if u.path == "/oembed":
            vid = re.search(r"v=([A-Za-z0-9_-]{11})", q.get("url", [""])[0])
            vid = vid.group(1) if vid else ""
            if vid in BAD or not vid: return self.send(404)
            return self.send(200, json.dumps({"title": TITLES.get(vid, vid + " official trailer")}).encode(), "application/json")
        if u.path == "/results":
            text = q.get("search_query", [""])[0].lower(); ids = []
            for k, v in SEARCH.items():
                if k in text: ids = v
            body = "<html>" + "".join('{"videoId":"%s"}' % i for i in ids) + "</html>"
            return self.send(200, body.encode(), "text/html")
        self.send(404)
http.server.ThreadingHTTPServer(("127.0.0.1", 8765), H).serve_forever()
