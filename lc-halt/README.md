# LC Halt

This folder contains all code necessary to develop, run and build LC Halt.

## About

Taken from ADR-07:

> As a reference for a licence holding system, we will develop 'LC Halt' as a small prototype.
Potentially, it may get extended to support more types of licences.
It may be of use as a reference to other licence holding systems how the interface to licence connect core can be designed.
>
>LC Halt should provide an API to manage licences for states, schools or specific users.
On request, it will hand out the respective licences.
It should provide a SwaggerUI and OpenAPI spec and persist the configured licences also after restarting the application.
The authentication at the API can be done with API keys.

LC Halt is built using [FastAPI](https://fastapi.tiangolo.com).

## Dev Setup

**Prerequisites:**
- docker & docker compose
- [uv](https://github.com/astral-sh/uv)
- Python 3

Activate the virtual environment and install requirements:

```sh
uv venv
uv pip install -r requirements.txt
```

Run LC Halt:

```sh
uv run fastapi dev main.py
```

## Run LC Halt

<!-- containerized setup goes here -->

## Deployment
