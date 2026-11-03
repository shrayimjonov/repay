from tkinter.messagebox import RETRY

from aiogram.types import ReplyKeyboardMarkup, KeyboardButton, InlineKeyboardButton

from aiogram.utils.keyboard import InlineKeyboardBuilder

from .repay_api import get_list_api, get_client_info_api, get_client_balance_api, get_client_location_api



main = ReplyKeyboardMarkup(keyboard=[
    [KeyboardButton(text='Кредиты')]
], resize_keyboard=True)



async def inline_credits(page: int ,token: str):
    keyboard = InlineKeyboardBuilder()
    response = get_list_api(page,token=token)

    total_pages = 0

    if response['status'] == "SUCCESS":
        loans = response["result"].get("loans", [])
        total_pages = response["result"].get("totalPages", 1)

        if loans:
            for loan in loans:
                client_name = loan.get("payerName")
                client_id = loan.get("id")
                loanExtId = loan.get("extId")
                keyboard.add(InlineKeyboardButton(text=client_name, callback_data=f'client:{client_id}:{client_name}:{loanExtId}'))
        else:
            keyboard.add(InlineKeyboardButton(text="Нет доступных кредитов", callback_data="no_loans"))


    if page > 0:
        keyboard.add(InlineKeyboardButton(text="Назад", callback_data=f'page:{page - 1}'))
    if page + 1 < total_pages:
        keyboard.add(InlineKeyboardButton(text="Далее", callback_data=f'page:{page + 1}'))

    return keyboard.adjust(2).as_markup()


async def get_client_info(loanExtId: str, token: str):
    response = get_client_info_api(loanExtId, token)
    if response['status'] == "SUCCESS":
        try:
            if response['result']['resident'] == True:
                response_message = (
                f"👤 Имя клиента: {response['result']['payerName']}\n"
                f"🗓 Дата рождения: {response['result']['birthday']}\n"
                f"🆔 ID Кредита: {response['result']['extId']}\n"
                f"📓 ПИНФЛ: {response['result']['pinfl']}\n"
                f"📱 Номер телефона: {response['result']['payerPhone']}\n"
                f"🤵🏼‍Резидент: ✅\n"
                )
                return response_message
            else:
                response_message = (
                    f"👤 Имя клиента: {response['result']['payerName']}\n"
                    f"🗓 Дата рождения: {response['result']['birthday']}\n"
                    f"🆔 ID Кредита: {response['result']['extId']}\n"
                    f"📓 ПИНФЛ: {response['result']['pinfl']}\n"
                    f"📱 Номер телефона: {response['result']['payerPhone']}\n"
                    f"🤵🏼‍Резидент: ❌\n"
                )
                return response_message
        except Exception as e:
            return e


async def get_client_balance(loanExtId: str, token: str):
    response = get_client_balance_api(loanExtId, token)
    if response['status'] == "SUCCESS":
        try:
            transit_balance = response['result']['transit_balance']
            max_amount = response['result']['max_amount']
            recommended_amount = response['result']['recommended_amount']

            def format_amount(amount):
                soms = amount // 100
                tins = amount % 100
                return f"{soms:,}".replace(',', ' ') + f".{tins:02d}"

            formatted_balance = format_amount(transit_balance)
            formatted_max_amount = format_amount(max_amount)
            formatted_recommended_amount = format_amount(recommended_amount)
            response_message = (
                f"🏦 Транзитный счёт: {response['result']['transit_account']}\n"
                f"💰 Максимальная сумма: {formatted_max_amount}\n"
                f"💵 Рекомендуемая сумма: {formatted_recommended_amount}\n"
                f"💳 Баланс на транзитном счете: {formatted_balance}\n"
            )
            return response_message
        except Exception as e:
            return e



async def get_client_location(loanExtId: str, token: str):
    keyboard = InlineKeyboardBuilder()
    response = get_client_location_api(loanExtId, token)

    if response['status'] == "SUCCESS":
        locations = response["result"].get("data")

        if not locations:
            return "Локации нету"

        for location in locations:
            location_id = location.get("id")
            description = location.get("description")
            latitude = location.get("latitude")
            longitude = location.get("longitude")
            keyboard.add(InlineKeyboardButton(text=description, callback_data=f'location_id:{location_id}:{latitude}:{longitude}'))

        return keyboard.adjust(2).as_markup()

    return "Не удалось получить локации"




