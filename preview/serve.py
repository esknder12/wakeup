#!/usr/bin/env python3
"""Tiny static server for the NEQU volume-lock preview.

Serves the repository root (so the preview can reuse the app's real drawables)
and sends "/" straight to the preview page.
"""
import http.server
import os
import socketserver

PORT = int(os.environ.get("PORT", "3000"))
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=ROOT, **kwargs)

    def _rewrite_root(self):
        if self.path in ("/", "/index.html"):
            self.path = "/preview/index.html"

    def do_GET(self):
        self._rewrite_root()
        return super().do_GET()

    def do_HEAD(self):
        self._rewrite_root()
        return super().do_HEAD()

    def end_headers(self):
        self.send_header("Cache-Control", "no-store")
        super().end_headers()

    def log_message(self, fmt, *args):
        print("%s - %s" % (self.address_string(), fmt % args), flush=True)


class Server(socketserver.ThreadingTCPServer):
    allow_reuse_address = True
    daemon_threads = True


if __name__ == "__main__":
    with Server(("0.0.0.0", PORT), Handler) as httpd:
        print(f"NEQU preview serving {ROOT} on http://0.0.0.0:{PORT}", flush=True)
        httpd.serve_forever()
