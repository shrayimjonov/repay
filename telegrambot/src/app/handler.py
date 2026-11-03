from aiogram import Router, F
from aiogram import types
from aiogram.filters import Command, state
from aiogram.types import Message, CallbackQuery, ReplyKeyboardMarkup, KeyboardButton
from aiogram.fsm.state import StatesGroup, State
from aiogram.fsm.context import FSMContext
from aiocache import Cache
from app.database import add_user_to_db

import app.keyboards as kb
import logging

from app.repay_api import get_token_api, create_client_location_api
from config import CACHE

cache = Cache.from_url(CACHE)

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),  # Вывод в консоль
        logging.FileHandler('app.log')  # Запись в файл log
    ]
)



router = Router()

def get_client_keyboard():
    return ReplyKeyboardMarkup(keyboard=[
        [KeyboardButton(text='ℹ️Информация'), KeyboardButton(text='💰Баланс')],
        [KeyboardButton(text='📍Локация'), KeyboardButton(text='Назад')]
    ], resize_keyboard=True)

keyboard_add_back_location = ReplyKeyboardMarkup(keyboard=[
        [KeyboardButton(text='Добавить локацию'), KeyboardButton(text='Назад о информации клиента', reply_markup=get_client_keyboard())],
    ],
        resize_keyboard=True,
    )

class DataUser(StatesGroup):
    login = State()
    password = State()
    main = State()


class InfoLocation(StatesGroup):
    location_name = State()
    location = State()


@router.message(Command('start'))
async def reg_handler(message: Message, state: FSMContext):
    await state.set_state(DataUser.login)
    await message.answer("Для пользования ботом Repay введите логин и пароль.")
    await message.answer("Логин: ")


@router.message(DataUser.login)
async def reg_second(message: Message, state: FSMContext):
    await state.update_data(login=message.text)
    await state.set_state(DataUser.password)
    await message.answer("Ведите пароль")


@router.message(DataUser.password)
async def reg_third(message: Message, state: FSMContext):
    try:
        logging.info(
            f"Получен пароль от пользователя {message.from_user.full_name} (Telegram ID: {message.from_user.id})")
        await state.update_data(password=message.text)
        data = await state.get_data()
        response = get_token_api(data["login"], data["password"])

        if response.status_code == 200:
            telegram_id = message.from_user.id
            name = message.from_user.full_name
            client_username = data["login"]
            tg_username = message.chat.username

            logging.info(
                f"Добавление пользователя в базу данных: Telegram ID = {telegram_id}, Username = {tg_username}")

            add_user_to_db(telegram_id, name, client_username, tg_username)

            logging.info(f"Пользователь {name} успешно добавлен в базу данных.")

            response_data = response.json()
            token = response_data.get("access_token")
            if token is not None:
                await cache.set(value=token, key=data["login"], ttl=3000)
                await state.set_state(DataUser.main)
                await message.answer("Вы успешно авторизовались", reply_markup=kb.main)

        else:
            logging.error(f"Ошибка при запросе токена: статус {response.status_code}")
            await message.answer("Логин или пароль неправильно. Попробуйте ввести ещё раз /start")

    except Exception as error:
        logging.error(f"Ошибка при обработке сообщения от пользователя {message.from_user.full_name}: {error}")



@router.message(lambda message: message.text == "Кредиты" or message.text == "Назад")
async def main(message: Message, state: FSMContext):

    page = 0

    try:
        data = await state.get_data()
        token = await cache.get(data["login"], None)

        await message.reply(text="Кредиты", reply_markup=await kb.inline_credits(page, token))
    except Exception:
        return await message.answer("Сначала выполните авторизацию командой /start.")


@router.callback_query(lambda callback_query: callback_query.data.startswith('page:'))
async def process_page_selection(callback_query: CallbackQuery, state: FSMContext):
    data = await state.get_data()
    token = await cache.get(data["login"])

    if not token:
        await callback_query.answer("Сначала выполните авторизацию командой /start.")
        return

    page = int(callback_query.data.split(':')[1])
    keyboard = await kb.inline_credits(page, token)
    await callback_query.message.edit_reply_markup(reply_markup=keyboard)



