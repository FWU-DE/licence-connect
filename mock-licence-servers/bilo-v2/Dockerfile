FROM python:3.14

WORKDIR /app

COPY ./mock-licence-servers/bilo-v2/requirements.txt ./
COPY ../api/src/test/resources/application.properties ./
RUN pip install --no-cache-dir -r requirements.txt

COPY ./mock-licence-servers/bilo-v2/ .

CMD ["python", "mock_bilo_v2_server.py"]
