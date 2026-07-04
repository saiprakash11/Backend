from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen


BACKEND = "http://localhost:8080"


class EMSHandler(SimpleHTTPRequestHandler):
    def end_headers(self):
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type, Authorization")
        self.send_header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
        self.send_header("Pragma", "no-cache")
        self.send_header("Expires", "0")
        super().end_headers()

    def do_OPTIONS(self):
        self.send_response(204)
        self.end_headers()

    def do_GET(self):
        if self.path.startswith(("/api/", "/v3/", "/swagger-ui", "/actuator/")):
            self.proxy()
            return
        for header in ("If-Modified-Since", "If-None-Match"):
            if header in self.headers:
                del self.headers[header]
        super().do_GET()

    def do_POST(self):
        self.proxy()

    def do_PUT(self):
        self.proxy()

    def do_DELETE(self):
        self.proxy()

    def proxy(self):
        length = int(self.headers.get("Content-Length", "0") or "0")
        body = self.rfile.read(length) if length else None
        headers = {
            key: value
            for key, value in self.headers.items()
            if key.lower() not in {"host", "content-length", "connection"}
        }
        request = Request(
            BACKEND + self.path,
            data=body,
            headers=headers,
            method=self.command,
        )
        try:
            with urlopen(request, timeout=30) as response:
                payload = response.read()
                self.send_response(response.status)
                content_type = response.headers.get("Content-Type", "application/json; charset=utf-8")
                self.send_header("Content-Type", content_type)
                self.send_header("Content-Length", str(len(payload)))
                self.send_header("Connection", "close")
                self.end_headers()
                self.wfile.write(payload)
        except HTTPError as error:
            payload = error.read()
            self.send_response(error.code)
            content_type = error.headers.get("Content-Type", "text/plain; charset=utf-8")
            self.send_header("Content-Type", content_type)
            self.send_header("Content-Length", str(len(payload)))
            self.send_header("Connection", "close")
            self.end_headers()
            self.wfile.write(payload)
        except URLError as error:
            self.send_response(502)
            self.send_header("Content-Type", "text/plain; charset=utf-8")
            self.end_headers()
            self.wfile.write(f"Backend proxy error: {error}".encode("utf-8"))


if __name__ == "__main__":
    port = 3001
    server = ThreadingHTTPServer(("localhost", port), EMSHandler)
    print(f"EMS frontend/proxy server running at http://localhost:{port}")
    print("Open: http://localhost:{0}/authentication/unified_login_portal/code.html".format(port))
    server.serve_forever()