@router.callback_query(lambda callback_query: callback_query.data.startswith('client:'))
async def process_client_selection(callback_query: CallbackQuery, state: FSMContext):
    client_id  = callback_query.data.split(':')[1]
    client_name  = callback_query.data.split(':')[2]
    loanExtId = callback_query.data.split(':')[3]

    await callback_query.answer('')
    await state.update_data(client_id=client_id, client_name=client_name, loanExtId=loanExtId)

    keyboard = get_client_keyboard()

    await callback_query.message.answer(f"👤 {client_name}", reply_markup=keyboard)



@router.message(lambda message: message.text == "ℹ️Информация")
async def process_info_request(message: Message, state: FSMContext):
    data = await state.get_data()
    loanExtId = data.get('loanExtId')

    try:
        token = await cache.get(data["login"])

        client_info = await kb.get_client_info(loanExtId, token)

        max_message_length = 4096
        for i in range(0, len(client_info), max_message_length):
            await message.answer(client_info[i:i + max_message_length])
    except Exception:
        return await message.answer("Сначала выполните авторизацию командой /start.")

@router.message(lambda message: message.text == "💰Баланс")
async def process_balance_selection(message: Message, state: FSMContext):
    data = await state.get_data()
    loanExtId = data.get('loanExtId')

    try:
        token = await cache.get(data["login"])

        client_balance = await kb.get_client_balance(loanExtId, token)
        await message.answer(client_balance)
    except Exception:
        return await message.answer("Сначала выполните авторизацию командой /start.")


@router.message(lambda message: message.text == '📍Локация')
async def process_location_selection(message: Message, state: FSMContext):
    data = await state.get_data()
    loanExtId = data.get('loanExtId')

    token = await cache.get(data["login"], None)

    locations = await kb.get_client_location(loanExtId, token)

    if isinstance(locations, str):
        await message.reply(text=locations)
        await message.answer("Хотите добавить локацию", reply_markup=keyboard_add_back_location)
        return

    await message.answer("📍Выберите локацию:", reply_markup=locations)
    await message.answer("Хотите добавить локацию", reply_markup=keyboard_add_back_location)



@router.callback_query(lambda callback_query: callback_query.data.startswith('location_id:'))
async def process_location_detail(callback_query: CallbackQuery):
    latitude = callback_query.data.split(':')[2]
    longitude = callback_query.data.split(':')[3]

    await callback_query.answer('')
    await callback_query.message.reply_location(latitude=float(latitude), longitude=float(longitude))



@router.message(lambda message: message.text == "Добавить локацию")
async def request_location_name(message: Message, state: FSMContext):
    await state.set_state(InfoLocation.location_name)
    await message.answer("Введите имя локации:")
    await state.update_data(waiting_for_location_name=message.text)


@router.message(InfoLocation.location_name)
async def handle_location_name(message: Message, state: FSMContext):
    await state.update_data(location_name=message.text)

    await message.answer("Пожалуйста, отправьте свое местоположение.", reply_markup=types.ReplyKeyboardMarkup(
        keyboard=[[types.KeyboardButton(text="Отправить местоположение", request_location=True)]],
        resize_keyboard=True,
        one_time_keyboard=True
    ))
    await state.set_state(InfoLocation.location)


@router.message(InfoLocation.location)
async def handle_location(message: Message, state: FSMContext):

    data = await state.get_data()
    loanExtId = data.get('loanExtId')
    description = data.get('location_name')
    token = await cache.get(data["login"])

    client_name = data.get('client_name')

    if message.location is None:
        await message.answer(f"👤 {client_name}", reply_markup=get_client_keyboard())
        return

    latitude = message.location.latitude
    longitude = message.location.longitude


    response = create_client_location_api(loanExtId, latitude, longitude, description, token)
    if response['status'] == "SUCCESS":
        await message.answer("Локация добавлено", reply_markup=get_client_keyboard())

    else:
        await message.answer("Не удалось добавить, попробуйте позже",  reply_markup=get_client_keyboard())



@router.message(lambda message: message.text == 'Назад о информации клиента')
async def back_to_client_info(message: Message, state: FSMContext):
    data = await state.get_data()
    client_name = data.get('client_name')

    keyboard = get_client_keyboard()

    if client_name:  # Ensure client_name is not empty
        await message.reply(f'Вы вернулись к информации о клиенте: {client_name}', reply_markup=keyboard)
    else:
        await message.reply("Информация о клиенте недоступна.", reply_markup=keyboard)



