import os

from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app, supports_credentials=True, resources={r"/*": {"origins": "http://localhost:63342"}})
file_path = os.path.join(os.path.dirname(__file__), 'takeoff_and_land.py')


@app.route('/')
def home():
    return jsonify({"message": "Flask server is running!"})


@app.after_request
def after_request(response):
    print(response.headers)
    response.headers["Access-Control-Allow-Origin"] = "http://localhost:63342"
    response.headers["Access-Control-Allow-Headers"] = "Content-Type, Authorization"
    response.headers["Access-Control-Allow-Methods"] = "POST, OPTIONS"

    return response


@app.route('/save_code', methods=['OPTIONS', 'POST'])
def save_code():
    if request.method == 'OPTIONS':
        response = jsonify({})
        response.headers["Access-Control-Allow-Origin"] = "http://localhost:63342"
        response.headers["Access-Control-Allow-Methods"] = "POST, OPTIONS"
        response.headers["Access-Control-Allow-Headers"] = "Content-Type, Authorization"
        return response, 200

    data = request.get_json()
    generated_code = data['code']  # Get the code from the front end

    # Open the takeoff_and_land.py file and read the existing content
    with open('takeoff_and_land.py', 'r') as file:
        file_data = file.read()

    # Replace the placeholder {{BLOCKLY_GENERATED_CODE}} with the generated code
    updated_data = file_data.replace(
        "# BLOCKLY_GENERATED_CODE_START\n    # replace by generate code\n    # BLOCKLY_GENERATED_CODE_END",
        f"# BLOCKLY_GENERATED_CODE_START\n    {generated_code}\n    # BLOCKLY_GENERATED_CODE_END"
    )

    # Write the updated content back to the file
    with open('takeoff_and_land.py', 'a') as f:  # 'a' means appending
        f.write(updated_data + "\n")
        print("Saving code to:", file_path)

    # with open('takeoff_and_land.py', 'w') as file:
    #     file.write(updated_data)
    #     print("Saving code to:", file_path)

    return jsonify({"status": "success", "message": "Code successfully saved to takeoff_and_land.py"})


if __name__ == '__main__':
    print("Saving code to:", file_path)
    app.run(debug=True)


