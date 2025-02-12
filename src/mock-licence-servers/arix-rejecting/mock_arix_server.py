from flask import Flask, Response

app = Flask(__name__)

@app.route('/', defaults={'path': ''}, methods=['POST'])
@app.route('/<path:path>', methods=['POST'])
def endpoint(path):
    return Response("<error>Sorry, interface search not allowed for your IP</error>", mimetype='text/xml')

if __name__ == '__main__':
    app.run(debug=True, port=1235, host="0.0.0.0")