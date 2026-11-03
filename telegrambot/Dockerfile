FROM internal.example.com/hub.docker.com/python:3.10.12

WORKDIR /app

COPY . /app/
RUN pip install --upgrade pip
RUN pip install --no-cache-dir -r /app/requirements.txt

CMD ["run-webhook"]

EXPOSE 9000

CMD ["sh", "-c", "python /app/src/run.py"]