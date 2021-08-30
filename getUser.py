import requests

url = "http://34.64.85.29:8081/"
headers = {'content-type': 'application/json'}

reqData = {
    "userId": "userid"
}

response = requests.post(url+"user", json=reqData, headers=headers)

print(response.text)
