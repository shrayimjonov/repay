import requests




API_URL = "YOUR_API_URL"


def get_token_api(username, password):
    url = 'http://127.0.0.1:5555/realms/micro/protocol/openid-connect/token'
    data = {
        "client_id": "fasto",
        "username": username,
        "password": password,
        "grant_type": "password",
        "client_secret": "",
        "scope": "openid"
    }

    headers = {
        "Content-Type": "application/x-www-form-urlencoded"
    }

    response = requests.post(url, data=data, headers=headers)

    return response



def get_list_api(page, token):

    url = 'http://127.0.0.1:8888/loan/api/v1/filter'
    data = {
    "pageNumber" : page,
    "pageSize" : 10,

    "params" : {
        "isFeatured" : True
        }
    }

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }


    response = requests.post(url,headers=headers, json=data)

    return response.json()


def get_client_info_api(loanExtId, token):
    url = 'http://127.0.0.1:8888/loan/api/v1/byLoan'

    data = {
        "loanExtId" : loanExtId
    }

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    response = requests.post(url, headers=headers, json=data)

    return response.json()


def get_client_balance_api(loanExtId, token):
    url = 'http://127.0.0.1:8888/loan/api/v1/balance'

    data = {
        "loanExtId" : loanExtId
    }

    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    response = requests.post(url, headers=headers, json=data)

    return response.json()

def get_client_location_api(loanExtId, token):
    url = 'http://127.0.0.1:8888/file/api/v1/location/filter'

    data = {
        "loan_ext_id": loanExtId,
        "pageNumber": 0,
        "pageSize": 10,
        "sortBy": "id",
        "descending": True
    }


    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    response = requests.post(url, headers=headers, json=data)

    return response.json()


def create_client_location_api(loanExtId,latitude, longitude, description, token):

    url = 'http://127.0.0.1:8888/file/api/v1/location'

    data = {
        "loan_ext_id" : loanExtId,
        "latitude" : latitude,
        "longitude" : longitude,
        "description" : description
    }


    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {token}"
    }

    try:
        response = requests.post(url, headers=headers, json=data)
        response.raise_for_status()  # Проверка на HTTP ошибки
        try:
            return response.json()  # Попытка декодировать JSON
        except ValueError:
            print(f"Ошибка декодирования JSON: {response.text}")  # Логирование текста ответа
            return {"status": "FAILURE", "message": "Неверный формат ответа от сервера."}
    except requests.exceptions.RequestException as e:
        print(f"Ошибка запроса: {e}")
        return {"status": "FAILURE"}
