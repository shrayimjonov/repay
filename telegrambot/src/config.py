import os
from dotenv import load_dotenv
import hashlib

load_dotenv()

TOKEN = os.getenv('TOKEN')
CACHE = os.getenv('CACHE')

def hash_token(token):
    return hashlib.sha256(token.encode()).hexdigest()

TGToken = hash_token(TOKEN)

