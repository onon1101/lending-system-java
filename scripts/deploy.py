#!/usr/bin/env python3

import os
import socket
import subprocess
import sys
from pathlib import Path
from urllib.parse import urlparse


PROJECT_DIR = Path(__file__).resolve().parent.parent
COMPOSE_FILE = PROJECT_DIR / "compose.yml"
ENV_FILE = Path(os.environ.get("DEPLOY_ENV_FILE", Path.home() / ".env"))


SERVICES = {
    "postgres": {
        "env": "DATABASE_URL",
        "default_port": 5432,
        "default_url": "r2dbc:postgresql://postgres.onon1101.org:5432/lending_prod",
    },
    "redis": {
        "env": "REDIS_URL",
        "default_port": 6379,
        "default_url": "redis://redis.onon1101.org:6379",
    },
    "rustfs": {
        "env": "MINIO_ENDPOINT",
        "default_port": 9000,
        "default_url": "http://rustfs.onon1101.org:9000",
    },
}


def load_env(path: Path) -> dict[str, str]:
    if not path.is_file():
        raise FileNotFoundError(f"找不到環境設定檔：{path}")

    values: dict[str, str] = {}
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue

        key, value = line.split("=", 1)
        values[key.strip()] = value.strip().strip("\"'")

    return values


def parse_address(value: str, default_port: int) -> tuple[str, int]:
    # urllib does not recognize r2dbc:postgresql as a URL scheme.
    parsed = urlparse(value.removeprefix("r2dbc:"))
    if not parsed.hostname:
        raise ValueError(f"無法從連線字串取得主機名稱：{value}")

    return parsed.hostname, parsed.port or default_port


def is_reachable(host: str, port: int, timeout: float = 3.0) -> bool:
    try:
        addresses = socket.getaddrinfo(host, port, type=socket.SOCK_STREAM)
    except socket.gaierror:
        return False

    for family, socktype, proto, _, sockaddr in addresses:
        try:
            with socket.socket(family, socktype, proto) as connection:
                connection.settimeout(timeout)
                connection.connect(sockaddr)
                return True
        except OSError:
            continue

    return False


def compose(environment: dict[str, str], *arguments: str) -> None:
    command = [
        "/usr/bin/docker",
        "compose",
        "-f",
        str(COMPOSE_FILE),
        *arguments,
    ]
    print("+", " ".join(command), flush=True)
    subprocess.run(
        command,
        cwd=PROJECT_DIR,
        env=environment,
        check=True,
    )


def main() -> int:
    file_env = load_env(ENV_FILE)
    runtime_env = os.environ.copy()
    runtime_env.update(file_env)

    for service_name, config in SERVICES.items():
        env_name = config["env"]
        configured_url = file_env.get(env_name, config["default_url"])
        host, port = parse_address(configured_url, config["default_port"])

        if not is_reachable(host, port):
            raise ConnectionError(f"{service_name} 無法連線：{host}:{port}")

        print(f"✓ {service_name}: 使用主機服務 {host}:{port}")
        runtime_env[env_name] = configured_url

    compose(
        runtime_env,
        "up",
        "--detach",
        "--build",
        "--force-recreate",
        "--remove-orphans",
        "backend",
        "frontend",
    )
    compose(runtime_env, "ps")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (ConnectionError, OSError, ValueError, subprocess.CalledProcessError) as error:
        print(f"部署失敗：{error}", file=sys.stderr)
        raise SystemExit(1)
