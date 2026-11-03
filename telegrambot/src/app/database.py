import logging
from datetime import datetime
import psycopg2

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler('app.log')
    ]
)

def create_db_table():
    try:
        connection = psycopg2.connect(
            host="127.0.0.1",
            port="5432",
            dbname="repay",
            user="telegram",
            password=""
        )
        cursor = connection.cursor()

        create_table_query = '''
        CREATE TABLE IF NOT EXISTS tg_users (
            id SERIAL PRIMARY KEY,
            telegram_id INTEGER NOT NULL,
            name TEXT NOT NULL,
            created_date TIMESTAMP NOT NULL,
            client_username TEXT,
            tg_username TEXT
        )
        '''

        cursor.execute(create_table_query)
        connection.commit()
        logging.info("Таблица tg_users успешно создана или уже существует.")

    except Exception as error:
        logging.error(f"Ошибка при подключении к базе данных: {error}")

    finally:
        if connection:
            cursor.close()
            connection.close()


def add_user_to_db(telegram_id, name, client_username, tg_username):
    try:
        connection = psycopg2.connect(
            host="127.0.0.1",
            port="5432",
            dbname="repay",
            user="telegram",
            password=""
        )
        cursor = connection.cursor()

        check_query = "SELECT id FROM tg_users WHERE telegram_id = %s"
        cursor.execute(check_query, (telegram_id,))
        user = cursor.fetchone()

        if user:
            logging.info(f"Пользователь с telegram_id {telegram_id} уже существует в базе данных.")
        else:
            created_date = datetime.now()
            insert_query = '''
            INSERT INTO tg_users (telegram_id, client_username, created_date, name, tg_username)
            VALUES (%s, %s, %s, %s, %s)
            '''
            cursor.execute(insert_query, (telegram_id, client_username, created_date, name, tg_username))
            connection.commit()

            logging.info(f"Пользователь с telegram_id {telegram_id} успешно добавлен в базу данных.")

    except Exception as error:
        logging.error(f"Ошибка при добавлении данных пользователя с telegram_id {telegram_id}: {error}")

    finally:
        if connection:
            cursor.close()
            connection.close()
