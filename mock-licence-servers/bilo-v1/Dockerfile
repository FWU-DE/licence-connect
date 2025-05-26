FROM python:3.14

WORKDIR /app

COPY ./mock-licence-servers/bilo-v1/requirements.txt ./
COPY ../api/src/test/resources/application.properties ./
RUN pip install --no-cache-dir -r requirements.txt

COPY ./mock-licence-servers/bilo-v1/ .

CMD ["python", "mock_bilo_v1_server.py"]
