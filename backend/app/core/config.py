from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    PROJECT_NAME: str = "TripDirect Backend"
    API_V1_STR: str = "/api/v1"

    # Environment variables required by specification
    DATABASE_URL: str = "sqlite:///./tripdirect.db"
    JWT_SECRET_KEY: str = "dev_secret_key_tripdirect_987654321_super_secure_key"
    JWT_ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 60

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )


settings = Settings()
