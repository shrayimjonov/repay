import asyncio
import logging

from aiogram import Bot, Dispatcher

from config import TOKEN
from app.handler import router
from app.database import create_db_table

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler('bot.log')
    ]
)


bot = Bot(token=TOKEN)
dp = Dispatcher()


async def main():
    logging.info("Бот запускается...")
    dp.include_router(router)
    try:
        await dp.start_polling(bot)
    except Exception as e:
        logging.error(f"Ошибка при запуске бота: {e}")
        raise


if __name__ == '__main__':
    logging.info("Create db table...")
    create_db_table()

    try:
        logging.info("Бот запускается...")
        asyncio.run(main())
    except KeyboardInterrupt:
        logging.info("Бот был остановлен пользователем.")
    except Exception as e:
        logging.error(f"Ошибка при работе бота: {e}")
    finally:
        logging.info("Процесс завершен.")
